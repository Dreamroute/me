package com.github.dreamroute.me.sdk.netty;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.github.dreamroute.me.sdk.netty.codec.Encoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class Client {
    
    private ClientHandler clientHandler;
    private ObjectDecoder decoder;
    private Encoder encoder;
    
    public void createClient(String serverIp) {
        new Thread() {
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
                                    .addLast(decoder)
                                    .addLast(encoder)
                                    .addLast(clientHandler);
                            }
                        });
                try {
                    // 监听channel关闭，如果channel关闭，那么此处继续执行，执行NioEventLoopGroup.shutdownGracefully(), netty客户端完全关闭，此线程也就结束
                    // 这种操作只能在客户端实现，服务端即使同步监听channel关闭，也不会关闭服务
                    bootstrap.connect(serverIp, 10086).sync().channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    log.error("客户端创建长连接到服务端失败");
                    Thread.currentThread().interrupt();
                    worker.shutdownGracefully();
                } finally {
                    worker.shutdownGracefully();
                }
            }
        }.start();
    }

}
