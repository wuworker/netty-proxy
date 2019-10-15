package com.wxl.proxy.handler;

import io.netty.channel.socket.SocketChannel;

/**
 * Created by wuxingle on 2019/9/16.
 * 后置连接通道初始化
 */
public interface BackendChannelInitializer<T> extends ProxyChannelInitializer<SocketChannel, T> {

}
