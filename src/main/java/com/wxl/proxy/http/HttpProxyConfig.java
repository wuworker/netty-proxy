package com.wxl.proxy.http;

import com.wxl.proxy.http.proxy.SecondProxyConfig;
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

    private SecondProxyConfig secondProxy;

    protected HttpProxyConfig(String serverName, int bindPort, Duration connectTimeout,
                              SecondProxyConfig secondProxy) {
        super(serverName, bindPort, connectTimeout);
        this.secondProxy = secondProxy;
    }

    public static HttpProxyConfigBuilder builder() {
        return new HttpProxyConfigBuilder();
    }

    public static class HttpProxyConfigBuilder extends ProxyConfigBuilder<HttpProxyConfigBuilder> {

        private SecondProxyConfig secondProxy;

        public HttpProxyConfigBuilder secondProxy(SecondProxyConfig secondProxy) {
            this.secondProxy = secondProxy;
            return this;
        }

        @Override
        public HttpProxyConfig build() {
            return new HttpProxyConfig(serverName, bindPort, connectTimeout, secondProxy);
        }
    }
}
