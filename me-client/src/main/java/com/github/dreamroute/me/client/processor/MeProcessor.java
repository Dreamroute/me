package com.github.dreamroute.me.client.processor;

import org.springframework.stereotype.Component;

import com.github.dreamroute.me.sdk.common.CallBack;
import com.github.dreamroute.me.sdk.common.CallBackProcessor;
import com.github.dreamroute.me.sdk.common.Type;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MeProcessor implements CallBackProcessor {

    @Override
    public String[] process(CallBack callback) {
        String table = callback.getTableName();
        Type type = callback.getType();
        String[] data = callback.getData();
        log.info("table = {}, type = {}, data = {}", table, type.getCode(), data);
        return data;
    }

}
