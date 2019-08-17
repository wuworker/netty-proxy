package com.wxl.proxy.tcp;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * Create by wuxingle on 2019/8/17
 * 代理后端处理
 */
@Slf4j
public class TcpProxyBackendHandler extends ChannelInboundHandlerAdapter {

    private final Channel inboundChannel;

    public TcpProxyBackendHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("tcp proxy server to remote host channel is close:{}", ctx.channel().remoteAddress());
        if (inboundChannel.isActive()) {
            inboundChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        inboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) f -> {
            if (f.isSuccess()) {
                ctx.channel().read();
            } else {
                log.error("write to inbound error", f.cause());
                f.channel().close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("tcp proxy server will close remote host connect:{}, backend handler cause exception:{}",
                ctx.channel().remoteAddress(), cause);
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
