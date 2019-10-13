package com.wxl.proxy.http.proxy;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.net.InetSocketAddress;

/**
 * Created by wuxingle on 2019/9/10.
 * http二级代理
 */
@Getter
@ToString
@Builder
public class SecondProxyConfig {

    private SecondProxyType type;

    private InetSocketAddress address;

    private String username;

    private String password;

}
