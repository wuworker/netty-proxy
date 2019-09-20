package com.wxl.proxy.tcp;

import com.wxl.proxy.common.BackendChannelInitializer;
import com.wxl.proxy.common.ComposeChannelInitializer;
import com.wxl.proxy.common.FrontChannelInitializer;
import com.wxl.proxy.common.ServerChannelInitializer;
import com.wxl.proxy.server.EventLoopGroupManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;

/**
 * Created by wuxingle on 2019/9/20.
 * factory bean
 */
@Slf4j
public class TcpProxyServerFactoryBean implements FactoryBean<TcpProxyServer>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Getter
    @Setter
    private TcpProxyConfig config;

    @Override
    public TcpProxyServer getObject() throws Exception {
        log.debug("create tcp proxy server:{}", config);

        EventLoopGroupManager manager = applicationContext.getBean(EventLoopGroupManager.class);
        ObjectProvider<ServerChannelInitializer<TcpProxyConfig>> serverChannelInitializers = applicationContext.getBeanProvider(
                ResolvableType.forClassWithGenerics(ServerChannelInitializer.class, TcpProxyConfig.class));
        ObjectProvider<FrontChannelInitializer<TcpProxyConfig>> frontChannelInitializers = applicationContext.getBeanProvider(
                ResolvableType.forClassWithGenerics(ServerChannelInitializer.class, TcpProxyConfig.class));
        ObjectProvider<BackendChannelInitializer<TcpProxyConfig>> backendChannelInitializers = applicationContext.getBeanProvider(
                ResolvableType.forClassWithGenerics(ServerChannelInitializer.class, TcpProxyConfig.class));

        TcpProxyServer server = new TcpProxyServer(config, manager.getBossGroup(), manager.getWorkGroup());
        server.setServerHandlerInitializer(new ComposeChannelInitializer<>(serverChannelInitializers));
        server.setFrontHandlerInitializer(new ComposeChannelInitializer<>(frontChannelInitializers));
        server.setBackendHandlerInitializer(new ComposeChannelInitializer<>(backendChannelInitializers));
        return server;
    }

    @Override
    public Class<?> getObjectType() {
        return TcpProxyServer.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
