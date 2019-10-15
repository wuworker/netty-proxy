package com.wxl.proxy.autoconfig.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import org.springframework.beans.factory.DisposableBean;

/**
 * Created by wuxingle on 2019/9/2.
 * eventLoopGroup管理
 */
public class EventLoopGroupManager implements DisposableBean {

    @Getter
    private EventLoopGroup bossGroup;

    @Getter
    private EventLoopGroup workGroup;

    public EventLoopGroupManager(int bossThreads, int workThreads) {
        this.bossGroup = new NioEventLoopGroup(bossThreads);
        this.workGroup = new NioEventLoopGroup(workThreads);
    }

    /**
     * 在lifecycle之后被调用
     */
    @Override
    public void destroy() throws Exception {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
