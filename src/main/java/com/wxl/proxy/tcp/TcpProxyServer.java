package com.wxl.proxy.tcp;

import com.wxl.proxy.server.AbstractProxyServer;
import com.wxl.proxy.server.ProxyServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * Create by wuxingle on 2019/8/17
 * tcp隧道代理
 */
@Slf4j
public class TcpProxyServer extends AbstractProxyServer<TcpProxyServer> {

    protected static final String PROXY_HANDLER = "ProxyHandler";

    @Getter
    private final InetSocketAddress remoteAddress;


    public TcpProxyServer(String name, int bindPort, InetSocketAddress remoteAddress,
                          EventLoopGroup boosGroup, EventLoopGroup workGroup) {
        super(name, bindPort, boosGroup, workGroup);
        this.remoteAddress = remoteAddress;

    }

    /**
     * client channel handler 初始化
     */
    @Override
    protected void initClientChannel(SocketChannel ch) {
        ch.pipeline().addLast(PROXY_HANDLER,
                ProxyServer.logHandler(new TcpProxyFrontHandler(this, backendInitializer)));
    }

    /**
     * 其他配置
     */
    @Override
    protected void configBootstrap(ServerBootstrap bootstrap) {
        bootstrap.childOption(ChannelOption.AUTO_READ, false);
    }

}
