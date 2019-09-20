package com.wxl.proxy.config;

import com.wxl.proxy.server.ProxyConfig;
import com.wxl.proxy.server.ProxyServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxingle on 2019/9/20.
 * 检测多个服务绑定的端口是否冲突
 */
@Component
public class BindPortCheckBeanPostProcessor implements BeanPostProcessor {

    private Map<Integer, String> bindPorts = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ProxyServer) {
            ProxyConfig config = ((ProxyServer) bean).getConfig();
            String old = bindPorts.putIfAbsent(config.getBindPort(), config.getServerName());
            if (old != null) {
                throw new ProxyMultiBindException(config.getBindPort(), config.getServerName(), old);
            }
        }
        return bean;
    }

    private static class ProxyMultiBindException extends BeansException {
        public ProxyMultiBindException(int port, String name1, String name2) {
            super("multi server bind same port:" + port + "! '" + name1 + "' and '" + name2 + "'");
        }
    }

    public static class ProxyMultiBindFailureAnalyzer extends AbstractFailureAnalyzer<ProxyMultiBindException> {

        @Override
        protected FailureAnalysis analyze(Throwable rootFailure, ProxyMultiBindException cause) {
            return new FailureAnalysis(cause.getMessage(), "Please check server bind port!", cause);
        }
    }

}
