package com.github.dreamroute.me.server.service;

import com.github.dreamroute.me.server.entity.DeleteResp;
import com.github.dreamroute.me.server.entity.InsertResp;
import com.github.dreamroute.me.server.entity.UpdateResp;
import com.github.dreamroute.me.server.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author w.dehai
 */
@SpringBootTest
public class CrudServiceTest {
    
    @Autowired
    private CrudService crudService;
    
    @Test
    public void insertTest() {
        User user = User.builder()
                .id(100L)
                .password("1234561")
                .email("wangdehai@bdfint.com1").build();
        InsertResp resp = crudService.insert(user, "user");
        assertEquals(true, resp.isAllshardSuccess());
    }
    
    @Test
    public void updateTest() {
        User user = User.builder()
                .id(100L)
                .build();
        UpdateResp resp = crudService.update(user, "user");
        assertEquals(true, resp.isAllshardSuccess());
    }
    
    @Test
    public void deleteTest() {
        DeleteResp resp = crudService.delete(102L, "user");
        assertEquals(true, resp.isAllshardSuccess());
    }
    
    @Test
    public void insertOrderTest() {
        Order order = Order.builder().id(1L).totalPrice(new BigDecimal("22.3")).num(5).build();
        int len = 3;
        List<Goods> goodsList = new ArrayList<>(3);
        for (int i=0; i<len; i++) {
            goodsList.add(Goods.builder().id(Long.valueOf(i)).name("computer" + i).price(new BigDecimal("15.2" + i)).build());
        }
        order.setGoodsList(goodsList);
        order.setBuyer(Buyer.builder().name("w.dehai").age(30).build());
        InsertResp resp = crudService.insert(order, "order");
        assertEquals(true, resp.getSuccess());
    }
    
}
