package com.github.dreamroute.me.sdk.common;

/**
 * @author w.dehai
 */
public enum Type {

    /**
     * 新增
     */
    INSERT("INSERT"),

    /**
     * 修改
     */
    UPDATE("UPDATE"),

    /**
     * 删除
     */
    DELETE("DELETE");

    private String code;

    private Type(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
