package com.github.dreamroute.me.server.config;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.github.dreamroute.me.sdk.common.Adapter;
import com.github.dreamroute.me.sdk.common.CallBack;
import com.github.dreamroute.me.sdk.common.Type;
import com.github.dreamroute.me.server.entity.BaseEntity;
import com.github.dreamroute.me.server.entity.Operation;
import com.github.dreamroute.me.server.exception.MeException;
import com.github.dreamroute.me.server.service.CrudService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author w.dehai
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "20200109", consumerGroup = "me-group")
public class Consumer implements RocketMQListener<String> {

    @Autowired
    private CrudService crudService;
    @Autowired
    private RestTemplate rest;

    private static final ReentrantLock lock = new ReentrantLock();

    @Override
    public void onMessage(String message) {

        log.info("MQ收到的消息记录: {}", message);

        Operation opt = JSON.parseObject(message, Operation.class);
        String database = opt.getDatabase();
        String tableName = opt.getTable();
        
        // 过滤不需要处理的表
        if (!ConfigStore.TABLE_NAME.contains(database + '.' + tableName)) {
            return;
        }
        
        Long platformId = ConfigStore.getPlatformIdByDatabaseName(database);
        Map<String, Adapter> adapterMap = ConfigStore.getAdapterByPlatformId(platformId);
        if (adapterMap == null || adapterMap.isEmpty()) {
            throw new MeException("此异常无需解决，业务系统已经下线，抛异常避免消息被消费");
        }
        Adapter adapter = adapterMap.get(tableName);
        log.info("Adapter: " + JSON.toJSONString(adapter, true));

        // 处理ddl语句
        if (opt.isDdl()) {
            processDdl(opt, adapter.getIndex());
            return;
        }

        // 处理数据
        log.info("开始进行数据处理");
        String[] data = processData(opt, platformId, adapter);
        log.info("处理之后的数据: " + JSON.toJSONString(data));
        // 此处很关键，data == null说明远程回调全部失败，表示客户端已经下线，需要抛出异常，避免消息被消费掉
        if (data == null || data.length == 0) {
            throw new MeException("此异常无需解决，处理需要回调的数据完成，但是得到的数据结果为空，可能是回调业务端出错或者是业务端下线了，此处抛出异常，等待下一次继续消费此消息");
        }

        // crud
        String index = adapter.getIndex();
        log.info("开始进行INSERT/UPDATA/DELETE操作");
        for (String d : data) {
            if (Objects.equals(opt.getType(), "INSERT")) {
                createIndexIfAbsent(platformId, index);
                crudService.insert(d, index);
            } else if (Objects.equals(opt.getType(), "UPDATE")) {
                crudService.update(d, index);
            } else if (Objects.equals(opt.getType(), "DELETE")) {
                crudService.delete(JSON.parseObject(d, BaseEntity.class).getId(), index);
            }
        }
        log.info("INSERT/UPDATA/DELETE操作完成");
    }

    /**
     * 如果索引缺失就先创建一个索引
     */
    private void createIndexIfAbsent(Long platformId, String index) {
        try {
            lock.lock();
            boolean exist = crudService.exist(index);
            if (!exist) {
                Map<String, Object> mapping = ConfigStore.getMappingByPlatformId(platformId).get(index);
                if (mapping == null || mapping.isEmpty()) {
                    throw new MeException("平台ID为" + platformId + "的平台的索引" + index + "的Mapping不存在");
                }
                crudService.putMapping(index, mapping);
            }
        } finally {
            lock.unlock();
        }
    }

    private String[] processData(Operation opt, Long platformId, Adapter adapter) {
        // 此处定义为null，如果isRefactor=true内的所有远程调用均失败，那么data == null，上游需要对此null进行异常抛出，避免消息被消费掉
        String[] data = null;
        if (adapter.isRefactor()) {
            Set<String> hosts = ConfigStore.getHostsByPlatformId(platformId);
            if (hosts == null || hosts.isEmpty()) {
                log.info("ID为{}的平台未注册到ME平台, 数据无法被同步到ES");
                throw new MeException("此异常无需解决，抛出异常，避免消息被成功ack");
            } else {
                CallBack cb = new CallBack(opt.getTable(), opt.getData(), Type.valueOf(opt.getType()));
                for (String host : hosts) {
                    try {
                        ResponseEntity<String[]> resp = rest.postForEntity("http://" + host + "/me/callback", cb, String[].class);
                        if (resp.getStatusCodeValue() == 200) {
                            data = resp.getBody();
                            break;
                        } else {
                            log.info("ID为{}的平台的host={}回调错误, 返回值状态不为200", platformId, host);
                        }
                    } catch (RestClientException e) {
                        log.info("ID为{}的平台的host={}回调异常", platformId, host);
                    }
                }
            }
        } else {
            data = opt.getData();
        }
        return data;
    }

    private void processDdl(Operation opt, String index) {
        if (Objects.equals(opt.getType(), "TRUNCATE")) {
            crudService.deleteIndex(index);
        }
    }

}