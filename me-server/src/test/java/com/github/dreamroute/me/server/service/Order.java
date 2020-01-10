package com.github.dreamroute.me.server.service;

import java.math.BigDecimal;
import java.util.List;

import com.github.dreamroute.me.server.entity.BaseEntity;

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
public class Order extends BaseEntity {
    
    private BigDecimal totalPrice;
    private int num;
    private List<Goods> goodsList;
    private Buyer buyer;

}
