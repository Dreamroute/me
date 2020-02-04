package com.github.dreamroute.me.server.netty;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class Mm {
    
    @Test
    public void serverTest() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(10), new NioEventLoopGroup(10))
            .channel(NioServerSocketChannel.class)
            .localAddress(new InetSocketAddress(8082))
            .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new LineBasedFrameDecoder(2048))
                        .addLast(new StringDecoder())
                        .addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        System.err.println(buf.toString(Charset.forName("UTF-8")));
                    }
                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        cause.printStackTrace();
                    }
                });
            }
        });
        ChannelFuture future = bootstrap.bind().sync();
        future.channel().closeFuture().sync();
    }
    
    @Test
    public void clientTest() throws Exception {
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .remoteAddress(InetAddress.getLocalHost(), 8082)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ctx.writeAndFlush("w.dehai");
                            }
                        });
                    }
                });
                ChannelFuture future = bootstrap.connect(System.getProperty("host", "127.0.0.1"), 8082).sync();
                future.channel().closeFuture().sync();
    }

}
