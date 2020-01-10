package com.github.dreamroute.me.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author w.dehai
 */
@Configuration
public class CommonConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
