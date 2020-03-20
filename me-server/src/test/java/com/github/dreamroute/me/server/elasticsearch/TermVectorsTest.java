package com.github.dreamroute.me.server.elasticsearch;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.TermVectorsRequest;
import org.elasticsearch.client.core.TermVectorsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class TermVectorsTest {
    
    @Autowired
    private RestHighLevelClient client;
    
    @Test
    public void baseTest() throws Exception {
        TermVectorsRequest request = new TermVectorsRequest("user", String.valueOf(37L)); 
        request.setFields("name");
        TermVectorsResponse resp = client.termvectors(request, RequestOptions.DEFAULT);
        log.info(JSON.toJSONString(resp));
    }
    
}
