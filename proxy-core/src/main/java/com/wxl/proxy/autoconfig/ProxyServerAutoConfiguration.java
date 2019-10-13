package com.wxl.proxy.autoconfig;

import com.wxl.proxy.beanpost.BindPortCheckBeanPostProcessor;
import com.wxl.proxy.beanpost.ProxyServerPostProcessor;
import com.wxl.proxy.properties.ProxyProperties;
import com.wxl.proxy.server.EventLoopGroupManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wuxingle on 2019/9/20.
 * 代理服务配置
 */
@Configuration
@EnableConfigurationProperties(ProxyProperties.class)
public class ProxyServerAutoConfiguration {

    private final ProxyProperties proxyProperties;

    public ProxyServerAutoConfiguration(ProxyProperties proxyProperties) {
        this.proxyProperties = proxyProperties;
    }

    @Bean
    public EventLoopGroupManager eventLoopGroupManager() {
        int bossThreads = proxyProperties.getBossThreads();
        int workThreads = proxyProperties.getWorkThreads();
        return new EventLoopGroupManager(bossThreads, workThreads);
    }

    /**
     * 检测多个服务绑定的端口是否冲突
     */
    @Bean
    public static BindPortCheckBeanPostProcessor bindPortCheckBeanPostProcessor() {
        return new BindPortCheckBeanPostProcessor();
    }

    /**
     * 给proxyServer设置channelInitializer
     */
    @Bean
    public static ProxyServerPostProcessor proxyServerPostProcessor() {
        return new ProxyServerPostProcessor();
    }

}
