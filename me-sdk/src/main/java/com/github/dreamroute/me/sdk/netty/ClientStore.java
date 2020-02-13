package com.github.dreamroute.me.sdk.netty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientStore {
    
    private ClientStore() {}
    
    public static final Map<Integer, Thread> CLIENT_THREAD = new ConcurrentHashMap<>();

}
