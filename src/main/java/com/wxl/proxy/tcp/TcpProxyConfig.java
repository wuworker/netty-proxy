package com.wxl.proxy.tcp;

import com.wxl.proxy.server.ProxyConfig;
import lombok.Getter;
import lombok.ToString;

import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * Create by wuxingle on 2019/9/1
 * tcp代理配置
 */
@Getter
@ToString(callSuper = true)
public class TcpProxyConfig extends ProxyConfig {

    private InetSocketAddress remoteAddress;

    protected TcpProxyConfig(String serverName, int bindPort, Duration connectTimeout, InetSocketAddress remoteAddress) {
        super(serverName, bindPort, connectTimeout);
        this.remoteAddress = remoteAddress;
    }

    public static TcpProxyConfigBuilder builder() {
        return new TcpProxyConfigBuilder();
    }

    public static class TcpProxyConfigBuilder extends ProxyConfigBuilder<TcpProxyConfigBuilder> {

        private InetSocketAddress remoteAddress;

        public TcpProxyConfigBuilder remoteAddress(InetSocketAddress remoteAddress) {
            this.remoteAddress = remoteAddress;
            return this;
        }

        @Override
        public TcpProxyConfig build() {
            return new TcpProxyConfig(serverName, bindPort, connectTimeout, remoteAddress);
        }
    }


}
