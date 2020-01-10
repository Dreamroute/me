package com.github.dreamroute.me.sdk.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.dreamroute.me.sdk.common.Config;

/**
 * @author w.dehai
 */
@FeignClient(value = "me")
public interface ConfigResource {

    @RequestMapping("/registryConfig")
    public int registryConfig(@RequestBody Config config);

}
