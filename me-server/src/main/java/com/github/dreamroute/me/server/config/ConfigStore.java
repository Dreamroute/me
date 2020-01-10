package com.github.dreamroute.me.server.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.dreamroute.me.sdk.common.Adapter;
import com.vip.vjtools.vjkit.collection.type.ConcurrentHashSet;

/**
 * @author w.dehai
 */
public class ConfigStore {

    private ConfigStore() {}

    /**
     * <key=库名, value=平台id>
     */
    static final Map<String, Long> DATABASENAME_PLATFORMID = new ConcurrentHashMap<>();
    
    /**
     * 所有表名，格式为databaseName.tableName
     */
    static final ConcurrentHashSet<String> TABLE_NAME = new ConcurrentHashSet<>();

    /**
     * <key=平台id, value=<key=表名, value=对应表的配置信息>>
     * 多个平台会进行并发注册, 所以使用ConcurrentHashMap, 内部Map是全量替换的, HashMap即可
     */
    static final Map<Long, Map<String, Adapter>> PLATFORMID_ADAPTER = new ConcurrentHashMap<>();

    /**
     * <key=platormId, value=Set<host:port>>
     * 多个平台会进行并发注册, 所以使用ConcurrentHashMap, 内部Set在做心跳检测时可能会移除部分值，所以需要ConcurrentHashSet
     */
    static final Map<Long, Set<String>> CONN = new ConcurrentHashMap<>();
    
    /**
     * <key=platformId, value=<表名, mapping>>
     */
    static final Map<Long, Map<String, Map<String, Object>>> MAPPING_MAP = new ConcurrentHashMap<>();
    
    public static Long getPlatformIdByDatabaseName(String databaseName) {
        return DATABASENAME_PLATFORMID.get(databaseName);
    }

    /**
     * 根据平台id获取表配置信息
     */
    public static Map<String, Adapter> getAdapterByPlatformId(Long platformId) {
        return ConfigStore.PLATFORMID_ADAPTER.get(platformId);
    }

    /**
     * 根据平台id设置表配置信息
     */
    public static void setPlatformIdAdapter(Long platformId, Map<String, Adapter> adapter) {
        ConfigStore.PLATFORMID_ADAPTER.put(platformId, adapter);
    }

    /**
     * 缓存平台id和对应的host
     */
    public static void cache(Long platformId, String uri) {
        CONN.computeIfAbsent(platformId, key -> {
            Set<String> adapter = new ConcurrentHashSet<>();
            adapter.add(uri);
            return adapter;
        }).add(uri);
    }

    /**
     * 根据平台id获取平台的host集合
     */
    public static Set<String> getHostsByPlatformId(Long platformId) {
        return CONN.get(platformId);
    }
    
    /**
     * 缓存mapping
     */
    public static void cacheMapping(Long platformId, Map<String, Map<String, Object>> mapping) {
        MAPPING_MAP.put(platformId, mapping);
    }
    
    /**
     * 根据平台id获取mapping
     */
    public static Map<String, Map<String, Object>> getMappingByPlatformId(Long platformId) {
        return MAPPING_MAP.get(platformId);
    }

}
