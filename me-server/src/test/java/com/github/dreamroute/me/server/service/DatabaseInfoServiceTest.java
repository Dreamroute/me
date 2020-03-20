package com.github.dreamroute.me.server.service;

import com.github.dreamroute.me.server.entity.DatabaseInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
