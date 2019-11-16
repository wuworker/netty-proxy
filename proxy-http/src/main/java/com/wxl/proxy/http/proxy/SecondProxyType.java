package com.wxl.proxy.http.proxy;

/**
 * Created by wuxingle on 2019/9/10.
 * 二级代理类型
 */
public enum SecondProxyType {

    HTTP, SOCKS4, SOCKS5;


    public static SecondProxyType parse(String type) {
        if (HTTP.name().equalsIgnoreCase(type)) {
            return HTTP;
        }
        if (SOCKS4.name().equalsIgnoreCase(type)) {
            return SOCKS4;
        }
        if (SOCKS5.name().equalsIgnoreCase(type)) {
            return SOCKS5;
        }
        return null;
    }
}
