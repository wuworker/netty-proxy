package com.wxl.proxy.server;

import com.wxl.proxy.handler.ProxyChannelInitializer;
import com.wxl.proxy.log.ServerLoggingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;

/**
 * Create by wuxingle on 2019/8/23
 * 代理服务器基本框架
 * 默认autoRead false
 */
public abstract class AbstractProxyServer<T extends ProxyConfig>
        extends AbstractSimpleServer implements ProxyServer<T> {

    private ProxyChannelInitializer<ServerSocketChannel, T> serverInitializer;

    private ProxyChannelInitializer<SocketChannel, T> frontInitializer;

    private ProxyChannelInitializer<SocketChannel, T> backendInitializer;

    private T config;

    public AbstractProxyServer(T config, LoopResource loopResource) {
        super(config.getBindPort(), loopResource);
        this.config = config;
    }

    @Override
    public T getConfig() {
        return config;
    }

    @Override
    public String name() {
        return config.getServerName();
    }

    @Override
    public void setServerHandlerInitializer(ProxyChannelInitializer<ServerSocketChannel, T> initializer) {
        this.serverInitializer = initializer;
    }

    @Override
    public void setFrontHandlerInitializer(ProxyChannelInitializer<SocketChannel, T> initializer) {
        this.frontInitializer = initializer;
    }

    @Override
    public void setBackendHandlerInitializer(ProxyChannelInitializer<SocketChannel, T> initializer) {
        this.backendInitializer = initializer;
    }

    /**
     * 其他配置
     */
    @Override
    protected void configBootstrap(ServerBootstrap bootstrap) {
        bootstrap.childAttr(ATTR_PROXY_NAME, config.getServerName())
                .childOption(ChannelOption.AUTO_READ, false);
    }

    /**
     * server channel handler 初始化
     */
    @Override
    protected void initServerChannel(ServerSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ServerLoggingHandler(config.getServerName()));
        if (serverInitializer != null) {
            serverInitializer.init(ch, config);
        }
    }

    @Override
    protected final void initClientChannel(SocketChannel ch) throws Exception {
        initClientChannel(ch, backendInitializer);
    }

    /**
     * client channel handler 初始化
     */
    protected void initClientChannel(SocketChannel ch, ProxyChannelInitializer<SocketChannel, T> backendInitializer)
            throws Exception {
        if (frontInitializer != null) {
            frontInitializer.init(ch, config);
        }
    }

}
