package com.github.dreamroute.me.server.entity;

import org.elasticsearch.action.delete.DeleteResponse;

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
public class DeleteResp extends BaseResp {

    /**
     * ES原生返回值
     */
    private DeleteResponse resp;

}
