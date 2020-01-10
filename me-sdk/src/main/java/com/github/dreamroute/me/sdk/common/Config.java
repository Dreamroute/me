package com.github.dreamroute.me.sdk.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author w.dehai
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Config {
    
    private String heartbeatIp;
    private int heartbeatPort;
    private Long platformId;
    private List<Adapter> adapter;

}
