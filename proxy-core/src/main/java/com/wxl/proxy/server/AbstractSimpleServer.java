package com.wxl.proxy.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.util.Assert;

/**
 * Create by wuxingle on 2019/10/26
 * 普通服务器框架
 */
public abstract class AbstractSimpleServer implements SimpleServer {

    private final EventLoopGroup boosGroup;

    private final EventLoopGroup workGroup;

    private boolean running;

    private Channel serverChannel;

    private int bindPort;

    public AbstractSimpleServer(int bindPort,
                                EventLoopGroup boosGroup,
                                EventLoopGroup workGroup) {
        Assert.notNull(workGroup, "work event loop group can not null!");
        Assert.notNull(boosGroup, "boss event loop group can not null!");
        this.boosGroup = boosGroup;
        this.workGroup = workGroup;
        this.bindPort = bindPort;
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
                        }
                    })
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            initClientChannel(ch);
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


    /**
     * 其他配置
     */
    protected void configBootstrap(ServerBootstrap bootstrap) {

    }

    /**
     * server channel handler 初始化
     */
    protected void initServerChannel(ServerSocketChannel ch) throws Exception {

    }

    /**
     * client channel handler 初始化
     */
    protected void initClientChannel(SocketChannel ch) throws Exception {

    }

}
