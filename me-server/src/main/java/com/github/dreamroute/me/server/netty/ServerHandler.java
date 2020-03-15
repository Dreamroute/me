package com.github.dreamroute.me.server.netty;

import java.util.Iterator;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.github.dreamroute.me.sdk.common.CallBack;
import com.github.dreamroute.me.sdk.netty.Addr;
import com.vip.vjtools.vjkit.collection.type.ConcurrentHashSet;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
@Component
public class ServerHandler extends ChannelInboundHandlerAdapter {
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Addr) {
            // 心跳上报客户端配置
            Addr addr = (Addr) msg;
            log.info("Server接收到客户端上报的IP地址为: {}", addr.getClientIp());
            
            MeCache.CTX_MAP.computeIfAbsent(addr.getPlatformId(), platformId -> new ConcurrentHashSet<>()).add(ctx);
        } else if (msg instanceof CallBack) {
            // 处理异步调用的返回数据
            CallBack cb = (CallBack) msg;
            MeFuture<CallBack> future = MeCache.THREAD_WAITE_CACHE.getIfPresent(cb.getId());
            future.set(cb);
        }
        ReferenceCountUtil.release(msg);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("服务端下线, 客户端与服务端断开连接, 移除当前通道.");
        ctx.close();
        String ctxName = ctx.name();
        MeCache.CTX_MAP.values().forEach(ctxSet -> {
           Iterator<ChannelHandlerContext> ct = ctxSet.iterator(); 
           while (ct.hasNext()) {
               if (Objects.equals(ct.next().name(), ctxName)) {
                   ct.remove();
                   break;
               }
           }
        });
    }

}
