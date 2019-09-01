package com.wxl.proxy.http.interceptor;

/**
 * Create by wuxingle on 2019/9/1
 * http代理拦截初始化
 */
public interface HttpProxyInterceptorInitializer {


    void init(HttpProxyInterceptorPipeline pipeline);

}
