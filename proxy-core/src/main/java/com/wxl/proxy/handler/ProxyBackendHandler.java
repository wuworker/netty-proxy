package com.wxl.proxy.handler;

import com.wxl.proxy.server.ProxyConfig;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import static com.wxl.proxy.server.ProxyServer.logListener;

/**
 * Create by wuxingle on 2019/9/1
 * 代理后置处理
 */
public abstract class ProxyBackendHandler<T extends ProxyConfig> extends ChannelInboundHandlerAdapter {

    protected Channel inboundChannel;

    protected T config;

    public ProxyBackendHandler(T config, Channel inboundChannel) {
        this.config = config;
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().read();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (inboundChannel.isActive()) {
            inboundChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (inboundChannel.isActive()) {
            forwardData(ctx, msg);
        } else {
            ctx.channel().close();
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
     * 数据转发
     */
    protected void forwardData(ChannelHandlerContext ctx, Object msg) throws Exception {
        inboundChannel.writeAndFlush(msg).addListener(logListener(f -> {
            if (f.isSuccess()) {
                ctx.channel().read();
            } else {
                forwardDataFail(f.channel(), f.cause());
            }
        }));
    }


    protected void forwardDataFail(Channel channel, Throwable cause) throws Exception {
        channel.close();
    }

}
