package com.github.dreamroute.me.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;

@EnableScheduling
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@EnableFeignClients(basePackages = {"com.github.dreamroute.me.sdk"})
@ComponentScan(basePackages = {"com.github.dreamroute.me.client", "com.github.dreamroute.me.sdk"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class MeClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeClientApplication.class, args);
    }

    @GetMapping("/health")
    public boolean health() {
        return true;
    }

}
