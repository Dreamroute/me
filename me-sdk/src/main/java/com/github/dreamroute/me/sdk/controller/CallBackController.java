package com.github.dreamroute.me.sdk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.dreamroute.me.sdk.common.CallBack;
import com.github.dreamroute.me.sdk.common.CallBackProcessor;

/**
 * @author w.dehai
 */
@RestController
public class CallBackController {
    
    @Autowired
    private CallBackProcessor processor;
    
    @PostMapping("/me/callback")
    public String[] callback(@RequestBody CallBack callback) {
        return processor.process(callback);
    }

}
