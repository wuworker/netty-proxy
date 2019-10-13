package com.wxl.proxy.common;

import io.netty.channel.socket.SocketChannel;

/**
 * Created by wuxingle on 2019/9/16.
 * 前置连接通道初始化
 */
public interface FrontChannelInitializer<T> extends ProxyChannelInitializer<SocketChannel, T> {

}
