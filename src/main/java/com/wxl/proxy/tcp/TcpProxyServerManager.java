package com.wxl.proxy.tcp;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by wuxingle on 2019/8/17
 * 多个tcp代理manager
 */
@Slf4j
public class TcpProxyServerManager implements SmartLifecycle {

    private List<TcpProxyServer> servers = new ArrayList<>();

    @Getter
    private EventLoopGroup bossGroup;

    @Getter
    private EventLoopGroup workGroup;

    public TcpProxyServerManager(int bossThreads, int workThreads) {
        this.bossGroup = new NioEventLoopGroup(bossThreads);
        this.workGroup = new NioEventLoopGroup(workThreads);
    }

    public TcpProxyServerManager addLast(TcpProxyServer server) {
        servers.add(server);
        return this;
    }

    @Override
    public void start() {
        for (TcpProxyServer server : servers) {
            try {
                if (!server.isRunning()) {
                    server.start();
                }
            } catch (Throwable e) {
                log.error("server {} start is error", e);
            }
        }
    }

    @Override
    public void stop() {
        try {
            for (TcpProxyServer server : servers) {
                try {
                    if (server.isRunning()) {
                        server.stop();
                    }
                } catch (Throwable e) {
                    log.error("server {} stop is error", server.name(), e);
                }
            }
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public boolean isRunning() {
        for (TcpProxyServer server : servers) {
            if (server.isRunning()) {
                return true;
            }
        }
        return false;
    }

}

