package com.wxl.proxy.server;

/**
 * Create by wuxingle on 2019/11/11
 * eventLoopGroup事件循环资源池
 */
public interface LoopResources {

    /**
     * 分配资源
     */
    default LoopResource alloc(String name) {
        return alloc(name, 0, 0);
    }


    LoopResource alloc(String name, int bossThreads, int workThreads);

    /**
     * 资源释放
     */
    void release(String name);


    void release();
}
