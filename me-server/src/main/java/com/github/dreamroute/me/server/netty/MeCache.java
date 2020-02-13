package com.github.dreamroute.me.server.netty;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.github.dreamroute.me.sdk.common.CallBack;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 将客户端IP和ChannelHandlerContext缓存，用于服务端主动向客户端发送消息
 * 
 * @author w.dehai
 */
@Slf4j
public class MeCache {
    
    private MeCache() {}
    
    /**
     * 每一个ip对应一个通道，由于k8's使用的是ip池，可能被重复利用，所以这里每次客户端新创建一个通道就直接调用map的put方法，
     * 把之前的ip给替换掉
     * 由于不存在不同应用对应同一个ip地址，所以这里的实现没有问题，如果有不同应用对应同一个ip，那么这种做法是不行的，会出现
     * 同一个ip对应多个通道冲突问题
     */
    public static final Map<Long, Set<ChannelHandlerContext>> CTX_MAP = new ConcurrentHashMap<>();
    
    /**
     * 缓存同步调用线程
     */
    public static final Cache<Long, MeFuture<CallBack>> THREAD_WAITE_CACHE = CacheBuilder.newBuilder()
            .initialCapacity(20)
            .maximumSize(100000)
            .concurrencyLevel(30)
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .removalListener(notification -> log.info("key: {}被移除", String.valueOf(notification.getKey())))
            .build();

}
