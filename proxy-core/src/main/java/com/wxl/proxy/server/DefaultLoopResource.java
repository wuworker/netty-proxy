package com.wxl.proxy.server;

import io.netty.channel.EventLoopGroup;
import lombok.Getter;

/**
 * Create by wuxingle on 2019/11/14
 * 默认资源
 */
public class DefaultLoopResource implements LoopResource {

    @Getter
    private final String name;

    private final EventLoopGroup boss;

    private final EventLoopGroup work;

    private final LoopResources resources;

    public DefaultLoopResource(String name, LoopResources resources,
                               EventLoopGroup boss, EventLoopGroup work) {
        this.name = name;
        this.resources = resources;
        this.boss = boss;
        this.work = work;
    }

    @Override
    public EventLoopGroup bossGroup() {
        return boss;
    }

    @Override
    public EventLoopGroup workGroup() {
        return work;
    }

    @Override
    public LoopResources parent() {
        return resources;
    }

    @Override
    public void release() {
        resources.release(name);
    }
}
