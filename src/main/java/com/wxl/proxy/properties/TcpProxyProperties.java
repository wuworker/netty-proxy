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
@ConfigurationProperties(prefix = PROXY_PREFIX + ".tcp")
public class TcpProxyProperties {

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

