package com.github.dreamroute.me.sdk.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.alibaba.fastjson.JSON;
import com.github.dreamroute.me.sdk.common.Adapter;
import com.github.dreamroute.me.sdk.common.Config;
import com.github.dreamroute.me.sdk.common.IpUtil;
import com.github.dreamroute.me.sdk.netty.Client;

import lombok.extern.slf4j.Slf4j;

/**
 * 将本地配置文件上报到me服务器，使用定时任务而不是CommandLineRunner的方式可以实现服务端掉线再上线之后的重连 原理：解析application.yml文件，将解析结果使用RestTemplate上报到服务器
 * 
 * @author w.dehai
 */
@Slf4j
@Configuration
public class MeRegister {

    @Autowired
    private Config config;
    @Autowired
    private ConfigResource configResource;
    @Autowired
    private Client client;

    @Value("${server.port}")
    private int port;
    
    @Value("${me.client.ip:error}")
    private String ip;
    
    private String existServerIp;
    private boolean existClient;

    @Scheduled(cron = "1/10 * * * * ?")
    public void register() {
        validateMapping(config);
        config.setHeartbeatPort(port);
        String serverIp = null;
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
        if (existClient)
            return;
        client.createClient(serverIp);
        existClient = true;
    }

    private void validateMapping(Config config) {
        
        if (Objects.equals(ip, "error"))
            ip = IpUtil.getIp();
        config.setHeartbeatIp(ip);

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

    public static boolean isJson(String content) {
        try {
            JSON.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
