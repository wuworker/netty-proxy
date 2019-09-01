package com.wxl.proxy.http;

import com.wxl.proxy.common.ProxyChannelInitializer;
import com.wxl.proxy.common.ProxyFrontHandler;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorInitializer;
import com.wxl.proxy.server.AbstractProxyServer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Create by wuxingle on 2019/9/1
 * http隧道代理
 */
public class HttpProxyServer extends AbstractProxyServer<HttpProxyConfig> {

    private HttpProxyInterceptorInitializer interceptorInitializer = pipeline -> {
    };

    public HttpProxyServer(HttpProxyConfig config,
                           EventLoopGroup boosGroup,
                           EventLoopGroup workGroup) {
        super(config, boosGroup, workGroup);
        this.config = config;
    }

    /**
     * 前置处理器
     */
    @Override
    protected ProxyFrontHandler<HttpProxyConfig> newFrontHandler(
            HttpProxyConfig config, ProxyChannelInitializer<SocketChannel, HttpProxyConfig> backendInitializer) {
        return new HttpProxyFrontHandler(config, backendInitializer, interceptorInitializer);
    }

    /**
     * client channel handler 初始化
     */
    @Override
    protected void initClientChannel(SocketChannel ch) throws Exception {
        super.initClientChannel(ch);
        ch.pipeline().addFirst(HttpServerCodec.class.getName(), new HttpServerCodec());
    }


}
