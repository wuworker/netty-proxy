package com.wxl.proxy.autoconfig.http;

import com.wxl.proxy.http.proxy.SecondProxyType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

import static com.wxl.proxy.autoconfig.server.ProxyProperties.PROXY_PREFIX;

/**
 * Created by wuxingle on 2019/9/2.
 * http代理配置
 */
@Data
@ConfigurationProperties(prefix = HttpProxyProperties.HTTP_PROXY_PREFIX)
public class HttpProxyProperties {

    public static final String HTTP_PROXY_PREFIX = PROXY_PREFIX + ".http";

    private String name = "http-proxy";

    private boolean enabled = true;

    private Integer bindPort;

    private Duration connectTimeout;

    /**
     * ssl配置，用于https解密
     */
    private SslProperties ssl;

    /**
     * 二级代理配置
     */
    private SecondProxyProperties secondProxy;

    @Data
    public static class SecondProxyProperties {

        private SecondProxyType type;

        private String host;

        private Integer port;

        private String username;

        private String password;

    }

    @Data
    public static class SslProperties {

        /**
         * ca私钥路径
         */
        private String caPrivateKeyPath;

        /**
         * ca证书路径
         */
        private String caCertPath;

    }

}
