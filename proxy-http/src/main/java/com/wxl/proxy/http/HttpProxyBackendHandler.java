package com.wxl.proxy.http;

import com.wxl.proxy.handler.ProxyBackendHandler;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorPipeline;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import lombok.extern.slf4j.Slf4j;

/**
 * Create by wuxingle on 2019/9/1
 * http代理后置处理
 */
@Slf4j
public class HttpProxyBackendHandler extends ProxyBackendHandler<HttpProxyConfig> {

    private String host;

    private int port;

    private HttpProxyInterceptorPipeline interceptorPipeline;

    public HttpProxyBackendHandler(HttpProxyConfig config,
                                   String host, int port, Channel inboundChannel,
                                   HttpProxyInterceptorPipeline interceptorPipeline) {
        super(config, inboundChannel);
        this.host = host;
        this.port = port;
        this.interceptorPipeline = interceptorPipeline;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("backend channel is close {}:{}", host, port);
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (inboundChannel.isActive()) {
            if (interceptorPipeline != null && msg instanceof HttpObject) {
                interceptorPipeline.afterResponse(inboundChannel, ctx.channel(), (HttpObject) msg);
            } else {
                forwardData(ctx, msg);
            }
        } else {
            log.info("front is close!{}:{}", host, port);
            ctx.channel().close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("will close backend connect {}:{}, backend handler cause exception",
                host, port, cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void forwardDataFail(Channel channel, Throwable cause) throws Exception {
        log.error("write to front error", cause);
        super.forwardDataFail(channel, cause);
    }
}
