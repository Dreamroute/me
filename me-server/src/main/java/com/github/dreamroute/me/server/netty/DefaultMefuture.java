package com.github.dreamroute.me.server.netty;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.github.dreamroute.me.server.exception.MeException;

import lombok.Getter;
import lombok.Setter;

/**
 * 用于异步线程等待的工具类
 * 
 * @author w.dehai
 */
public class DefaultMefuture<T> implements MeFuture<T> {
    
    @Getter
    @Setter
    private T resp;
    private CountDownLatch cdl = new CountDownLatch(1);

    @Override
    public T get() {
        // 默认10秒
        return this.get(10, TimeUnit.SECONDS);
    }
    
    @Override
    public T get(long timeout) {
        return this.get(timeout, TimeUnit.SECONDS);
    }

    @Override
    public T get(long timeout, TimeUnit unit) {
        T result = null;
        try {
            if (cdl.await(timeout, unit))
                result = this.resp;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MeException("cdl等待中被Interrupt");
        }
        return result;
    }
    
    @Override
    public void set(T resp) {
        this.resp = resp;
        this.cdl.countDown();
    }

}
