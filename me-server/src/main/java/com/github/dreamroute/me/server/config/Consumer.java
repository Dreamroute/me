package com.github.dreamroute.me.server.config;

import com.alibaba.fastjson.JSON;
import com.github.dreamroute.me.sdk.common.Adapter;
import com.github.dreamroute.me.sdk.common.CallBack;
import com.github.dreamroute.me.sdk.common.Type;
import com.github.dreamroute.me.server.entity.BaseEntity;
import com.github.dreamroute.me.server.entity.Operation;
import com.github.dreamroute.me.server.exception.MeException;
import com.github.dreamroute.me.server.netty.DefaultMefuture;
import com.github.dreamroute.me.server.netty.MeCache;
import com.github.dreamroute.me.server.netty.MeFuture;
import com.github.dreamroute.me.server.service.CrudService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author w.dehai
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "20200109", consumerGroup = "me-group")
public class Consumer implements RocketMQListener<String> {

    private CrudService crudService;

    public Consumer(CrudService crudService) {
        this.crudService = crudService;
    }

    private static final ReentrantLock lock = new ReentrantLock();

    @Override
    public void onMessage(String message) {

        log.info("MQ收到的消息记录: {}", message);

        Operation opt = JSON.parseObject(message, Operation.class);
        String database = opt.getDatabase();
        String tableName = opt.getTable();
        String fullName = database + '.' + tableName;

        // 过滤不需要处理的表
        log.info("表名: {}", fullName);
        if (!ConfigStore.TABLE_NAME.contains(fullName)) {
            log.info("此表数据无需处理，直接返回");
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

    /**
     * // TODO 顺序问题
     */
    private String[] processData(Operation opt, Long platformId, Adapter adapter) {
        // 此处定义为data = null，如果isRefactor=true内的所有远程调用均失败，那么data == null，上游需要对此null进行异常抛出，避免消息被消费掉
        String[] data = null;
        if (adapter.isRefactor()) {
            Set<ChannelHandlerContext> ctxSet = MeCache.CTX_MAP.get(platformId);
            if (ctxSet == null || ctxSet.isEmpty()) {
                log.info("ID为{}的平台未注册到ME平台, 数据无法被同步到ES", platformId);
                throw new MeException("此异常无需解决，抛出异常，避免消息被成功ack");
            } else {
                Optional<ChannelHandlerContext> chc = ctxSet.stream().findAny();
                if (chc.isPresent()) {
                    ChannelHandlerContext ctx = chc.get();
                    CallBack cb = new CallBack();
                    cb.setTableName(opt.getTable());
                    cb.setData(opt.getData());
                    cb.setType(Type.valueOf(opt.getType()));
                    ctx.writeAndFlush(cb);
                    MeFuture<CallBack> mf = new DefaultMefuture<>();
                    MeCache.THREAD_WAITE_CACHE.put(cb.getId(), mf);
                    CallBack result = mf.get(10);
                    if (result == null)
                        throw new MeException("调用客户端返回了空, 可能的原因是客户端报错或者是客户端未处理该数据或者是调用超时，请求参数为: {}" + JSON.toJSONString(cb));
                    data = result.getData();
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