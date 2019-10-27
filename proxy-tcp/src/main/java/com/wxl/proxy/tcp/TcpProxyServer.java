package com.wxl.proxy.tcp;

import com.wxl.proxy.handler.ProxyChannelInitializer;
import com.wxl.proxy.server.AbstractProxyServer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

import static com.wxl.proxy.server.ProxyServer.logHandler;

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

    @Override
    protected void initClientChannel(SocketChannel ch, ProxyChannelInitializer<SocketChannel, TcpProxyConfig> backendInitializer)
            throws Exception {
        super.initClientChannel(ch, backendInitializer);
        TcpProxyFrontHandler frontHandler = new TcpProxyFrontHandler(getConfig(), backendInitializer);
        ch.pipeline().addLast(TcpProxyFrontHandler.class.getName(), logHandler(frontHandler));
    }

}
