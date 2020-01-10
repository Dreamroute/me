package com.github.dreamroute.me.sdk.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.alibaba.fastjson.JSON;
import com.github.dreamroute.me.sdk.common.Adapter;
import com.github.dreamroute.me.sdk.common.Config;
import com.github.dreamroute.me.sdk.common.IpUtil;

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

    @Value("${server.port}")
    private int port;

    @Scheduled(cron = "1/5 * * * * ?")
    public void register() {
        validateMapping(config);
        config.setHeartbeatPort(port);
        try {
            int result = configResource.registryConfig(config);
            if (result == 0) {
                log.info("注册到ME成功");
            }
        } catch (Exception e) {
            log.error("注册到ME失败" + e, e);
        }
    }

    private void validateMapping(Config config) {
        
        String ip = IpUtil.getIp();
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
