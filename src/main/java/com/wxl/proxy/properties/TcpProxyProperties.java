package com.wxl.proxy.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by wuxingle on 2019/8/17
 * tcp代理配置
 */
@Data
@ConfigurationProperties(prefix = "proxy.tcp")
public class TcpProxyProperties {

    private int bossThreads;

    private int workThreads;

    private Map<String, TcpProxyServerProperties> server = new HashMap<>();


}

