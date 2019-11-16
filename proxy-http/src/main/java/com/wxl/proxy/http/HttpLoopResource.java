package com.wxl.proxy.http;

import com.wxl.proxy.server.LoopResource;
import com.wxl.proxy.server.LoopResources;

/**
 * Create by wuxingle on 2019/11/16
 * http事件循环资源
 */
public interface HttpLoopResource extends LoopResource {

    String DEFAULT_RESOURCE_NAME = "http";

    static HttpLoopResource create(LoopResources loopResources) {
        return create(loopResources.alloc(DEFAULT_RESOURCE_NAME));
    }

    static HttpLoopResource create(LoopResource loopResource) {
        if (loopResource instanceof HttpLoopResource) {
            return (HttpLoopResource) loopResource;
        }
        return new HttpLoopResourceImpl(loopResource);
    }
}
