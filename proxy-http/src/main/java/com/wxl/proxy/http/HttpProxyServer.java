package com.wxl.proxy.http;

import com.wxl.proxy.handler.ProxyChannelInitializer;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorInitializer;
import com.wxl.proxy.server.AbstractProxyServer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

import static com.wxl.proxy.server.ProxyServer.logHandler;

/**
 * Create by wuxingle on 2019/9/1
 * http隧道代理
 */
@Slf4j
public class HttpProxyServer extends AbstractProxyServer<HttpProxyConfig> implements SmartLifecycle {

    private HttpProxyInterceptorInitializer interceptorInitializer;

    public HttpProxyServer(HttpProxyConfig config,
                           EventLoopGroup boosGroup,
                           EventLoopGroup workGroup) {
        super(config, boosGroup, workGroup);
    }

    public void setHttpInterceptorInitializer(HttpProxyInterceptorInitializer interceptorInitializer) {
        this.interceptorInitializer = interceptorInitializer;
    }

    /**
     * client channel handler 初始化
     */
    @Override
    protected void initClientChannel(SocketChannel ch, ProxyChannelInitializer<SocketChannel, HttpProxyConfig> backendInitializer)
            throws Exception {
        super.initClientChannel(ch, backendInitializer);
        HttpProxyFrontHandler frontHandler = new HttpProxyFrontHandler(getConfig(), backendInitializer, interceptorInitializer);
        ch.pipeline().addFirst(HttpServerCodec.class.getName(), new HttpServerCodec())
                .addLast(HttpProxyFrontHandler.class.getName(), logHandler(frontHandler));
    }


}
