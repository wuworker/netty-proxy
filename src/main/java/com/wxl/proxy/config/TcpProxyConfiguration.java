package com.wxl.proxy.config;

import com.wxl.proxy.properties.ProxyProperties;
import com.wxl.proxy.properties.TcpProxyProperties;
import com.wxl.proxy.properties.TcpProxyProperties.TcpServerProperties;
import com.wxl.proxy.tcp.TcpProxyConfig;
import com.wxl.proxy.tcp.TcpProxyServer;
import com.wxl.proxy.tcp.TcpProxyServerFactoryBean;
import com.wxl.proxy.tcp.TcpProxyServerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Map;

import static com.wxl.proxy.properties.ProxyProperties.PROXY_PREFIX;
import static com.wxl.proxy.properties.TcpProxyProperties.TCP_PROXY_PREFIX;

/**
 * Create by wuxingle on 2019/8/17
 * tcp隧道代理配置
 */
@Slf4j
@Configuration
public class TcpProxyConfiguration {

    @Bean
    public TcpProxyServerManager tcpProxyServerManager(ObjectProvider<TcpProxyServer> servers) {
        TcpProxyServerManager serverManager = new TcpProxyServerManager();
        servers.stream().forEach(serverManager::addLast);

        return serverManager;
    }

    @Bean
    public static TcpProxyServerBeanDefinitionRegister tcpProxyServerBeanDefinitionRegister() {
        return new TcpProxyServerBeanDefinitionRegister();
    }

    /**
     * 注册TcpProxyServer
     */
    private static class TcpProxyServerBeanDefinitionRegister implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

        private Environment environment;

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            Binder binder = Binder.get(environment);
            TcpProxyProperties tcpProperties = binder.bind(TCP_PROXY_PREFIX, TcpProxyProperties.class).get();
            ProxyProperties proxyProperties = binder.bind(PROXY_PREFIX, ProxyProperties.class).get();

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

                AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(TcpProxyServerFactoryBean.class)
                        .addPropertyValue("config", config)
                        .getBeanDefinition();

                registry.registerBeanDefinition(key, beanDefinition);
            }
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }
    }


}

