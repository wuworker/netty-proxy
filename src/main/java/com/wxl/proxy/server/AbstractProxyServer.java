package com.wxl.proxy.server;

import com.wxl.proxy.common.ProxyChannelInitializer;
import com.wxl.proxy.log.ServerLoggingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import org.springframework.util.Assert;

/**
 * Create by wuxingle on 2019/8/23
 * 代理服务器基本框架
 */
public abstract class AbstractProxyServer<S extends AbstractProxyServer<S>>
        implements ProxyServer<S> {

    @Getter
    private final int bindPort;

    private final String name;

    private final EventLoopGroup boosGroup;

    private final EventLoopGroup workGroup;

    private boolean running;

    private Channel serverChannel;

    private ProxyChannelInitializer<ServerSocketChannel, S> serverInitializer;

    private ProxyChannelInitializer<SocketChannel, S> frontInitializer;

    protected ProxyChannelInitializer<SocketChannel, S> backendInitializer;

    public AbstractProxyServer(String name, int bindPort, EventLoopGroup boosGroup,
                               EventLoopGroup workGroup) {
        Assert.hasText(name, "server name can not empty!");
        Assert.notNull(boosGroup, "boss event loop group can not null!");
        Assert.notNull(workGroup, "work event loop group can not null!");
        this.name = name;
        this.bindPort = bindPort;
        this.boosGroup = boosGroup;
        this.workGroup = workGroup;
    }

    @Override
    @SuppressWarnings("unchecked")
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
                                serverInitializer.init(ch, (S) AbstractProxyServer.this);
                            }
                        }
                    })
                    .childAttr(ATTR_PROXY_NAME, name)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            initClientChannel(ch);
                            if (frontInitializer != null) {
                                frontInitializer.init(ch, (S) AbstractProxyServer.this);
                            }
                        }
                    });

            configBootstrap(bootstrap);

            ChannelFuture future = bootstrap.bind(bindPort).sync();
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
        return name;
    }

    @Override
    public void setServerHandlerInitializer(ProxyChannelInitializer<ServerSocketChannel, S> initializer) {
        this.serverInitializer = initializer;
    }

    @Override
    public void setFrontHandlerInitializer(ProxyChannelInitializer<SocketChannel, S> initializer) {
        this.frontInitializer = initializer;
    }

    @Override
    public void setBackendHandlerInitializer(ProxyChannelInitializer<SocketChannel, S> initializer) {
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
        ch.pipeline().addLast(new ServerLoggingHandler(name));
    }

    /**
     * client channel handler 初始化
     */
    protected abstract void initClientChannel(SocketChannel ch) throws Exception;


}
