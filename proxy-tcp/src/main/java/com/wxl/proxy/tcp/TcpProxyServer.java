package com.wxl.proxy.tcp;

import com.wxl.proxy.handler.ProxyChannelInitializer;
import com.wxl.proxy.handler.ProxyFrontHandler;
import com.wxl.proxy.server.AbstractProxyServer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Create by wuxingle on 2019/8/17
 * tcp隧道代理
 */
@Slf4j
public class TcpProxyServer extends AbstractProxyServer<TcpProxyConfig> {


    public TcpProxyServer(TcpProxyConfig config,
                          EventLoopGroup boosGroup,
                          EventLoopGroup workGroup) {
        super(config, boosGroup, workGroup);
    }

    /**
     * 前置处理器
     */
    @Override
    protected ProxyFrontHandler<TcpProxyConfig> newFrontHandler(
            TcpProxyConfig config, ProxyChannelInitializer<SocketChannel, TcpProxyConfig> backendInitializer) {
        return new TcpProxyFrontHandler(config, backendInitializer);
    }

}
