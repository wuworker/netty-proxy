package com.wxl.proxy.properties;

import lombok.Data;

/**
 * Create by wuxingle on 2019/8/17
 * tcp代理服务配置
 */
@Data
public class TcpProxyServerProperties {

    private int bindPort;

    private String remoteHost;

    private int remotePort;

}

