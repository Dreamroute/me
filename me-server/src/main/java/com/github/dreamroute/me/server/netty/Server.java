package com.github.dreamroute.me.server.netty;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.github.dreamroute.me.sdk.netty.codec.Encoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ObjectDecoder;
import lombok.AllArgsConstructor;

@Order(2)
@Component
@AllArgsConstructor
public class Server implements CommandLineRunner {
    
    private ServerHandler serverHandler;
    private ObjectDecoder decoder;
    private Encoder encoder;

    @Override
    public void run(String... args) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup()).channel(NioServerSocketChannel.class).option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                            .addLast(decoder)
                            .addLast(encoder)
                            .addLast(serverHandler);
                    }
                });
        ChannelFuture future = bootstrap.bind(10086).sync();
        future.channel().closeFuture().sync();
    }

}
