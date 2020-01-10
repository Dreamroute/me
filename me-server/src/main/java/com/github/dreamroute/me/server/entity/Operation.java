package com.github.dreamroute.me.server.entity;

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
public class Operation {

    private String[] data;
    private String database;
    private String es;
    private boolean isDdl;
    private String[] old;
    private String[] pkNames;
    private String sql;
    private String table;
    private Long ts;
    private String type;

}
