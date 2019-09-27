package com.wxl.proxy.config;

import com.wxl.proxy.exception.BeanConfigException;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
@ConditionalOnProperty(prefix = TcpProxyProperties.TCP_PROXY_PREFIX, name = "enabled", havingValue = "true")
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

            // 没配置tcp,忽略
            BindResult<TcpProxyProperties> bindResult = binder.bind(TCP_PROXY_PREFIX, TcpProxyProperties.class);
            if (!bindResult.isBound()) {
                return;
            }
            TcpProxyProperties tcpProperties = bindResult.get();
            ProxyProperties proxyProperties = binder.bind(PROXY_PREFIX, ProxyProperties.class).get();

            Duration connectTimeout = tcpProperties.getConnectTimeout();
            if (connectTimeout == null) {
                connectTimeout = proxyProperties.getConnectTimeout();
            }

            if (CollectionUtils.isEmpty(tcpProperties.getServer())) {
                log.warn("no tcp proxy server! can config '" + TCP_PROXY_PREFIX + ".enabled=false' to disable tcp proxy");
                return;
            }

            for (Map.Entry<String, TcpServerProperties> entry : tcpProperties.getServer().entrySet()) {
                String key = entry.getKey();
                TcpServerProperties prop = entry.getValue();

                Integer bindPort = prop.getBindPort();
                if (bindPort == null || bindPort <= 0 || bindPort > 0xffff) {
                    throw new BeanConfigException("proxy.tcp.server." + key + ".bind-port", "tcp proxy bind port is illegal");
                }

                String remoteHost = prop.getRemoteHost();
                Integer remotePort = prop.getRemotePort();

                if (!StringUtils.hasText(remoteHost)) {
                    throw new BeanConfigException("proxy.tcp.server." + key + ".remote-host",
                            "remote host can not empty");
                }
                if (remotePort == null || remotePort <= 0 || remotePort > 0xffff) {
                    throw new BeanConfigException("proxy.tcp.server." + key + ".remote-port",
                            "remote port is illegal");
                }

                TcpProxyConfig config = TcpProxyConfig.builder()
                        .serverName(key)
                        .bindPort(bindPort)
                        .remoteAddress(new InetSocketAddress(remoteHost, remotePort))
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

