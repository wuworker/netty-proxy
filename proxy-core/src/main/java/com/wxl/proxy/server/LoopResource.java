package com.wxl.proxy.server;

import io.netty.channel.EventLoopGroup;

/**
 * Create by wuxingle on 2019/11/11
 * eventLoopGroup事件循环资源
 */
public interface LoopResource {


    EventLoopGroup bossGroup();


    EventLoopGroup workGroup();


    LoopResources parent();


    void release();
}
