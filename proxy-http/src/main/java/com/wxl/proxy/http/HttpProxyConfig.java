package com.wxl.proxy.http;

import com.wxl.proxy.http.proxy.SecondProxyConfig;
import com.wxl.proxy.http.ssl.SslConfig;
import com.wxl.proxy.server.ProxyConfig;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;

/**
 * Create by wuxingle on 2019/9/1
 * http配置
 */
@Getter
@ToString(callSuper = true)
public class HttpProxyConfig extends ProxyConfig {

    /**
     * 二级代理
     */
    private SecondProxyConfig secondProxy;

    /**
     * ssl配置，用于https解密和ssl握手
     */
    private SslConfig ssl;

    protected HttpProxyConfig(String serverName, int bindPort, Duration connectTimeout,
                              SecondProxyConfig secondProxy, SslConfig ssl) {
        super(serverName, bindPort, connectTimeout);
        this.secondProxy = secondProxy;
        this.ssl = ssl;
    }

    public static HttpProxyConfigBuilder builder() {
        return new HttpProxyConfigBuilder();
    }

    public static class HttpProxyConfigBuilder extends ProxyConfigBuilder<HttpProxyConfigBuilder> {

        private SecondProxyConfig secondProxy;

        private SslConfig ssl;

        public HttpProxyConfigBuilder secondProxy(SecondProxyConfig secondProxy) {
            this.secondProxy = secondProxy;
            return this;
        }

        public HttpProxyConfigBuilder ssl(SslConfig ssl) {
            this.ssl = ssl;
            return this;
        }

        @Override
        public HttpProxyConfig build() {
            return new HttpProxyConfig(serverName, bindPort,
                    connectTimeout, secondProxy, ssl);
        }
    }
}
