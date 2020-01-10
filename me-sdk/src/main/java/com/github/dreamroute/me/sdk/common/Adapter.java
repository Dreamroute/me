package com.github.dreamroute.me.sdk.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author w.dehai
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Adapter {
    
    private String tableName;
    private String index;
    private boolean refactor;
    private String mapping;

}
