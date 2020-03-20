package com.github.dreamroute.me.server.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.yaml.snakeyaml.Yaml;

import com.alibaba.fastjson.JSON;
import com.github.dreamroute.me.sdk.common.Adapter;
import com.github.dreamroute.me.sdk.common.Config;

import lombok.extern.slf4j.Slf4j;
/**
 * @author w.dehai
 */
@Slf4j
@SpringBootTest
public class ConfigControllerTest {
    
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    
    @Test
    public void configTest() throws Exception {
        
        Yaml yml = new Yaml();
        Map<String, Object> src = yml.load(new ClassPathResource("/application.yml").getInputStream());
        @SuppressWarnings("unchecked") Map<String, Object> me = (Map<String, Object>) src.get("me");
        @SuppressWarnings("unchecked") List<Map<String, Object>> ads = (List<Map<String, Object>>) me.get("adapter");
        List<Adapter> adapterList = new ArrayList<>(ads.size());
        if (!ads.isEmpty()) {
            adapterList = ads.stream().map(e -> Adapter.builder()
                    .tableName(String.valueOf(e.get("tableName")))
                    .index(String.valueOf(e.get("index")))
                    .refactor((boolean) e.get("refactor")).build())
                    .collect(Collectors.toList());
        }
        
        Long platformId = Long.valueOf((Integer) me.get("platformId"));
        Config config = Config.builder().platformId(platformId).adapter(adapterList).build();

        MvcResult result = mockMvc.perform(post("/config")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(config)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        log.info(result.getResponse().getContentAsString());
    }

}
