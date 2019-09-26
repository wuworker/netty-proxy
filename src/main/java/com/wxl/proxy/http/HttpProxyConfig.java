package com.wxl.proxy.http;

import com.wxl.proxy.http.proxy.SecondProxyConfig;
import com.wxl.proxy.http.ssl.SslConfig;
import com.wxl.proxy.server.ProxyConfig;
import io.netty.handler.ssl.SslContext;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;

/**
 * Create by wuxingle on 2019/9/1
 * http配置
 */
@Getter
@ToString(callSuper = true, exclude = {"clientSslContext"})
public class HttpProxyConfig extends ProxyConfig {

    private SecondProxyConfig secondProxy;

    /**
     * 代理和真实服务连接的ssl
     */
    private SslContext clientSslContext;

    /**
     * ssl配置，用于https解密和ssl握手
     */
    private SslConfig ssl;

    protected HttpProxyConfig(String serverName, int bindPort, Duration connectTimeout,
                              SecondProxyConfig secondProxy, SslContext clientSslContext, SslConfig ssl) {
        super(serverName, bindPort, connectTimeout);
        this.secondProxy = secondProxy;
        this.clientSslContext = clientSslContext;
        this.ssl = ssl;
    }

    public static HttpProxyConfigBuilder builder() {
        return new HttpProxyConfigBuilder();
    }

    public static class HttpProxyConfigBuilder extends ProxyConfigBuilder<HttpProxyConfigBuilder> {

        private SecondProxyConfig secondProxy;

        private SslContext clientSslContext;

        private SslConfig ssl;

        public HttpProxyConfigBuilder secondProxy(SecondProxyConfig secondProxy) {
            this.secondProxy = secondProxy;
            return this;
        }

        public HttpProxyConfigBuilder clientSslContext(SslContext clientSslContext) {
            this.clientSslContext = clientSslContext;
            return this;
        }

        public HttpProxyConfigBuilder ssl(SslConfig ssl) {
            this.ssl = ssl;
            return this;
        }

        @Override
        public HttpProxyConfig build() {
            return new HttpProxyConfig(serverName, bindPort,
                    connectTimeout, secondProxy, clientSslContext, ssl);
        }
    }
}
