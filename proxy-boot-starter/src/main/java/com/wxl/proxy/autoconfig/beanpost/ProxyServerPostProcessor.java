package com.wxl.proxy.autoconfig.beanpost;

import com.wxl.proxy.handler.BackendChannelInitializer;
import com.wxl.proxy.handler.ComposeChannelInitializer;
import com.wxl.proxy.handler.FrontChannelInitializer;
import com.wxl.proxy.handler.ServerChannelInitializer;
import com.wxl.proxy.server.ProxyServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by wuxingle on 2019/9/20.
 * 给proxyServer设置channelInitializer
 */
public class ProxyServerPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ProxyServer) {
            ProxyServer<?> server = (ProxyServer) bean;

            // 获取改server的配置类型
            Type genericSuperclass = bean.getClass().getGenericSuperclass();
            Type type = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];

            // 获取对应的channelInitializer
            ObjectProvider<ServerChannelInitializer<?>> serverInitializers = applicationContext.getBeanProvider(
                    ResolvableType.forClassWithGenerics(ServerChannelInitializer.class, ResolvableType.forType(type)));
            ObjectProvider<FrontChannelInitializer<?>> frontInitializers = applicationContext.getBeanProvider(
                    ResolvableType.forClassWithGenerics(FrontChannelInitializer.class, ResolvableType.forType(type)));
            ObjectProvider<BackendChannelInitializer<?>> backendInitializers = applicationContext.getBeanProvider(
                    ResolvableType.forClassWithGenerics(BackendChannelInitializer.class, ResolvableType.forType(type)));

            server.setServerHandlerInitializer(new ComposeChannelInitializer(serverInitializers));
            server.setFrontHandlerInitializer(new ComposeChannelInitializer(frontInitializers));
            server.setBackendHandlerInitializer(new ComposeChannelInitializer(backendInitializers));
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
