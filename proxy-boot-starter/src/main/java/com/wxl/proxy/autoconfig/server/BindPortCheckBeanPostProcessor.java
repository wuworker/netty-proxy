package com.wxl.proxy.autoconfig.server;

import com.wxl.proxy.admin.AdminTelnetServer;
import com.wxl.proxy.autoconfig.exception.BeanConfigException;
import com.wxl.proxy.http.HttpProxyServer;
import com.wxl.proxy.server.SimpleServer;
import com.wxl.proxy.tcp.TcpProxyServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

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
        if (bean instanceof SimpleServer) {
            SimpleServer server = (SimpleServer) bean;
            int bindPort = server.bindPort();
            String old = bindPorts.putIfAbsent(bindPort, server.name());
            if (old != null) {
                String key;
                if (bean instanceof TcpProxyServer) {
                    key = "proxy.tcp.server." + server.name() + ".bind-port";
                } else if (bean instanceof HttpProxyServer) {
                    key = "proxy.http.bind-port";
                } else if (bean instanceof AdminTelnetServer) {
                    key = "proxy.admin.bind-port";
                } else {
                    key = null;
                }
                throw new BeanConfigException(key, "multi server bind same port:" + bindPort + "!",
                        "Please check server bind port!");
            }
        }
        return bean;
    }
}
