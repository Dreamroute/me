package com.github.dreamroute.me.server.netty;

import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class ServerClientTest {

    @Test
    public void serverTest() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(boss, worker).channel(NioServerSocketChannel.class).option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                System.err.println(buf.toString(Charset.forName("UTF-8")));
                                buf.release();
                            }

                            @Override
                            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                ByteBuf buf = ctx.alloc().buffer();
                                buf.writeCharSequence("return data.", Charset.forName("UTF-8"));
                                ctx.writeAndFlush(buf);
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {}

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                cause.printStackTrace();
                            }
                            
                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                ctx.close();
                                boss.shutdownGracefully();
                                worker.shutdownGracefully();
                            }
                            
                        });
                    }
                });
        ChannelFuture future = bootstrap.bind(8082).sync();
        future.channel().closeFuture().sync();
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void clientTest() throws Exception {
        NioEventLoopGroup pool = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap().group(pool).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                .addLast(new IdleStateHandler(0, 0, 5))
                .addLast(new ChannelInboundHandlerAdapter() {
                    
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        System.err.println("客户端触发心跳");
                        ByteBuf buf = ctx.alloc().buffer();
                        buf.writeCharSequence("send data.", Charset.forName("UTF-8"));
                        ctx.writeAndFlush(buf);
                    }
                    
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        System.err.println(buf.toString(Charset.forName("UTF-8")));
                        buf.release();
                    }

                    @Override
                    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                        ByteBuf buf = ctx.alloc().buffer();
                        buf.writeCharSequence("send data.", Charset.forName("UTF-8"));
                        ctx.writeAndFlush(buf);
                    }

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        ByteBuf buf = ctx.alloc().buffer();
                        buf.writeCharSequence("send data.", Charset.forName("UTF-8"));
                        ctx.writeAndFlush(buf);
                    }
                });
            }
        });
        ChannelFuture future = bootstrap.connect("127.0.0.1", 8082).sync();
        future.channel().closeFuture().sync();
        Thread.sleep(Long.MAX_VALUE);
    }

}
