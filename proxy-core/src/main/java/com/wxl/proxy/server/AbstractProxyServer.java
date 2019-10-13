package com.wxl.proxy.server;

import com.wxl.proxy.common.ProxyChannelInitializer;
import com.wxl.proxy.common.ProxyFrontHandler;
import com.wxl.proxy.log.ServerLoggingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.util.Assert;

import static com.wxl.proxy.server.ProxyServer.logHandler;

/**
 * Create by wuxingle on 2019/8/23
 * 代理服务器基本框架
 * 默认autoRead false
 */
public abstract class AbstractProxyServer<T extends ProxyConfig> implements ProxyServer<T> {

    private final EventLoopGroup boosGroup;

    private final EventLoopGroup workGroup;

    private boolean running;

    private Channel serverChannel;

    private ProxyChannelInitializer<ServerSocketChannel, T> serverInitializer;

    private ProxyChannelInitializer<SocketChannel, T> frontInitializer;

    private ProxyChannelInitializer<SocketChannel, T> backendInitializer;

    private T config;

    public AbstractProxyServer(T config,
                               EventLoopGroup boosGroup,
                               EventLoopGroup workGroup) {
        Assert.notNull(boosGroup, "boss event loop group can not null!");
        Assert.notNull(workGroup, "work event loop group can not null!");
        this.config = config;
        this.boosGroup = boosGroup;
        this.workGroup = workGroup;
    }

    @Override
    public T getConfig() {
        return config;
    }

    @Override
    public final void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new ChannelInitializer<ServerSocketChannel>() {
                        @Override
                        protected void initChannel(ServerSocketChannel ch) throws Exception {
                            initServerChannel(ch);
                            if (serverInitializer != null) {
                                serverInitializer.init(ch, config);
                            }
                        }
                    })
                    .childAttr(ATTR_PROXY_NAME, config.getServerName())
                    .childOption(ChannelOption.AUTO_READ, false)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            initClientChannel(ch);
                            if (frontInitializer != null) {
                                frontInitializer.init(ch, config);
                            }
                        }
                    });

            configBootstrap(bootstrap);

            ChannelFuture future = bootstrap.bind(config.getBindPort()).sync();
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
    public final void stop() {
        if (serverChannel != null) {
            serverChannel.close().syncUninterruptibly();
        }
        running = false;
    }

    @Override
    public final boolean isRunning() {
        return running;
    }

    @Override
    public String name() {
        return config.getServerName();
    }

    @Override
    public void setServerHandlerInitializer(ProxyChannelInitializer<ServerSocketChannel, T> initializer) {
        this.serverInitializer = initializer;
    }

    @Override
    public void setFrontHandlerInitializer(ProxyChannelInitializer<SocketChannel, T> initializer) {
        this.frontInitializer = initializer;
    }

    @Override
    public void setBackendHandlerInitializer(ProxyChannelInitializer<SocketChannel, T> initializer) {
        this.backendInitializer = initializer;
    }

    /**
     * 其他配置
     */
    protected void configBootstrap(ServerBootstrap bootstrap) {

    }

    /**
     * server channel handler 初始化
     */
    protected void initServerChannel(ServerSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ServerLoggingHandler(config.getServerName()));
    }

    /**
     * client channel handler 初始化
     */
    protected void initClientChannel(SocketChannel ch) throws Exception {
        //应用前置处理器
        ProxyFrontHandler<T> frontHandler = newFrontHandler(config, backendInitializer);
        if (frontHandler != null) {
            ch.pipeline().addLast(frontHandler.getClass().getName(), logHandler(frontHandler));
        }
    }

    /**
     * 前置处理器
     */
    protected abstract ProxyFrontHandler<T> newFrontHandler(
            T config, ProxyChannelInitializer<SocketChannel, T> backendInitializer);

}
