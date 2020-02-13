package com.github.dreamroute.me.server.netty;

import com.github.dreamroute.me.sdk.common.CallBack;
import com.github.dreamroute.me.sdk.netty.Addr;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Addr) {
            // 处理客户端注册信息
            Addr addr = (Addr) msg;
            log.info("Server接收到客户端上报的IP地址为: {}", addr.getClientIp());
            
            MeCache.CTX_MAP.put(addr.getClientIp(), ctx);
        } else if (msg instanceof CallBack) {
            // 处理同步调用的返回数据
            CallBack cb = (CallBack) msg;
            MeFuture<CallBack> future = MeCache.THREAD_WAITE_CACHE.getIfPresent(cb.getId());
            future.set(cb);
        }
        ReferenceCountUtil.release(msg);
    }

}
