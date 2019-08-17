package com.wxl.proxy.config;

import com.wxl.proxy.properties.TcpProxyProperties;
import com.wxl.proxy.properties.TcpProxyServerProperties;
import com.wxl.proxy.tcp.TcpProxyServer;
import com.wxl.proxy.tcp.TcpProxyServerBuilder;
import com.wxl.proxy.tcp.TcpProxyServerManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Create by wuxingle on 2019/8/17
 * tcp隧道代理配置
 */
@Configuration
@EnableConfigurationProperties(TcpProxyProperties.class)
public class TcpProxyConfiguration {

    private final TcpProxyProperties tcpProxyProperties;

    public TcpProxyConfiguration(TcpProxyProperties tcpProxyProperties) {
        this.tcpProxyProperties = tcpProxyProperties;
    }

    @Bean
    public TcpProxyServerManager tcpProxyServerManager() {
        int bossThreads = tcpProxyProperties.getBossThreads();
        int workThreads = tcpProxyProperties.getWorkThreads();

        TcpProxyServerManager serverManager = new TcpProxyServerManager(bossThreads, workThreads);

        TcpProxyServerBuilder builder = new TcpProxyServerBuilder();
        for (Map.Entry<String, TcpProxyServerProperties> entry : tcpProxyProperties.getServer().entrySet()) {
            String key = entry.getKey();
            TcpProxyServerProperties prop = entry.getValue();

            TcpProxyServer server = builder.name(key)
                    .bindPort(prop.getBindPort())
                    .remoteAddress(new InetSocketAddress(prop.getRemoteHost(), prop.getRemotePort()))
                    .bossGroup(serverManager.getBossGroup())
                    .workGroup(serverManager.getWorkGroup())
                    .build();
            serverManager.addLast(server);
        }
        return serverManager;
    }

}


