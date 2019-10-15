package com.wxl.proxy.tcp;

import com.wxl.proxy.handler.ProxyBackendHandler;
import com.wxl.proxy.handler.ProxyChannelInitializer;
import com.wxl.proxy.handler.ProxyFrontHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

import static com.wxl.proxy.server.ProxyServer.ATTR_BACKEND_CHANNEL;
import static com.wxl.proxy.server.ProxyServer.logListener;

/**
 * Create by wuxingle on 2019/8/17
 * 代理前端处理
 */
@Slf4j
public class TcpProxyFrontHandler extends ProxyFrontHandler<TcpProxyConfig> {

    public TcpProxyFrontHandler(TcpProxyConfig config,
                                ProxyChannelInitializer<SocketChannel, TcpProxyConfig> backendHandlerInitializer) {
        super(config, backendHandlerInitializer);
    }

    /**
     * 后置处理器
     */
    @Override
    protected ProxyBackendHandler<TcpProxyConfig> newBackendHandler(TcpProxyConfig config, Channel inboundChannel) {
        return new TcpProxyBackendHandler(config, inboundChannel);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("channel is active: '{}'", ctx.channel().remoteAddress());

        InetSocketAddress remoteAddress = config.getRemoteAddress();
        Channel inboundChannel = ctx.channel();

        ChannelFuture future = buildClientBootstrap(inboundChannel)
                .connect(remoteAddress);

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
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("front channel is close: '{}'", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("will close front connect: '{}', front handler cause exception:{}",
                ctx.channel().remoteAddress(), cause);
        super.exceptionCaught(ctx, cause);
    }
}
