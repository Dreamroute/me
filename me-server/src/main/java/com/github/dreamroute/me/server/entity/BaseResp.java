package com.github.dreamroute.me.server.entity;

import org.elasticsearch.action.support.replication.ReplicationResponse.ShardInfo.Failure;

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
public class BaseResp {

    /**
     * 是否所有shard成功
     */
    private boolean allshardSuccess;

    /**
     * shard总数
     */
    private int total;

    /**
     * 成功的shard数量
     */
    private int success;

    /**
     * 失败的shard数量
     */
    private int faild;

    /**
     * 失败的shard的失败原因
     */
    private Failure[] failures;
    
}
