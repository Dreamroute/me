package com.github.dreamroute.me.sdk.common;

/**
 * @author w.dehai
 */
public class Const {

    private Const() {}

    public static final String RESP_SUCCESS = "0";

    public static final int HTTP_SUCCESS = 200;

    public enum HeartbeatMessage {
        
        PING("PING"), PONG("PONG");
        
        private String message;

        HeartbeatMessage(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return this.message;
        }
    }

}
