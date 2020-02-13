package com.github.dreamroute.me.sdk.netty;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Client {
    
    @Autowired
    private ClientHandler clientHandler;
    
    public void createClient(String serverIp) {
        Thread channelThread = new Thread() {
            @Override
            public void run() {
                NioEventLoopGroup worker = new NioEventLoopGroup();
                Bootstrap bootstrap = new Bootstrap()
                        .group(worker)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, false)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipleline = ch.pipeline();
                                pipleline
                                    .addLast(new IdleStateHandler(0, 0 , 10, TimeUnit.SECONDS)) // 心跳
                                    .addLast(new ObjectDecoder(1024 * 1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
                                    .addLast(new ObjectEncoder())
                                    .addLast(clientHandler);
                            }
                        });
                try {
                    bootstrap.connect(serverIp, 10086).sync().channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    log.error("客户端创建长连接到服务端失败");
                    Thread.currentThread().interrupt();
                    worker.shutdownGracefully();
                }
            }
        };
        channelThread.start();
    }

}
