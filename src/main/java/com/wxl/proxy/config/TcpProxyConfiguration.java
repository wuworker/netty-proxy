package com.wxl.proxy.config;

import com.wxl.proxy.common.BackendChannelInitializer;
import com.wxl.proxy.common.ComposeChannelInitializer;
import com.wxl.proxy.common.FrontChannelInitializer;
import com.wxl.proxy.common.ServerChannelInitializer;
import com.wxl.proxy.properties.ProxyProperties;
import com.wxl.proxy.properties.TcpProxyProperties;
import com.wxl.proxy.properties.TcpProxyProperties.TcpServerProperties;
import com.wxl.proxy.server.EventLoopGroupManager;
import com.wxl.proxy.tcp.TcpProxyConfig;
import com.wxl.proxy.tcp.TcpProxyServer;
import com.wxl.proxy.tcp.TcpProxyServerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Map;

/**
 * Create by wuxingle on 2019/8/17
 * tcp隧道代理配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({ProxyProperties.class, TcpProxyProperties.class})
public class TcpProxyConfiguration {

    private final ProxyProperties proxyProperties;

    private final TcpProxyProperties tcpProperties;

    public TcpProxyConfiguration(ProxyProperties proxyProperties,
                                 TcpProxyProperties tcpProxyProperties) {
        this.proxyProperties = proxyProperties;
        this.tcpProperties = tcpProxyProperties;
    }

    @Bean
    public TcpProxyServerManager tcpProxyServerManager(EventLoopGroupManager groupManager,
                                                       ObjectProvider<ServerChannelInitializer<TcpProxyConfig>> serverChannelInitializers,
                                                       ObjectProvider<FrontChannelInitializer<TcpProxyConfig>> frontChannelInitializers,
                                                       ObjectProvider<BackendChannelInitializer<TcpProxyConfig>> backendChannelInitializers) {
        TcpProxyServerManager serverManager = new TcpProxyServerManager();

        Duration connectTimeout = tcpProperties.getConnectTimeout();
        if (connectTimeout == null) {
            connectTimeout = proxyProperties.getConnectTimeout();
        }

        for (Map.Entry<String, TcpServerProperties> entry : tcpProperties.getServer().entrySet()) {
            String key = entry.getKey();
            TcpServerProperties prop = entry.getValue();

            TcpProxyConfig config = TcpProxyConfig.builder()
                    .serverName(key)
                    .bindPort(prop.getBindPort())
                    .remoteAddress(new InetSocketAddress(prop.getRemoteHost(), prop.getRemotePort()))
                    .connectTimeout(prop.getConnectTimeout() == null ? connectTimeout : prop.getConnectTimeout())
                    .build();

            log.debug("tcp proxy use config:{}", config);
            TcpProxyServer tcpProxyServer = new TcpProxyServer(config, groupManager.getBossGroup(), groupManager.getWorkGroup());
            tcpProxyServer.setServerHandlerInitializer(new ComposeChannelInitializer<>(serverChannelInitializers));
            tcpProxyServer.setFrontHandlerInitializer(new ComposeChannelInitializer<>(frontChannelInitializers));
            tcpProxyServer.setBackendHandlerInitializer(new ComposeChannelInitializer<>(backendChannelInitializers));

            serverManager.addLast(tcpProxyServer);
        }
        return serverManager;
    }

}

