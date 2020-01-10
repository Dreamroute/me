package com.github.dreamroute.me.server.service;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.dreamroute.me.server.entity.DatabaseInfo;
import com.github.dreamroute.me.server.service.DatabaseInfoService;

/**
 * @author w.dehai
 */
@SpringBootTest
public class DatabaseInfoServiceTest {

    @Autowired
    private DatabaseInfoService databaseInfoService;

    @Test
    public void listAllTest() {
        List<DatabaseInfo> all = databaseInfoService.listAll();
        assertNotEquals(0, all.size());
    }

}
