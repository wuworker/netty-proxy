package com.wxl.proxy.autoconfig.tcp;

import com.wxl.proxy.tcp.TcpProxyServer;
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
                log.error("server {} start is error", server.name(), e);
            }
        }
    }

    @Override
    public void stop() {
        for (TcpProxyServer server : servers) {
            try {
                if (server.isRunning()) {
                    server.stop();
                }
            } catch (Throwable e) {
                log.error("server {} stop is error", server.name(), e);
            }
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

