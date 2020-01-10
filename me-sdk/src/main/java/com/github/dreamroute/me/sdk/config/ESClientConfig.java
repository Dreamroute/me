package com.github.dreamroute.me.sdk.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.dreamroute.me.sdk.common.MeSdkException;

/**
 * @author w.dehai
 */
@Configuration
public class ESClientConfig {
    
    @Value("${es.addr}")
    private String esAddr;
    
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        
        if (esAddr == null || esAddr.length() == 0) {
            throw new MeSdkException("缺少ElasticSearch的es.addr配置，例如: '192.168.1.1:9200, 192.168.1.2:9200'");
        }
        
        List<HttpHost> hh = Arrays.stream(esAddr.split(","))
                .map(String::trim)
                .map(e -> e.split(":"))
                .map(e -> new HttpHost(e[0].trim(), Integer.parseInt(e[1].trim()), "http"))
                .collect(Collectors.toList());
        
        return new RestHighLevelClient(RestClient.builder(hh.toArray(new HttpHost[hh.size()])));
    }

}