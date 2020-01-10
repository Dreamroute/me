package com.github.dreamroute.me.server.exception;
/**
 * @author w.dehai
 */
public class MeException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -1113481623118893985L;

    public MeException(String message) {
        super(message);
    }

    public MeException(String message, Throwable cause) {
        super(message, cause);
    }
}
