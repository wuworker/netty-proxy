package com.wxl.proxy.tcp;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;

import java.net.InetSocketAddress;

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
        return new TcpProxyServer(name, bindPort, remoteAddress, bossGroup, workGroup);
    }

}

