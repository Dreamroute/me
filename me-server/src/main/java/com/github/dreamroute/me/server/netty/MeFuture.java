package com.github.dreamroute.me.server.netty;

import java.util.concurrent.TimeUnit;

public interface MeFuture<T> {

    T get();
    
    T get(long timeout);
    
    T get(long timeout, TimeUnit unit);
    
    void set(T resp);
    
}
