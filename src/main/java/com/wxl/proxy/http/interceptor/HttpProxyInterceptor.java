package com.wxl.proxy.http.interceptor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpObject;

/**
 * Create by wuxingle on 2019/9/1
 * http代理拦截
 */
public interface HttpProxyInterceptor<Q extends HttpObject, S extends HttpObject> {


    void beforeRequest(Channel inboundChannel, Channel outboundChannel,
                       Q request, HttpProxyInterceptorPipeline pipeline) throws Exception;


    void afterResponse(Channel inboundChannel, Channel outboundChannel,
                       S response, HttpProxyInterceptorPipeline pipeline) throws Exception;

}
