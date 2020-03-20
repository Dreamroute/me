package com.github.dreamroute.me.server.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.dreamroute.me.server.entity.Platform;
/**
 * @author w.dehai
 */
@SpringBootTest
public class PlatformMapperTest {
    
    @Autowired
    private PlatformMapper platformMapper;
    
    @Test
    public void insertTest() {
        Platform platform = new Platform();
        platform.setName("测试平台");
        int result = platformMapper.insert(platform);
        assertEquals(1, result);
    }
}
