package com.wxl.proxy.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * Create by wuxingle on 2019/8/17
 * 代理前端处理
 */
@Slf4j
public class TcpProxyFrontHandler extends ChannelInboundHandlerAdapter {

    private final InetSocketAddress remoteAddress;

    private Channel outboundChannel;

    public TcpProxyFrontHandler(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("client {} is connect success!", ctx.channel().remoteAddress());
        final Channel inboundChannel = ctx.channel();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(inboundChannel.eventLoop())
                .channel(inboundChannel.getClass())
                .handler(new TcpProxyBackendHandler(inboundChannel))
                .option(ChannelOption.AUTO_READ, false);

        ChannelFuture future = bootstrap.connect(remoteAddress);
        outboundChannel = future.channel();
        future.addListener(f -> {
            if (f.isSuccess()) {
                log.info("remote host '{}' connect success", remoteAddress);
                inboundChannel.read();
            } else {
                log.info("remote host '{}' connect fail! {}", remoteAddress, f.cause());
                inboundChannel.close();
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("tcp proxy server to client channel is close:{}", ctx.channel().remoteAddress());
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
                    log.error("write to outbound error", f.cause());
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
        log.error("tcp proxy server will close client connect:{}, front handler cause exception:{}",
                ctx.channel().remoteAddress(), cause);
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
