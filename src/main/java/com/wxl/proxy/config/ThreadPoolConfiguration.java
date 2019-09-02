package com.wxl.proxy.config;

import com.wxl.proxy.properties.ProxyProperties;
import com.wxl.proxy.server.EventLoopGroupManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wuxingle on 2019/9/2.
 * 线程池管理
 */
@Configuration
@EnableConfigurationProperties(ProxyProperties.class)
public class ThreadPoolConfiguration {

    private final ProxyProperties proxyProperties;

    public ThreadPoolConfiguration(ProxyProperties proxyProperties) {
        this.proxyProperties = proxyProperties;
    }

    @Bean
    public EventLoopGroupManager eventLoopGroupManager() {
        int bossThreads = proxyProperties.getBossThreads();
        int workThreads = proxyProperties.getWorkThreads();
        return new EventLoopGroupManager(bossThreads, workThreads);
    }

}
