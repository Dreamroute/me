package com.github.dreamroute.me.sdk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.dreamroute.me.sdk.common.Config;

/**
 * @author w.dehai
 */
@Configuration
public class ConfigBean {
    
    @Bean
    @ConfigurationProperties(prefix = "me")
    public Config config() {
        return new Config();
    }

}
