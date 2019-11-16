package com.wxl.proxy.tcp;

import com.wxl.proxy.handler.ProxyBackendHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Create by wuxingle on 2019/8/17
 * 代理后端处理
 */
@Slf4j
public class TcpProxyBackendHandler extends ProxyBackendHandler<TcpProxyConfig> {

    public TcpProxyBackendHandler(TcpProxyConfig config, Channel inboundChannel) {
        super(config, inboundChannel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("backend channel is close: '{}'", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("will close backend connect: '{}', backend handler cause exception",
                ctx.channel().remoteAddress(), cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void forwardDataFail(Channel channel, Throwable cause) throws Exception {
        log.error("write to front error", cause);
        super.forwardDataFail(channel, cause);
    }
}
