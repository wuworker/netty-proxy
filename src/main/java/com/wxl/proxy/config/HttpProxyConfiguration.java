package com.wxl.proxy.config;

import com.wxl.proxy.http.HttpProxyConfig;
import com.wxl.proxy.http.HttpProxyServer;
import com.wxl.proxy.properties.HttpProxyProperties;
import com.wxl.proxy.properties.ProxyProperties;
import com.wxl.proxy.server.EventLoopGroupManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Create by wuxingle on 2019/9/1
 * http隧道代理配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({ProxyProperties.class, HttpProxyProperties.class})
public class HttpProxyConfiguration {

    private final ProxyProperties proxyProperties;

    private final HttpProxyProperties httpProperties;

    public HttpProxyConfiguration(ProxyProperties proxyProperties,
                                  HttpProxyProperties httpProperties) {
        this.proxyProperties = proxyProperties;
        this.httpProperties = httpProperties;
    }

    @Bean
    public HttpProxyServer httpProxyServer(EventLoopGroupManager groupManager) {

        Duration connectTimeout = httpProperties.getConnectTimeout();
        if (connectTimeout == null) {
            connectTimeout = proxyProperties.getConnectTimeout();
        }
        HttpProxyConfig config = HttpProxyConfig.builder()
                .serverName(httpProperties.getName())
                .bindPort(httpProperties.getBindPort())
                .connectTimeout(connectTimeout)
                .build();

        log.debug("http proxy use config:{}", config);

        return new HttpProxyServer(config, groupManager.getBossGroup(), groupManager.getWorkGroup());
    }

}
