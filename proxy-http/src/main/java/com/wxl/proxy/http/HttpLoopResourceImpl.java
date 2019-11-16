package com.wxl.proxy.http;

import com.wxl.proxy.server.LoopResource;
import com.wxl.proxy.server.LoopResources;
import io.netty.channel.EventLoopGroup;

/**
 * Create by wuxingle on 2019/11/16
 * http事件循环资源默认实现
 */
class HttpLoopResourceImpl implements HttpLoopResource {

    private LoopResource loopResource;

    HttpLoopResourceImpl(LoopResource loopResource) {
        this.loopResource = loopResource;
    }

    @Override
    public EventLoopGroup bossGroup() {
        return loopResource.bossGroup();
    }

    @Override
    public EventLoopGroup workGroup() {
        return loopResource.workGroup();
    }

    @Override
    public LoopResources parent() {
        return loopResource.parent();
    }

    @Override
    public void release() {
        loopResource.release();
    }
}
