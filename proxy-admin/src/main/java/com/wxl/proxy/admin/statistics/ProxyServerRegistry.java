package com.wxl.proxy.admin.statistics;

import com.wxl.proxy.server.ProxyServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by wuxingle on 2019/11/6
 * proxyServer注册中心
 */
public class ProxyServerRegistry implements DisposableBean {

    private Map<Integer, String> bindPorts = new ConcurrentHashMap<>();

    private Map<String, ProxyServer<?>> permanent = new ConcurrentHashMap<>();

    private Map<String, ProxyServer<?>> temporary = new ConcurrentHashMap<>();

    public ProxyServerRegistry(ObjectProvider<ProxyServer<?>> proxyServers) {
        proxyServers.stream().forEach(proxyServer -> permanent.put(proxyServer.name(), proxyServer));
    }

    @Override
    public void destroy() throws Exception {
        if (!temporary.isEmpty()) {
            for (ProxyServer<?> proxyServer : temporary.values()) {
                synchronized (proxyServer) {
                    if (proxyServer.isRunning()) {
                        proxyServer.stop();
                    }
                }
            }
        }
    }

    /**
     * 注册临时代理
     */
    public void registry(ProxyServer<?> server) {
        String oldName = bindPorts.putIfAbsent(server.bindPort(), server.name());
        if (oldName != null) {
            throw new IllegalStateException("bindPort can not same:" + server.bindPort());
        }

        ProxyServer old = temporary.putIfAbsent(server.name(), server);
        if (old != null) {
            throw new IllegalStateException("server name is already exist!" + server.name());
        }
    }

    /**
     * 获取正在运行的代理
     */
    public List<ProxyServer<?>> getRunning() {
        List<ProxyServer<?>> list = new ArrayList<>();
        permanent.forEach((k, v) -> {
            if (v.isRunning()) {
                list.add(v);
            }
        });
        temporary.forEach((k, v) -> {
            if (v.isRunning()) {
                list.add(v);
            }
        });
        return list;
    }

    /**
     * 根据名字获取代理
     */
    public ProxyServer<?> get(String name) {
        ProxyServer<?> proxyServer = permanent.get(name);
        if (proxyServer == null) {
            proxyServer = temporary.get(name);
        }
        return proxyServer;
    }

    public Optional<ProxyServer<?>> getOptional(String name) {
        return Optional.ofNullable(get(name));
    }

    /**
     * 获取持久代理
     */
    public List<ProxyServer<?>> getPermanent() {
        return new ArrayList<>(permanent.values());
    }

    /**
     * 获取临时代理
     */
    public List<ProxyServer<?>> getTemporary() {
        return new ArrayList<>(temporary.values());
    }
}
