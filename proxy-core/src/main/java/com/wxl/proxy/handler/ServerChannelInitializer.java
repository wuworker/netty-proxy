package com.wxl.proxy.handler;

import io.netty.channel.socket.ServerSocketChannel;

/**
 * Created by wuxingle on 2019/9/16.
 * 代理服务通道初始化
 */
public interface ServerChannelInitializer<T> extends ProxyChannelInitializer<ServerSocketChannel, T> {

}
