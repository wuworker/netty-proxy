package com.wxl.proxy;

import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * Create by wuxingle on 2019/10/31
 * 系统常量
 */
public abstract class ProxySystemConstants {

    /**
     * 默认编码
     */
    public static final Charset DEFAULT_CHARSET = CharsetUtil.UTF_8;

    /**
     * 换行符
     */
    public static final String DEFAULT_LINE_SPLIT = "\r\n";

    /**
     * 当前版本
     */
    public static final String PROXY_VERSION;


    static {
        PROXY_VERSION = ProxySystemConstants.class.getPackage().getImplementationVersion();
    }

}
