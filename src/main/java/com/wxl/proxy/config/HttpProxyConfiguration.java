package com.wxl.proxy.config;

import com.wxl.proxy.http.HttpProxyConfig;
import com.wxl.proxy.http.HttpProxyServer;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorInitializer;
import com.wxl.proxy.http.proxy.SecondProxyConfig;
import com.wxl.proxy.properties.HttpProxyProperties;
import com.wxl.proxy.properties.ProxyProperties;
import com.wxl.proxy.server.EventLoopGroupManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2019/9/1
 * http隧道代理配置
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = HttpProxyProperties.HTTP_PROXY_PREFIX, name = "enabled", havingValue = "true")
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
    public HttpProxyServer httpProxyServer(EventLoopGroupManager groupManager,
                                           ObjectProvider<HttpProxyInterceptorInitializer> interceptorInitializers) {
        Duration connectTimeout = httpProperties.getConnectTimeout();
        if (connectTimeout == null) {
            connectTimeout = proxyProperties.getConnectTimeout();
        }

        // 二级代理
        HttpProxyProperties.SecondProxyProperties secondProxyProp = httpProperties.getSecondProxy();
        SecondProxyConfig secondProxyConfig = null;
        if (secondProxyProp != null && secondProxyProp.getType() != null) {
            InetSocketAddress address = new InetSocketAddress(secondProxyProp.getHost(), secondProxyProp.getPort());

            secondProxyConfig = SecondProxyConfig.builder()
                    .type(secondProxyProp.getType())
                    .address(address)
                    .username(secondProxyProp.getUsername())
                    .password(secondProxyProp.getPassword())
                    .build();
        }

        HttpProxyConfig config = HttpProxyConfig.builder()
                .serverName(httpProperties.getName())
                .bindPort(httpProperties.getBindPort())
                .secondProxy(secondProxyConfig)
                .connectTimeout(connectTimeout)
                .build();

        log.debug("create http proxy server:{}", config);

        HttpProxyServer server = new HttpProxyServer(config, groupManager.getBossGroup(), groupManager.getWorkGroup());

        // http拦截器
        List<HttpProxyInterceptorInitializer> initializers = interceptorInitializers.orderedStream().collect(Collectors.toList());
        if (!initializers.isEmpty()) {
            server.setInterceptorInitializer(pipeline -> {
                for (HttpProxyInterceptorInitializer initializer : initializers) {
                    initializer.init(pipeline);
                }
            });
        }
        return server;
    }

}
