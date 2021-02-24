package com.github.dreamroute.me.sdk.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.dreamroute.me.sdk.common.Adapter;
import com.github.dreamroute.me.sdk.common.Config;
import com.github.dreamroute.me.sdk.netty.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Objects;

/**
 * 将本地配置文件上报到me服务器，使用定时任务而不是CommandLineRunner的方式可以实现服务端掉线再上线之后的重连 原理：解析application.yml文件，将解析结果使用RestTemplate上报到服务器
 * 
 * @author w.dehai
 */
@Slf4j
@Configuration
public class MeRegister {

    @Autowired
    private Client client;
    @Autowired
    private Config config;
    @Autowired
    private ConfigResource configResource;

    private String existServerIp;
    private boolean existClient;

    @Scheduled(cron = "1/10 * * * * ?")
    public void register() {

        // 检查客户端配置文件是否符合要求
        validateMapping(config);

        String serverIp;
        try {
            serverIp = configResource.registryConfig(config);
            if (serverIp == null || serverIp.length() == 0) {
                log.info("服务端已下线");
                return;
            }
            log.info("上报配置到ME成功");
            if (existServerIp == null || !Objects.equals(serverIp, existServerIp)) {
                existServerIp = serverIp;
                existClient = false;
            }

            // 创建长连接客户端
            createClient(serverIp);
        } catch (Exception e) {
            log.error("上报配置到ME失败" + e, e);
            existClient = false;
        }

    }

    private void createClient(String serverIp) {
        if (!existClient) {
            client.createClient(serverIp);
            existClient = true;
        }
    }

    private void validateMapping(Config config) {

        Long platformId = config.getPlatformId();
        if (platformId == null) {
            log.error("配置文件中缺少me.platformId配置");
        }

        List<Adapter> ads = config.getAdapter();
        if (ads == null || ads.isEmpty()) {
            log.error("配置文件中缺少me.adapter配置");
        }

        for (Adapter ad : ads) {
            String mapping = ad.getMapping();
            if (mapping == null || mapping.length() == 0) {
                log.error("配置文件中缺少me.mapping配置");
            }
            if (!isJson(mapping)) {
                log.error("配置文件中me.mapping格式不正确，需要符合JSON格式");
            }

        }
    }

    /**
     * 格式不对，或者为空都报错
     */
    public static boolean isJson(String content) {
        try {
            JSONObject jo = JSON.parseObject(content);
            return !jo.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

}
