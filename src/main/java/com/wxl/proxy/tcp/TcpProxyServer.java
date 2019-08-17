package com.wxl.proxy.tcp;

import com.wxl.proxy.common.ServerLoggingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;

/**
 * Create by wuxingle on 2019/8/17
 * tcp隧道代理
 */
@Slf4j
public class TcpProxyServer implements SmartLifecycle {

    protected static final String PROXY_HANDLER = "ProxyHandler";

    @Getter
    private final String name;

    @Getter
    private final int bindPort;

    @Getter
    private final InetSocketAddress remoteAddress;

    private final EventLoopGroup boosGroup;

    private final EventLoopGroup workGroup;

    private boolean running;

    private Channel serverChannel;

    TcpProxyServer(String name, int bindPort, InetSocketAddress remoteAddress) {
        this(name, bindPort, remoteAddress, new NioEventLoopGroup(), new NioEventLoopGroup());
    }

    TcpProxyServer(String name, int bindPort, InetSocketAddress remoteAddress,
                   EventLoopGroup boosGroup, EventLoopGroup workGroup) {
        Assert.hasText(name, "server name can not empty!");
        Assert.notNull(remoteAddress, "remote address can not null!");
        Assert.notNull(boosGroup, "boss event loop group can not null!");
        Assert.notNull(workGroup, "work event loop group can not null!");
        this.name = name;
        this.bindPort = bindPort;
        this.remoteAddress = remoteAddress;
        this.boosGroup = boosGroup;
        this.workGroup = workGroup;
    }

    @Override
    public void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            ChannelFuture future = bootstrap.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new ChannelInitializer<ServerSocketChannel>() {
                        @Override
                        protected void initChannel(ServerSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ServerLoggingHandler(name));
                        }
                    })
                    .childOption(ChannelOption.AUTO_READ, false)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(PROXY_HANDLER, new TcpProxyFrontHandler(remoteAddress));
                        }
                    })
                    .bind(bindPort)
                    .sync();

            serverChannel = future.channel();
            running = true;
        } catch (Exception e) {
            running = false;
            throw new IllegalStateException(e);
        } finally {
            if (!running) {
                stop();
            }
        }
    }

    @Override
    public void stop() {
        if (serverChannel != null) {
            serverChannel.close().syncUninterruptibly();
        }
    }

    /**
     * 是否正在运行
     */
    @Override
    public boolean isRunning() {
        return running;
    }

}
