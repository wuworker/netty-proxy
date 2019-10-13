package com.wxl.proxy.server;

import lombok.Getter;
import lombok.ToString;

import java.time.Duration;

/**
 * Create by wuxingle on 2019/9/1
 * 代理配置
 */
@Getter
@ToString
public class ProxyConfig {

    private String serverName;

    private int bindPort;

    private Duration connectTimeout;

    protected ProxyConfig(String serverName, int bindPort, Duration connectTimeout) {
        this.serverName = serverName;
        this.bindPort = bindPort;
        this.connectTimeout = connectTimeout;
    }

    public static class ProxyConfigBuilder<T extends ProxyConfigBuilder<T>> {

        protected String serverName;

        protected int bindPort;

        protected Duration connectTimeout;

        public T serverName(String serverName) {
            this.serverName = serverName;
            return self();
        }

        public T bindPort(int bindPort) {
            this.bindPort = bindPort;
            return self();
        }

        public T connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return self();
        }

        @SuppressWarnings("unchecked")
        private T self() {
            return (T) this;
        }

        public ProxyConfig build() {
            return new ProxyConfig(serverName, bindPort, connectTimeout);
        }
    }


}
