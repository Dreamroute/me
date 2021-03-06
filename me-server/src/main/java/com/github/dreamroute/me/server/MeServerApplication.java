package com.github.dreamroute.me.server;

import com.bdfint.base.config.DataSourceConfig;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.github.dreamroute.me.sdk.config.ESClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author w.dehai
 */
@RestController
@EnableScheduling
@EnableFeignClients
@EnableApolloConfig
@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@EnableTransactionManagement
@MapperScan(basePackages = {"com.github.dreamroute.me.server.mapper"})
@ComponentScan(basePackageClasses = ESClientConfig.class, includeFilters = {@Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ESClientConfig.class})}, useDefaultFilters = false)
@ComponentScan(basePackageClasses = DataSourceConfig.class, includeFilters = {@Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {DataSourceConfig.class})}, useDefaultFilters = false)
public class MeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeServerApplication.class, args);
    }
    
    @GetMapping("/health")
    public boolean health() {
        return true;
    }

}
