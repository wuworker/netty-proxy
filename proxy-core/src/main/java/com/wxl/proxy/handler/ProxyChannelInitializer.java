package com.wxl.proxy.handler;

import io.netty.channel.Channel;
import org.springframework.core.Ordered;

/**
 * Create by wuxingle on 2019/8/18
 * proxy channel初始化
 */
public interface ProxyChannelInitializer<C extends Channel, T> extends Ordered {

    void init(C channel, T config) throws Exception;

    @Override
    default int getOrder() {
        return 0;
    }
}

