package com.github.dreamroute.me.server.netty;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class Server {

    @Test
    public void serverTest() throws Exception {
        new ServerBootstrap().group(new NioEventLoopGroup(), new NioEventLoopGroup()).channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(8082))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline cpp = ch.pipeline();
                        cpp.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                System.err.println("服务端接收到的信息：" + buf.toString(Charset.forName("UTF-8")));
                                ctx.writeAndFlush("Dreamroute");
                            }

                            @Override
                            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                cause.printStackTrace();
                            }
                        });
                    }
                }).bind().sync().channel().closeFuture().sync();
    }

    @Test
    public void clientTest() throws Exception {
        new Bootstrap().group(new NioEventLoopGroup()).channel(NioSocketChannel.class).remoteAddress(new InetSocketAddress("localhost", 8082)).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        ctx.writeAndFlush(Unpooled.copiedBuffer("w.dehai", CharsetUtil.UTF_8));
                    }

                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                        System.err.println("客户端接收到的信息：" + msg.toString());
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        cause.printStackTrace();
                        ctx.close();
                    }
                });
            }
        }).connect().sync().channel().closeFuture().sync();
    }

}
