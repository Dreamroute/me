package com.github.dreamroute.me.sdk.common;

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
public class CallBack {
    
    private String tableName;
    private String[] data;
    private Type type;

}
