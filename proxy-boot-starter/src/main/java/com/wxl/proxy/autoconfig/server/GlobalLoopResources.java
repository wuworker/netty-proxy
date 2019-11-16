package com.wxl.proxy.autoconfig.server;

import com.wxl.proxy.server.LoopResource;
import com.wxl.proxy.server.LoopResources;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.Ordered;

/**
 * Create by wuxingle on 2019/11/14
 * 共用的循环资源
 */
public class GlobalLoopResources implements LoopResources, Ordered, DisposableBean {

    private int bossThreads;

    private int workThreads;

    private GlobalLoopResource loopResource;

    GlobalLoopResources(int bossThreads, int workThreads) {
        this.bossThreads = bossThreads;
        this.workThreads = workThreads;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void destroy() throws Exception {
        release();
    }

    @Override
    public LoopResource alloc(String name) {
        if (loopResource == null) {
            synchronized (this) {
                if (loopResource == null) {
                    loopResource = new GlobalLoopResource();
                }
            }
        }
        return loopResource;
    }


    @Override
    public LoopResource alloc(String name, int bossThreads, int workThreads) {
        return alloc(name);
    }

    @Override
    public void release(String name) {

    }

    @Override
    public void release() {
        if (loopResource != null) {
            synchronized (this) {
                if (loopResource != null) {
                    loopResource.boss.shutdownGracefully();
                    loopResource.work.shutdownGracefully();
                    loopResource = null;
                }
            }
        }
    }

    private class GlobalLoopResource implements LoopResource {

        private final EventLoopGroup boss;

        private final EventLoopGroup work;

        GlobalLoopResource() {
            this.boss = new NioEventLoopGroup(bossThreads);
            this.work = new NioEventLoopGroup(workThreads);
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
            return GlobalLoopResources.this;
        }

        @Override
        public void release() {

        }
    }

}
