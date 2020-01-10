package com.github.dreamroute.me.sdk.common;

/**
 * @author w.dehai
 */
public interface CallBackProcessor {

    /**
     * 对于数据库变回调接口，客户端可以进行适当的改变
     * 
     * @param callback 数据变动数据相关
     * @return 客户端改变之后回传的JSON数组
     */
    String[] process(CallBack callback);
    
}
