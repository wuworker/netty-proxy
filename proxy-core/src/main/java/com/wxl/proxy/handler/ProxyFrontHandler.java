package com.wxl.proxy.handler;

import com.wxl.proxy.server.ProxyConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;

import static com.wxl.proxy.server.ProxyServer.*;

/**
 * Create by wuxingle on 2019/9/1
 * 代理前置处理
 */
public abstract class ProxyFrontHandler<T extends ProxyConfig> extends ChannelInboundHandlerAdapter {

    protected Channel outboundChannel;

    protected T config;

    private ProxyChannelInitializer<SocketChannel, T> backendHandlerInitializer;

    public ProxyFrontHandler(T config, ProxyChannelInitializer<SocketChannel, T> backendHandlerInitializer) {
        this.config = config;
        this.backendHandlerInitializer = backendHandlerInitializer;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (outboundChannel != null && outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 构建连接真实服务器的Bootstrap
     */
    protected Bootstrap buildClientBootstrap(Channel inboundChannel) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(inboundChannel.eventLoop())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        initChannelHandler(ch, inboundChannel);
                        if (backendHandlerInitializer != null) {
                            backendHandlerInitializer.init(ch, config);
                        }
                    }
                })
                .channel(inboundChannel.getClass())
                .attr(ATTR_PROXY_NAME, inboundChannel.attr(ATTR_PROXY_NAME).get())
                .attr(ATTR_FRONT_CHANNEL, inboundChannel)
                .option(ChannelOption.AUTO_READ, false);
        if (config.getConnectTimeout() != null) {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) config.getConnectTimeout().toMillis());
        }

        return bootstrap;
    }


    protected void initChannelHandler(SocketChannel ch, Channel inboundChannel) throws Exception {
        ProxyBackendHandler<T> backendHandler = newBackendHandler(config, inboundChannel);
        if (backendHandler != null) {
            ch.pipeline().addLast(backendHandler.getClass().getName(), logHandler(backendHandler));
        }
    }

    /**
     * 后置处理器
     */
    protected abstract ProxyBackendHandler<T> newBackendHandler(T config, Channel inboundChannel);
}

