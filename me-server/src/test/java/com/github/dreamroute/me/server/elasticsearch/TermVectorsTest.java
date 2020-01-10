package com.github.dreamroute.me.server.elasticsearch;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.TermVectorsRequest;
import org.elasticsearch.client.core.TermVectorsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TermVectorsTest {
    
    @Autowired
    private RestHighLevelClient client;
    
    @Test
    public void baseTest() throws Exception {
        TermVectorsRequest request = new TermVectorsRequest("user", String.valueOf(37L)); 
        request.setFields("name");
        TermVectorsResponse resp = client.termvectors(request, RequestOptions.DEFAULT);
        System.err.println(resp);
    }
    
}
