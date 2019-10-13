package com.wxl.proxy.http.proxy;

import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;

/**
 * Created by wuxingle on 2019/9/10.
 * 代理handler
 */
public class SecondProxyHandlers {

    /**
     * 创建proxyHandler
     */
    public static ProxyHandler newProxyHandler(SecondProxyConfig config) {
        Assert.notNull(config, "proxy autoconfig can not null");
        Assert.notNull(config.getType(), "proxy type can not null");
        Assert.notNull(config.getAddress(), "proxy address can not null");

        boolean auth = StringUtils.hasText(config.getUsername()) && StringUtils.hasText(config.getPassword());
        InetSocketAddress address = config.getAddress();
        ProxyHandler proxyHandler;
        switch (config.getType()) {
            case HTTP:
                proxyHandler = auth ? new HttpProxyHandler(address, config.getUsername(), config.getPassword())
                        : new HttpProxyHandler(address);
                break;
            case SOCKS4:
                proxyHandler = new Socks4ProxyHandler(address, config.getUsername());
                break;
            case SOCKS5:
                proxyHandler = auth ? new Socks5ProxyHandler(address, config.getUsername(), config.getPassword())
                        : new Socks5ProxyHandler(address);
                break;
            default:
                throw new IllegalStateException("can not use proxy type:" + config.getType());
        }
        return proxyHandler;
    }


}
