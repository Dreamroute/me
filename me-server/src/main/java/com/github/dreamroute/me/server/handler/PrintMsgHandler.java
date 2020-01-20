package com.github.dreamroute.me.server.handler;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.dreamroute.me.server.service.CrudService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
@Component
public class PrintMsgHandler extends ChannelInboundHandlerAdapter {
    
    @Autowired
    private CrudService crudService;
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.err.println(buf.toString(Charset.forName("UTF-8")));
        System.err.println(crudService);
    }

}
