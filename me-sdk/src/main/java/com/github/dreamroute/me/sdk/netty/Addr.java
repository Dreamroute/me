package com.github.dreamroute.me.sdk.netty;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 4792425917218848642L;
    
    private String clientIp;

}
