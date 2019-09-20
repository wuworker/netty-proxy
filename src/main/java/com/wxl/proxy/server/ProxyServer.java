package com.wxl.proxy.server;

import com.wxl.proxy.common.ProxyChannelInitializer;
import com.wxl.proxy.log.ChannelHandlerLogEnhance;
import com.wxl.proxy.log.LoggingChannelFutureListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.SystemPropertyUtil;
import org.springframework.context.Lifecycle;


/**
 * Create by wuxingle on 2019/8/23
 * 代理服务器
 */
public interface ProxyServer<T extends ProxyConfig> extends Lifecycle {

    boolean LOG_CONNECT_ID = SystemPropertyUtil.getBoolean(
            "proxy.log.connectId", true);

    AttributeKey<String> ATTR_CONNECT_ID = AttributeKey.valueOf("connectId");
    AttributeKey<String> ATTR_PROXY_NAME = AttributeKey.valueOf("proxyName");
    AttributeKey<Channel> ATTR_FRONT_CHANNEL = AttributeKey.valueOf("frontChannel");
    AttributeKey<Channel> ATTR_BACKEND_CHANNEL = AttributeKey.valueOf("backendChannel");

    String name();

    T getConfig();

    /**
     * serverChannel初始化
     */
    void setServerHandlerInitializer(ProxyChannelInitializer<ServerSocketChannel, T> initializer);

    /**
     * 代理前端channel初始化
     */
    void setFrontHandlerInitializer(ProxyChannelInitializer<SocketChannel, T> initializer);

    /**
     * 代理后端channel初始化
     */
    void setBackendHandlerInitializer(ProxyChannelInitializer<SocketChannel, T> initializer);


    /**
     * handler增加connectId日志
     */
    @SuppressWarnings("unchecked")
    static <T extends ChannelHandler> T logHandler(T handler) {
        if (!LOG_CONNECT_ID) {
            return handler;
        }
        return (T) ChannelHandlerLogEnhance.adapter(handler);
    }

    /**
     * handler增加connectId日志
     */
    static <T extends ChannelHandler> T logSharableHandler(T handler) {
        if (!LOG_CONNECT_ID) {
            return handler;
        }
        return ChannelHandlerLogEnhance.proxy(handler);
    }

    /**
     * listener增加connectId日志
     */
    static ChannelFutureListener logListener(ChannelFutureListener listener) {
        if (!LOG_CONNECT_ID) {
            return listener;
        }
        return (LoggingChannelFutureListener) listener::operationComplete;
    }
}


