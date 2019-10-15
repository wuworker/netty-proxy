package com.wxl.proxy.autoconfig.tcp;

import com.wxl.proxy.autoconfig.server.ProxyProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by wuxingle on 2019/8/17
 * tcp代理配置
 */
@Data
@ConfigurationProperties(prefix = TcpProxyProperties.TCP_PROXY_PREFIX)
public class TcpProxyProperties {

    public static final String TCP_PROXY_PREFIX = ProxyProperties.PROXY_PREFIX + ".tcp";

    private boolean enabled = false;

    private Duration connectTimeout;

    private Map<String, TcpServerProperties> server = new HashMap<>();

    @Data
    public static class TcpServerProperties {

        private Integer bindPort;

        private String remoteHost;

        private Integer remotePort;

        private Duration connectTimeout;
    }
}

