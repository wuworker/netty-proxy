package com.wxl.proxy.server;

import org.springframework.context.Lifecycle;

/**
 * Create by wuxingle on 2019/10/26
 * 普通服务器
 */
public interface SimpleServer extends Lifecycle {

    /**
     * 服务器名
     */
    String name();

    /**
     * 当前绑定的端口
     */
    int bindPort();

    /**
     * 事件循环资源
     */
    LoopResource loopResource();
}
