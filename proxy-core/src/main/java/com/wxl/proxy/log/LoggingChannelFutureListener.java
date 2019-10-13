package com.wxl.proxy.log;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import static com.wxl.proxy.log.MDCUtils.logConnectId;

/**
 * Create by wuxingle on 2019/8/25
 * 记录log的ChannelFutureListener
 */
public interface LoggingChannelFutureListener extends ChannelFutureListener {

    @Override
    default void operationComplete(ChannelFuture future) throws Exception {
        logConnectId(future.channel(), () -> complete(future));
    }


    void complete(ChannelFuture future) throws Exception;

}
