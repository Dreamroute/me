package com.github.dreamroute.me.sdk.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.alibaba.fastjson.JSON;
import com.github.dreamroute.me.sdk.common.Adapter;
import com.github.dreamroute.me.sdk.common.Config;
import com.github.dreamroute.me.sdk.common.IpUtil;
import com.github.dreamroute.me.sdk.netty.ClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 将本地配置文件上报到me服务器，使用定时任务而不是CommandLineRunner的方式可以实现服务端掉线再上线之后的重连 原理：解析application.yml文件，将解析结果使用RestTemplate上报到服务器
 * 
 * @author w.dehai
 */
@Slf4j
@Configuration
public class MeRegister {

    @Autowired
    private Config config;
    @Autowired
    private ConfigResource configResource;

    @Value("${server.port}")
    private int port;
    
    @Value("${me.client.ip:error}")
    private String ip;
    
    private String existServerIp;
    private boolean existClient;

    @Scheduled(cron = "1/30 * * * * ?")
    public void register() {
        validateMapping(config);
        config.setHeartbeatPort(port);
        String serverIp = null;
        try {
            serverIp = configResource.registryConfig(config);
            if (serverIp != null && serverIp.length() > 0) {
                log.info("上报配置到ME成功");
            }
            if (!Objects.equals(serverIp, existServerIp)) {
                existClient = false;
            }
        } catch (Exception e) {
            log.error("上报配置到ME失败" + e, e);
        }
        
        // 创建长连接客户端
        createClient(serverIp);
    }

    private void createClient(String serverIp) {
        if (existClient)
            return;
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipleline = ch.pipeline();
                        pipleline.addLast(new IdleStateHandler(0, 0, 5)); // 5s一次心跳检测
                        pipleline.addLast(new ClientHandler());
                    }
                });
        try {
            bootstrap.connect(serverIp, 10086).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("客户端创建长连接到服务端失败");
            Thread.currentThread().interrupt();
            worker.shutdownGracefully();
        }
        existClient = true;
    }

    private void validateMapping(Config config) {
        
        if (Objects.equals(ip, "error"))
            ip = IpUtil.getIp();
        config.setHeartbeatIp(ip);

        Long platformId = config.getPlatformId();
        if (platformId == null) {
            log.error("配置文件中缺少me.platformId配置");
        }

        List<Adapter> ads = config.getAdapter();
        if (ads == null || ads.isEmpty()) {
            log.error("配置文件中缺少me.adapter配置");
        }

        for (Adapter ad : ads) {
            String mapping = ad.getMapping();
            if (mapping == null || mapping.length() == 0) {
                log.error("配置文件中缺少me.mapping配置");
            }
            if (!isJson(mapping)) {
                log.error("配置文件中me.mapping格式不正确，需要符合JSON格式");
            }
        }
    }

    public static boolean isJson(String content) {
        try {
            JSON.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
