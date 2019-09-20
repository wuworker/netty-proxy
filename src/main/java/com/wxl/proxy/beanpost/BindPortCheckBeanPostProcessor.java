package com.wxl.proxy.beanpost;

import com.wxl.proxy.server.ProxyConfig;
import com.wxl.proxy.server.ProxyServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxingle on 2019/9/20.
 * 检测多个服务绑定的端口是否冲突
 */
public class BindPortCheckBeanPostProcessor implements BeanPostProcessor {

    private Map<Integer, String> bindPorts = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ProxyServer) {
            ProxyConfig config = ((ProxyServer) bean).getConfig();
            int bindPort = config.getBindPort();
            if (bindPort < 0 || bindPort > 0xFFFF) {
                throw new ProxyBindPortException(bindPort, config.getServerName());
            }
            String old = bindPorts.putIfAbsent(bindPort, config.getServerName());
            if (old != null) {
                throw new ProxyBindPortException(bindPort, config.getServerName(), old);
            }
        }
        return bean;
    }

    private static class ProxyBindPortException extends BeansException {

        public ProxyBindPortException(int port, String name) {
            super("server '" + name + "' bind port out of range:" + port);
        }

        public ProxyBindPortException(int port, String name1, String name2) {
            super("multi server bind same port:" + port + "! '" + name1 + "' and '" + name2 + "'");
        }
    }

    public static class ProxyBindPortFailureAnalyzer extends AbstractFailureAnalyzer<ProxyBindPortException> {

        @Override
        protected FailureAnalysis analyze(Throwable rootFailure, ProxyBindPortException cause) {
            return new FailureAnalysis(cause.getMessage(), "Please check server bind port!", cause);
        }
    }

}
