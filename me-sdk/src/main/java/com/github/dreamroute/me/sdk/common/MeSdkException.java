package com.github.dreamroute.me.sdk.common;

public class MeSdkException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 3336426874093779819L;
    
    public MeSdkException(String message) {
        super(message);
    }

    public MeSdkException(String message, Throwable cause) {
        super(message, cause);
    }

}
