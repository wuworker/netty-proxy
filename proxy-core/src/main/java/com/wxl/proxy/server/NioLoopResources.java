package com.wxl.proxy.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by wuxingle on 2019/11/14
 * nioLoopResources
 */
public class NioLoopResources implements LoopResources {

    private Map<String, LoopResource> cache = new ConcurrentHashMap<>();

    private int bossThreads;

    private int workThreads;

    public NioLoopResources() {
    }

    public NioLoopResources(int bossThreads, int workThreads) {
        this.bossThreads = bossThreads;
        this.workThreads = workThreads;
    }

    /**
     * 分配资源
     */
    @Override
    public LoopResource alloc(String name) {
        return alloc(name, bossThreads, workThreads);
    }


    @Override
    public LoopResource alloc(String name, int bossThreads, int workThreads) {
        return cache.computeIfAbsent(name, k -> {
            EventLoopGroup boss = new NioEventLoopGroup(bossThreads);
            EventLoopGroup work = new NioEventLoopGroup(workThreads);
            return new DefaultLoopResource(name, this, boss, work);
        });
    }

    /**
     * 资源释放
     */
    @Override
    public void release(String name) {
        LoopResource resource = cache.remove(name);
        if (resource != null) {
            closeLoopResource(resource);
        }
    }

    @Override
    public void release() {
        Iterator<Map.Entry<String, LoopResource>> it = cache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, LoopResource> next = it.next();
            it.remove();
            closeLoopResource(next.getValue());
        }
    }


    private void closeLoopResource(LoopResource loopResource) {
        loopResource.bossGroup().shutdownGracefully();
        loopResource.workGroup().shutdownGracefully();
    }
}

