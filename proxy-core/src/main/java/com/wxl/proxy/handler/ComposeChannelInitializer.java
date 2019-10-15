package com.wxl.proxy.handler;

import io.netty.channel.Channel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wuxingle on 2019/9/16.
 * 多个channelInit的组合
 */
public class ComposeChannelInitializer<C extends Channel, T> implements ProxyChannelInitializer<C, T> {

    private List<? extends ProxyChannelInitializer<C, T>> initializers;

    public ComposeChannelInitializer(List<? extends ProxyChannelInitializer<C, T>> initializers) {
        Assert.notNull(initializers, "initializers can not null");
        this.initializers = initializers;
    }

    public ComposeChannelInitializer(ObjectProvider<? extends ProxyChannelInitializer<C, T>> provider) {
        this(provider.orderedStream().collect(Collectors.toList()));
    }

    @Override
    public void init(C channel, T config) throws Exception {
        for (ProxyChannelInitializer<C, T> initializer : initializers) {
            initializer.init(channel, config);
        }
    }
}
