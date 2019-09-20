package com.wxl.proxy.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.wxl.proxy.properties.ProxyProperties.PROXY_PREFIX;

/**
 * Create by wuxingle on 2019/8/17
 * tcp代理配置
 */
@Data
@ConfigurationProperties(prefix = TcpProxyProperties.TCP_PROXY_PREFIX)
public class TcpProxyProperties {

    public static final String TCP_PROXY_PREFIX = PROXY_PREFIX + ".tcp";

    private boolean enabled = false;

    private Duration connectTimeout;

    private Map<String, TcpServerProperties> server = new HashMap<>();

    @Data
    public static class TcpServerProperties {

        private int bindPort;

        private String remoteHost;

        private int remotePort;

        private Duration connectTimeout;
    }
}

