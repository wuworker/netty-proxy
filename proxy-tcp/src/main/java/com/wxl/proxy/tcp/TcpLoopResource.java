package com.wxl.proxy.tcp;

import com.wxl.proxy.server.LoopResource;
import com.wxl.proxy.server.LoopResources;

/**
 * Create by wuxingle on 2019/11/16
 * tcp事件循环资源
 */
public interface TcpLoopResource extends LoopResource {

    String DEFAULT_RESOURCE_NAME = "tcp";

    static TcpLoopResource create(LoopResources loopResources) {
        return create(loopResources.alloc(DEFAULT_RESOURCE_NAME));
    }

    static TcpLoopResource create(LoopResource loopResource) {
        if (loopResource instanceof TcpLoopResource) {
            return (TcpLoopResource) loopResource;
        }
        return new TcpLoopResourceImpl(loopResource);
    }

}
