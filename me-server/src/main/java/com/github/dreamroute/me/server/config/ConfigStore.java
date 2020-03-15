package com.github.dreamroute.me.server.config;

import com.github.dreamroute.me.sdk.common.Adapter;
import com.vip.vjtools.vjkit.collection.type.ConcurrentHashSet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
     * <key=platformId, value=<表名, mapping>>
     */
    static final Map<Long, Map<String, Map<String, Object>>> MAPPING_MAP = new ConcurrentHashMap<>();

    /**
     * <key=平台id, value=<key=表名, value=对应表的配置信息>>
     * 多个平台会进行并发注册, 所以使用ConcurrentHashMap, 内部Map对于外层来说仅仅是个value, HashMap即可
     */
    static final Map<Long, Map<String, Adapter>> PLATFORMID_ADAPTER = new ConcurrentHashMap<>();
    
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
     * 根据平台id设置表配置信息, 由于客户端每个节点的配置都相同，所以这里直接覆盖
     */
    public static void cachePlatformIdAdapter(Long platformId, Map<String, Adapter> adapter) {
        ConfigStore.PLATFORMID_ADAPTER.put(platformId, adapter);
    }

    /**
     * 缓存mapping，由于客户端每个节点的配置都相同，所以这里直接覆盖
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
