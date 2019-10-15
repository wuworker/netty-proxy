package com.wxl.proxy.autoconfig.tcp;

import com.wxl.proxy.autoconfig.server.EventLoopGroupManager;
import com.wxl.proxy.tcp.TcpProxyConfig;
import com.wxl.proxy.tcp.TcpProxyServer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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
        EventLoopGroupManager manager = applicationContext.getBean(EventLoopGroupManager.class);
        TcpProxyServer server = new TcpProxyServer(config, manager.getBossGroup(), manager.getWorkGroup());

        log.debug("create tcp proxy server:{}", config);
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
