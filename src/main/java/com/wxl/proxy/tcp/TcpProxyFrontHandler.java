package com.wxl.proxy.tcp;

import com.wxl.proxy.common.ProxyChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

import static com.wxl.proxy.server.ProxyServer.*;

/**
 * Create by wuxingle on 2019/8/17
 * 代理前端处理
 */
@Slf4j
public class TcpProxyFrontHandler extends ChannelInboundHandlerAdapter {

    private TcpProxyServer server;

    private final InetSocketAddress remoteAddress;

    private Channel outboundChannel;

    private ProxyChannelInitializer<SocketChannel, TcpProxyServer> backendHandlerInitializer;

    public TcpProxyFrontHandler(TcpProxyServer server,
                                ProxyChannelInitializer<SocketChannel, TcpProxyServer> backendHandlerInitializer) {
        this.server = server;
        this.remoteAddress = server.getRemoteAddress();
        this.backendHandlerInitializer = backendHandlerInitializer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel inboundChannel = ctx.channel();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(inboundChannel.eventLoop())
                .channel(inboundChannel.getClass())
                .attr(ATTR_PROXY_NAME, inboundChannel.attr(ATTR_PROXY_NAME).get())
                .attr(ATTR_FRONT_CHANNEL, inboundChannel)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(logHandler(new TcpProxyBackendHandler(inboundChannel)));
                        if (backendHandlerInitializer != null) {
                            backendHandlerInitializer.init(ch, server);
                        }
                    }
                })
                .option(ChannelOption.AUTO_READ, false);

        ChannelFuture future = bootstrap.connect(remoteAddress);
        outboundChannel = future.channel();
        future.addListener(logListener(f -> {
            if (f.isSuccess()) {
                log.info("remote '{}' connect success", remoteAddress);
                inboundChannel.read();
            } else {
                log.info("remote '{}' connect fail! {}", remoteAddress, f.cause());
                inboundChannel.close();
            }
        }));
        inboundChannel.attr(ATTR_BACKEND_CHANNEL).set(outboundChannel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("front channel is close: '{}'", ctx.channel().remoteAddress());
        if (outboundChannel != null && outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel != null && outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    ctx.channel().read();
                } else {
                    log.error("write to backend error", f.cause());
                    f.channel().close();
                }
            });
        } else {
            log.warn("the msg will ignore:{}", msg);
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("will close front connect: '{}', front handler cause exception:{}",
                ctx.channel().remoteAddress(), cause);
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
