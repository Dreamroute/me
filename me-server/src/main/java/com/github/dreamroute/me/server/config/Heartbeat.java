package com.github.dreamroute.me.server.config;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.github.dreamroute.me.sdk.common.Const;

import lombok.extern.slf4j.Slf4j;

/**
 * @author w.dehai
 */
@Slf4j
@Component
public class Heartbeat {

    @Autowired
    private RestTemplate rest;

    /**
     * 检查客户端是否还存在，如果超过20秒不存在，直接踢出
     */
    @Scheduled(cron = "1/20 * * * * ?")
    public void checkClient() {
        Map<Long, Set<String>> clientInfo = ConfigStore.CONN;
        if (clientInfo != null && !clientInfo.isEmpty()) {
            for (Map.Entry<Long, Set<String>> client : clientInfo.entrySet()) {
                Long platformId = client.getKey();
                Set<String> hosts = client.getValue();
                if (hosts != null && !hosts.isEmpty()) {
                    Iterator<String> it = hosts.iterator();
                    while (it.hasNext()) {
                        String host = it.next();
                        String uri = "http://" + host + "/me/heart";
                        log.info("me心跳检测客户端, 客户端平台ID: {}, 客户端IP地址: {}", platformId, host);
                        try {
                            ResponseEntity<Boolean> resp = rest.postForEntity(uri, null, Boolean.class);
                            if (resp.getStatusCodeValue() != Const.HTTP_SUCCESS || !Objects.equals(resp.getBody(), Boolean.TRUE)) {
                                log.info("ID为{}的平台已经下线", platformId);
                                it.remove();
                            }
                        } catch (RestClientException e) {
                            log.info("ID为{}的平台已经下线", platformId);
                            log.error("" + e, e);
                            it.remove();
                        }
                    }
                }
            }
        }
    }
}
