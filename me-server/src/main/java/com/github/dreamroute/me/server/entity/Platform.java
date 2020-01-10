package com.github.dreamroute.me.server.entity;

import java.util.List;

import com.bdfint.base.common.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
/**
 * @author w.dehai
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Platform extends BaseEntity {
    
    private String name;
    private List<DatabaseInfo> databaseInfoList;

}
