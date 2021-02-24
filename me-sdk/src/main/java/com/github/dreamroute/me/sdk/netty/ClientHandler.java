package com.github.dreamroute.me.sdk.netty;

import com.alibaba.fastjson.JSON;
import com.github.dreamroute.me.sdk.common.CallBack;
import com.github.dreamroute.me.sdk.common.CallBackProcessor;
import com.github.dreamroute.me.sdk.common.IpUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 客户端心跳handler，断线不重连（可能是由于服务端重新发布，IP地址会更改）
 * 
 * @author w.dehai
 */
@Slf4j
@Sharable
@Component
public class ClientHandler extends ChannelInboundHandlerAdapter {
    
    @Autowired
    private CallBackProcessor processor;
    
    @Value("${me.platformId}")
    private Long platformId;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        CallBack cb = (CallBack) msg;
        log.info("客户端收到的回调信息: {}", JSON.toJSONString(cb, true));
        String[] data = processor.process(cb);
        cb.setData(data);
        ctx.writeAndFlush(cb);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.ALL_IDLE) {
                log.info("客户端触发心跳, 时间: {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
                ctx.writeAndFlush(new Addr(platformId, String.valueOf(IpUtil.getIp())));
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.close();
        log.info("服务端下线, 客户端与服务端断开连接, 移除客户端netty client, 服务端上线之后重新创建客户端(重新创建而不是重连是因为服务端IP地址可能会发生变化).");
    }

}
