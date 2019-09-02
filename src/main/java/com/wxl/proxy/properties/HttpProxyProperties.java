package com.wxl.proxy.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

import static com.wxl.proxy.properties.ProxyProperties.PROXY_PREFIX;

/**
 * Created by wuxingle on 2019/9/2.
 * http代理配置
 */
@Data
@ConfigurationProperties(prefix = PROXY_PREFIX + ".http")
public class HttpProxyProperties {

    private String name = "http-proxy";

    private int bindPort;

    private Duration connectTimeout;
}
