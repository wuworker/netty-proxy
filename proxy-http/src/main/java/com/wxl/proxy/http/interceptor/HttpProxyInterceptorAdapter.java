package com.wxl.proxy.http.interceptor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpObject;

/**
 * Create by wuxingle on 2019/9/1
 * http代理拦截
 */
public class HttpProxyInterceptorAdapter implements HttpProxyInterceptor<HttpObject, HttpObject> {

    @Override
    public void beforeRequest(Channel inboundChannel, Channel outboundChannel, HttpObject request, HttpProxyInterceptorPipeline pipeline) throws Exception {
        pipeline.beforeRequest(inboundChannel, outboundChannel, request);
    }

    @Override
    public void afterResponse(Channel inboundChannel, Channel outboundChannel, HttpObject response, HttpProxyInterceptorPipeline pipeline) throws Exception {
        pipeline.afterResponse(inboundChannel, outboundChannel, response);
    }
}
