package com.wxl.proxy.http.interceptor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by wuxingle on 2019/9/1
 * http代理拦截pipeline
 */
public class HttpProxyInterceptorPipeline {

    private List<HttpProxyInterceptor<HttpObject, HttpObject>> headInterceptors = new ArrayList<>();

    private List<HttpProxyInterceptor<HttpObject, HttpObject>> tailInterceptors = new ArrayList<>();

    private HttpProxyInterceptor<HttpObject, HttpObject> head;

    private HttpProxyInterceptor<HttpObject, HttpObject> tail;

    private int headIndex = -1;

    private int tailIndex = -1;

    @Getter
    private HttpRequest httpRequest;

    @Getter
    private HttpResponse httpResponse;

    public HttpProxyInterceptorPipeline() {
        this(null, null);
    }

    public HttpProxyInterceptorPipeline(HttpProxyInterceptor<HttpObject, HttpObject> head,
                                        HttpProxyInterceptor<HttpObject, HttpObject> tail) {
        this.head = head == null ? new HttpProxyInterceptorAdapter() : head;
        this.tail = tail == null ? new HttpProxyInterceptorAdapter() : tail;
    }

    public void beforeRequest(Channel inboundChannel, Channel outboundChannel, HttpObject request) throws Exception {
        if (request instanceof HttpRequest) {
            this.httpRequest = (HttpRequest) request;
        }
        HttpProxyInterceptor<HttpObject, HttpObject> interceptor = next(headIndex++, head, tail, headInterceptors);
        if (interceptor != null) {
            interceptor.beforeRequest(inboundChannel, outboundChannel, request, this);
        }

        headIndex = -1;
    }


    public void afterResponse(Channel inboundChannel, Channel outboundChannel, HttpObject response) throws Exception {
        if (response instanceof HttpResponse) {
            this.httpResponse = (HttpResponse) response;
        }
        HttpProxyInterceptor<HttpObject, HttpObject> interceptor = next(tailIndex++, tail, head, tailInterceptors);
        if (interceptor != null) {
            interceptor.afterResponse(inboundChannel, outboundChannel, response, this);
        }

        tailIndex = -1;
    }


    public HttpProxyInterceptorPipeline addLast(HttpProxyInterceptor<HttpObject, HttpObject> interceptor) {
        headInterceptors.add(interceptor);
        tailInterceptors.add(0, interceptor);
        return this;
    }

    public HttpProxyInterceptorPipeline addFirst(HttpProxyInterceptor<HttpObject, HttpObject> interceptor) {
        headInterceptors.add(0, interceptor);
        tailInterceptors.add(interceptor);
        return this;
    }


    /**
     * 下一个拦截器
     */
    private HttpProxyInterceptor<HttpObject, HttpObject> next(int index,
                                                              HttpProxyInterceptor<HttpObject, HttpObject> first,
                                                              HttpProxyInterceptor<HttpObject, HttpObject> last,
                                                              List<HttpProxyInterceptor<HttpObject, HttpObject>> interceptors) {
        if (index == -1) {
            return first;
        } else if (index < interceptors.size()) {
            return interceptors.get(index);
        } else if (index == interceptors.size()) {
            return last;
        }
        return null;
    }

}
