package com.github.dreamroute.me.sdk.common;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Data;

/**
 * @author w.dehai
 */
@Data
public class CallBack implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5668112096129178970L;
    
    private static final AtomicLong INVOKE_ID = new AtomicLong(1);
    
    private long id;
    private String tableName;
    private String[] data;
    private Type type;
    
    public CallBack() {
        this.id = newId();
    }
    
    private static long newId() {
        // getAndIncrement() When it grows to MAX_VALUE, it will grow to MIN_VALUE, and the negative can be used as ID
        return INVOKE_ID.getAndIncrement();
    }

}
