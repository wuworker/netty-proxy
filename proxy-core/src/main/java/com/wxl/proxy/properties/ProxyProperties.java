package com.wxl.proxy.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Created by wuxingle on 2019/9/2.
 * 代理配置
 */
@Data
@ConfigurationProperties(prefix = ProxyProperties.PROXY_PREFIX)
public class ProxyProperties {

    public static final String PROXY_PREFIX = "proxy";

    private int bossThreads;

    private int workThreads;

    private Duration connectTimeout;
}

