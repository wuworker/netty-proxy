package com.wxl.proxy.tcp;

import com.wxl.proxy.server.ProxyConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.InetSocketAddress;

/**
 * Create by wuxingle on 2019/9/1
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TcpProxyConfig extends ProxyConfig {

    private InetSocketAddress remoteAddress;
}
