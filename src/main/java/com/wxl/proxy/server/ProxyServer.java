package com.wxl.proxy.server;

import com.wxl.proxy.common.ProxyChannelInitializer;
import com.wxl.proxy.log.ChannelHandlerLogEnhance;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import org.springframework.context.Lifecycle;


/**
 * Create by wuxingle on 2019/8/23
 * 代理服务器
 */
public interface ProxyServer<S extends ProxyServer<S>>
        extends Lifecycle {

    AttributeKey<String> ATTR_CONNECT_ID = AttributeKey.valueOf("connectId");
    AttributeKey<String> ATTR_PROXY_NAME = AttributeKey.valueOf("proxyName");
    AttributeKey<Channel> ATTR_FRONT_CHANNEL = AttributeKey.valueOf("frontChannel");

    String name();

    /**
     * serverChannel初始化
     */
    void setServerHandlerInitializer(ProxyChannelInitializer<ServerSocketChannel, S> initializer);

    /**
     * 代理前端channel初始化
     */
    void setFrontHandlerInitializer(ProxyChannelInitializer<SocketChannel, S> initializer);

    /**
     * 代理后端channel初始化
     */
    void setBackendHandlerInitializer(ProxyChannelInitializer<SocketChannel, S> initializer);


    /**
     * 日志增强
     */
    @SuppressWarnings("unchecked")
    static <T extends ChannelHandler> T logEnhance(T handler) {
        return (T) ChannelHandlerLogEnhance.adapter(handler);
    }

    /**
     * 日志增强
     */
    static <T extends ChannelHandler> T logEnhanceSharable(T handler) {
        return ChannelHandlerLogEnhance.proxy(handler);
    }

}


