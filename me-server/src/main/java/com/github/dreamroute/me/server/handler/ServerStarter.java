package com.github.dreamroute.me.server.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Component
public class ServerStarter implements CommandLineRunner {
    
    @Autowired
    private PrintMsgHandler pmh;

    @Override
    public void run(String... args) throws Exception {
        new ServerBootstrap().group(new NioEventLoopGroup()).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(pmh);
            }
        }).bind(8082).sync().channel().closeFuture().sync();
    }

}
