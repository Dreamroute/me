package com.github.dreamroute.me.sdk.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author w.dehai
 */
@RestController
public class HeartbeatController {

    @PostMapping("/me/heart")
    public boolean heart() {
        return true;
    }
    
}
