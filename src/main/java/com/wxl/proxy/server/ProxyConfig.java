package com.wxl.proxy.server;

import lombok.Data;

/**
 * Create by wuxingle on 2019/9/1
 * 代理配置
 */
@Data
public class ProxyConfig {

    private String serverName;

    private int bindPort;

}
