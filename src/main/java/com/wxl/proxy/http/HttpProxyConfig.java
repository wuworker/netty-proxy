package com.wxl.proxy.http;

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

    protected HttpProxyConfig(String serverName, int bindPort, Duration connectTimeout) {
        super(serverName, bindPort, connectTimeout);
    }

    public static HttpProxyConfigBuilder builder() {
        return new HttpProxyConfigBuilder();
    }

    public static class HttpProxyConfigBuilder extends ProxyConfigBuilder<HttpProxyConfigBuilder> {

        @Override
        public HttpProxyConfig build() {
            return new HttpProxyConfig(serverName, bindPort, connectTimeout);
        }
    }
}
