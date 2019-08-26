package com.wxl.proxy.tcp;

import com.wxl.proxy.common.ProxyChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create by wuxingle on 2019/8/17
 * tcp隧道代理创建
 */
public class TcpProxyServerBuilder {

    @Getter
    private String name;

    @Getter
    private int bossThreads;

    @Getter
    private int workThreads;

    @Getter
    private int bindPort;

    @Getter
    private InetSocketAddress remoteAddress;

    private List<ProxyChannelInitializer<SocketChannel, TcpProxyServer>> frontHandlerInitializes = new ArrayList<>();

    private List<ProxyChannelInitializer<ServerSocketChannel, TcpProxyServer>> serverHandlerInitializes = new ArrayList<>();

    private List<ProxyChannelInitializer<SocketChannel, TcpProxyServer>> backendHandlerInitializes = new ArrayList<>();

    private EventLoopGroup bossGroup;

    private EventLoopGroup workGroup;

    public TcpProxyServerBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TcpProxyServerBuilder bossGroup(int bossGroup) {
        this.bossThreads = bossGroup;
        return this;
    }

    public TcpProxyServerBuilder workGroup(int workGroup) {
        this.workThreads = workGroup;
        return this;
    }

    public TcpProxyServerBuilder bossGroup(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
        return this;
    }

    public TcpProxyServerBuilder workGroup(EventLoopGroup workGroup) {
        this.workGroup = workGroup;
        return this;
    }

    public TcpProxyServerBuilder bindPort(int bindPort) {
        this.bindPort = bindPort;
        return this;
    }

    public TcpProxyServerBuilder addFrontHandlerInitializer(
            ProxyChannelInitializer<SocketChannel, TcpProxyServer> channelInitializer) {
        frontHandlerInitializes.add(channelInitializer);
        return this;
    }

    public TcpProxyServerBuilder addServerHandlerInitializers(
            ProxyChannelInitializer<ServerSocketChannel, TcpProxyServer> channelInitializer) {
        serverHandlerInitializes.add(channelInitializer);
        return this;
    }

    public TcpProxyServerBuilder addBackendHandlerInitializer(
            ProxyChannelInitializer<SocketChannel, TcpProxyServer> channelInitializer) {
        backendHandlerInitializes.add(channelInitializer);
        return this;
    }

    public TcpProxyServerBuilder setFrontHandlerInitializer(List<ProxyChannelInitializer<SocketChannel, TcpProxyServer>> list) {
        frontHandlerInitializes = Collections.unmodifiableList(list);
        return this;
    }

    public TcpProxyServerBuilder setServerHandlerInitializers(List<ProxyChannelInitializer<ServerSocketChannel, TcpProxyServer>> list) {
        serverHandlerInitializes = Collections.unmodifiableList(list);
        return this;
    }

    public TcpProxyServerBuilder setBackendHandlerInitializer(List<ProxyChannelInitializer<SocketChannel, TcpProxyServer>> list) {
        backendHandlerInitializes = Collections.unmodifiableList(list);
        return this;
    }

    public TcpProxyServerBuilder remoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        return this;
    }

    public TcpProxyServer build() {
        if (bossGroup == null) {
            bossGroup = new NioEventLoopGroup(bossThreads);
        }
        if (workGroup == null) {
            workGroup = new NioEventLoopGroup(workThreads);
        }
        TcpProxyServer server = new TcpProxyServer(name, bindPort, remoteAddress, bossGroup, workGroup);

        if (!CollectionUtils.isEmpty(serverHandlerInitializes)) {
            server.setServerHandlerInitializer((channel, server1) -> {
                for (ProxyChannelInitializer<ServerSocketChannel, TcpProxyServer> initialize : serverHandlerInitializes) {
                    initialize.init(channel, server1);
                }
            });
        }
        if (!CollectionUtils.isEmpty(frontHandlerInitializes)) {
            server.setFrontHandlerInitializer((channel, server1) -> {
                for (ProxyChannelInitializer<SocketChannel, TcpProxyServer> initialize : frontHandlerInitializes) {
                    initialize.init(channel, server1);
                }
            });
        }
        if (!CollectionUtils.isEmpty(backendHandlerInitializes)) {
            server.setBackendHandlerInitializer((channel, server1) -> {
                for (ProxyChannelInitializer<SocketChannel, TcpProxyServer> initialize : backendHandlerInitializes) {
                    initialize.init(channel, server1);
                }
            });
        }
        return server;
    }
}

