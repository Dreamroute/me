package com.github.dreamroute.me.server.elasticsearch;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReindexApiTest {
    
    @Autowired
    private RestHighLevelClient client;
    
    @Test
    public void baseTest() throws Exception {
        ReindexRequest request = new ReindexRequest();
        request.setSourceIndices("user");
        request.setDestIndex("uu");
        client.reindex(request, RequestOptions.DEFAULT);
    }
    
}
