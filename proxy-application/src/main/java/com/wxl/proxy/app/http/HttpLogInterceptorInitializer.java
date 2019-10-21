package com.wxl.proxy.app.http;

import com.wxl.proxy.http.interceptor.HttpProxyInterceptor;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorInitializer;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorPipeline;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wxl.proxy.autoconfig.http.HttpProxyProperties.HTTP_PROXY_PREFIX;

/**
 * Create by wuxingle on 2019/10/21
 * 打印http头日志
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = HTTP_PROXY_PREFIX, name = "log", havingValue = "true")
public class HttpLogInterceptorInitializer implements HttpProxyInterceptorInitializer {

    private HttpProxyInterceptor<HttpObject, HttpObject> interceptor = new HttpProxyInterceptor<HttpObject, HttpObject>() {
        @Override
        public void beforeRequest(Channel inboundChannel,
                                  Channel outboundChannel,
                                  HttpObject request,
                                  HttpProxyInterceptorPipeline pipeline) throws Exception {
            if (request instanceof HttpRequest) {
                log.info("{}", request);
            }
            pipeline.beforeRequest(inboundChannel, outboundChannel, request);
        }

        @Override
        public void afterResponse(Channel inboundChannel,
                                  Channel outboundChannel,
                                  HttpObject response,
                                  HttpProxyInterceptorPipeline pipeline) throws Exception {
            if (response instanceof HttpResponse) {
                log.info("{}", response);
            }
            pipeline.afterResponse(inboundChannel, outboundChannel, response);
        }
    };

    @Override
    public void init(HttpProxyInterceptorPipeline pipeline) {
        pipeline.addLast(interceptor);
    }
}
