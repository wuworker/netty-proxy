package com.wxl.proxy.properties;

import com.wxl.proxy.http.proxy.SecondProxyType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

import static com.wxl.proxy.properties.ProxyProperties.PROXY_PREFIX;

/**
 * Created by wuxingle on 2019/9/2.
 * http代理配置
 */
@Data
@ConfigurationProperties(prefix = HttpProxyProperties.HTTP_PROXY_PREFIX)
public class HttpProxyProperties {

    public static final String HTTP_PROXY_PREFIX = PROXY_PREFIX + ".http";

    private String name = "http-proxy";

    private int bindPort;

    private Duration connectTimeout;

    private SecondProxyProperties secondProxy = new SecondProxyProperties();

    @Data
    public static class SecondProxyProperties {

        private SecondProxyType type;

        private String host;

        private int port;

        private String username;

        private String password;

    }

}
