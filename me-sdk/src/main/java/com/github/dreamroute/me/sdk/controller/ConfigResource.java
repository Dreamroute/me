package com.github.dreamroute.me.sdk.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.dreamroute.me.sdk.common.Config;

/**
 * @author w.dehai
 */
@FeignClient(value = "${me.server.name}")
public interface ConfigResource {

    @PostMapping("/registryConfig")
    public String registryConfig(@RequestBody Config config);

}
