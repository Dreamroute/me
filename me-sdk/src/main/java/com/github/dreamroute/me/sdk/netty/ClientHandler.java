package com.github.dreamroute.me.sdk.netty;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.github.dreamroute.me.sdk.common.Const;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端心跳handler，断线不重连（可能是由于服务端重新发布，IP地址会更改）
 * 
 * @author w.dehai
 */
@Slf4j
@Component
public class ClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (Objects.equals(msg, Const.HeartbeatMessage.PONG.getMessage())) {
            log.info("客户端心跳检测成功.");
        }
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.ALL_IDLE) {
                ctx.writeAndFlush(Const.HeartbeatMessage.PING.getMessage());
            }
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("客户端与服务端断开连接.");
        ctx.close();
    }

}
