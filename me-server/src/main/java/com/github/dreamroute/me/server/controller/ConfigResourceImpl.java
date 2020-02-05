package com.github.dreamroute.me.server.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.dreamroute.me.sdk.common.Adapter;
import com.github.dreamroute.me.sdk.common.Config;
import com.github.dreamroute.me.sdk.common.IpUtil;
import com.github.dreamroute.me.sdk.controller.ConfigResource;
import com.github.dreamroute.me.server.config.ConfigStore;

/**
 * @author w.dehai
 */
@RestController
public class ConfigResourceImpl implements ConfigResource {

    @Override
    public String registryConfig(@RequestBody Config config) {

        // 缓存客户端连接
        cacheConn(config);

        // 缓存mapping
        cacheMapping(config);

        // 缓存表配置信息
        cacheAdapter(config);
        
        // 获取本地IP并返回本地IP
        return IpUtil.getIp();
    }

    private void cacheAdapter(Config config) {
        Map<String, List<Adapter>> tableNameAdapter = config.getAdapter().stream().collect(Collectors.groupingBy(Adapter::getTableName));
        Map<String, Adapter> adapterMap = new ConcurrentHashMap<>(16);
        tableNameAdapter.forEach((tableName, adapter) -> adapterMap.put(tableName, adapter.get(0)));
        ConfigStore.setPlatformIdAdapter(config.getPlatformId(), adapterMap);
    }

    private void cacheMapping(Config config) {
        Map<String, Map<String, Object>> mp = new ConcurrentHashMap<>();
        List<Adapter> ads = config.getAdapter();
        if (ads != null && !ads.isEmpty()) {
            ads.forEach(ad -> {
                String mapping = ad.getMapping();
                JSONObject map = JSON.parseObject(mapping);
                Map<String, Object> m = createMapping(map);
                mp.put(ad.getIndex(), m);
            });
        }
        ConfigStore.cacheMapping(config.getPlatformId(), mp);
    }

    private void cacheConn(Config config) {
        ConfigStore.cache(config.getPlatformId(), config.getHeartbeatIp() + ":" + config.getHeartbeatPort());
    }

    private Map<String, Object> createMapping(JSONObject map) {
        Map<String, Map<String, Object>> properties = new HashMap<>();
        if (map != null && !map.isEmpty()) {
            map.forEach((propName, type) -> {
                Map<String, Object> m = new HashMap<>();
                m.put("type", type);
                properties.put(propName, m);
            });
        }

        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);

        return mapping;
    }

}
