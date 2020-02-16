package com.github.dreamroute.me.sdk.netty.codec;

import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * 服务端客户端都使用Sharable
 * 
 * @author w.dehai
 */
@Sharable
@Component
public class Encoder extends ObjectEncoder {}
