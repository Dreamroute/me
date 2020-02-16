package com.github.dreamroute.me.sdk.netty.codec;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;

/**
 * 服务端客户端都使用Sharable, 由于ObjectDecoder默认构造方法需要参数，而参数无法在super()之前，因此使用FactoryBean方式进行初始化
 * 
 * @author w.dehai
 */
@Sharable
@Component
public class Decoder implements FactoryBean<ObjectDecoder> {

    @Override
    public ObjectDecoder getObject() throws Exception {
        return new ObjectDecoder(1024 * 1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader()));
    }

    @Override
    public Class<?> getObjectType() {
        return ObjectDecoder.class;
    }

}
