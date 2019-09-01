package com.wxl.proxy.http.interceptor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpObject;

/**
 * Create by wuxingle on 2019/9/1
 */
public abstract class FullResponseInterceptor implements HttpProxyInterceptor<HttpObject, HttpObject> {

    @Override
    public void beforeRequest(Channel inboundChannel,Channel outboundChannel, HttpObject request, HttpProxyInterceptorPipeline pipeline) {

    }

    @Override
    public void afterResponse(Channel inboundChannel,Channel outboundChannel, HttpObject response, HttpProxyInterceptorPipeline pipeline) {

    }
}
