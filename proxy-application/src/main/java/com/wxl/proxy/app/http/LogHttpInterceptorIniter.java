package com.wxl.proxy.app.http;

import com.wxl.proxy.http.interceptor.HttpProxyInterceptor;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorInitializer;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorPipeline;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Create by wuxingle on 2019/10/13
 * 打印http请求头和响应头
 */
@Component
public class LogHttpInterceptorIniter implements HttpProxyInterceptorInitializer {

    private LogHttpInterceptor interceptor = new LogHttpInterceptor();

    @Override
    public void init(HttpProxyInterceptorPipeline pipeline) {
        pipeline.addLast(interceptor);
    }
}

@Slf4j
class LogHttpInterceptor implements HttpProxyInterceptor<HttpObject, HttpObject> {

    @Override
    public void beforeRequest(Channel inboundChannel, Channel outboundChannel,
                              HttpObject request, HttpProxyInterceptorPipeline pipeline) throws Exception {
        if (request instanceof HttpRequest) {
            log.info("{}", request);
        }
        pipeline.beforeRequest(inboundChannel, outboundChannel, request);
    }

    @Override
    public void afterResponse(Channel inboundChannel, Channel outboundChannel,
                              HttpObject response, HttpProxyInterceptorPipeline pipeline) throws Exception {
        if (response instanceof HttpResponse) {
            log.info("{}", response);
        }
        pipeline.afterResponse(inboundChannel, outboundChannel, response);
    }

}

