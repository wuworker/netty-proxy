package com.wxl.proxy.http;

import com.wxl.proxy.handler.ProxyChannelInitializer;
import com.wxl.proxy.handler.ProxyFrontHandler;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptor;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorAdapter;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorInitializer;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorPipeline;
import com.wxl.proxy.http.proxy.SecondProxyHandlers;
import com.wxl.proxy.http.ssl.CertPool;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.resolver.NoopAddressResolverGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.wxl.proxy.server.ProxyServer.logHandler;
import static com.wxl.proxy.server.ProxyServer.logListener;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Create by wuxingle on 2019/9/1
 * http代理前置处理
 */
@Slf4j
public class HttpProxyFrontHandler extends ProxyFrontHandler<HttpProxyConfig> {


    private enum State {
        CREATE,

        HTTP_RUNNING,
        HTTPS_RUNNING,
        HTTPS_DECODE_RUNNING,

        DESTROY
    }

    private static final HttpResponseStatus HTTPS_TUNNEL_BUILD_SUCCESS =
            new HttpResponseStatus(200, "Connection established");

    private List<Object> bufMsg = new ArrayList<>(8);

    private String host;

    private int port = -1;

    private State state = State.CREATE;

    private HttpProxyInterceptorPipeline interceptorPipeline;

    private HttpProxyInterceptorInitializer interceptorInitializer;

    public HttpProxyFrontHandler(HttpProxyConfig config,
                                 ProxyChannelInitializer<SocketChannel, HttpProxyConfig> backendHandlerInitializer,
                                 HttpProxyInterceptorInitializer interceptorInitializer) {
        super(config, backendHandlerInitializer);
        this.interceptorInitializer = interceptorInitializer;
    }

    /**
     * 和真实服务连接的handler初始化
     */
    @Override
    protected void initChannelHandler(SocketChannel ch, Channel inboundChannel) throws Exception {
        HttpProxyBackendHandler backendHandler = new HttpProxyBackendHandler(config, host, port,
                inboundChannel, interceptorPipeline);
        ch.pipeline().addLast(HttpProxyBackendHandler.class.getName(), logHandler(backendHandler));

        // http解码
        if (state != State.HTTPS_RUNNING) {
            ch.pipeline().addFirst(HttpClientCodec.class.getName(), new HttpClientCodec());
        }

        // https握手
        if (state == State.HTTPS_DECODE_RUNNING) {
            SslContext clientSslContext = config.getSsl().getClientSslContext();
            SslHandler sslHandler = clientSslContext.newHandler(ch.alloc(), host, port);
            ch.pipeline().addFirst(SslHandler.class.getName(), sslHandler);
        }

        // 二级代理
        if (config.getSecondProxy() != null) {
            ch.pipeline().addFirst(ProxyHandler.class.getName(),
                    SecondProxyHandlers.newProxyHandler(config.getSecondProxy()));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
        log.debug("channel is active: '{}'", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("front channel is close: '{}:{}'", host, port);
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            if (request.decoderResult().isFailure()) {
                log.info("bad request:{}", request);
                ReferenceCountUtil.release(msg);
                ctx.channel().read();
                return;
            }

            log.info("{} {} {}", request.method(), request.uri(), request.protocolVersion());

            // 第一次请求建立远程连接
            if (state == State.CREATE) {
                initConnect(ctx, request);
                return;
            }
            // http转发
            interceptorPipeline.beforeRequest(ctx.channel(), outboundChannel, request);
        }
        // http转发
        else if (msg instanceof HttpContent) {
            if (state == State.CREATE) {
                log.warn("http proxy is not ready!");
                ReferenceCountUtil.release(msg);
                ctx.channel().read();
            } else if (state == State.HTTPS_RUNNING) {
                //must is last http content
                log.debug("https receive content:{}", msg);
                ReferenceCountUtil.release(msg);
                ctx.channel().read();
            } else if (((HttpContent) msg).decoderResult().isFailure()) {
                log.warn("bad request content:{}", msg);
                ReferenceCountUtil.release(msg);
                ctx.channel().read();
            } else {
                interceptorPipeline.beforeRequest(ctx.channel(), outboundChannel, (HttpContent) msg);
            }
        }
        // https转发
        else if (msg instanceof ByteBuf) {
            if (state == State.CREATE) {
                ReferenceCountUtil.release(msg);
                throw new IllegalStateException("server error, maybe not add handler 'HttpServerCodec' first!");
            }
            // https解码
            if (config.getSsl() != null) {
                ByteBuf data = (ByteBuf) msg;
                // ssl握手
                if (data.getByte(0) == 22) {
                    SslContext sslCtx = SslContextBuilder
                            .forServer(config.getSsl().getServerPriKey(), CertPool.getCert(this.host, config.getSsl()))
                            .build();
                    ctx.pipeline().addFirst(HttpServerCodec.class.getName(), new HttpServerCodec());
                    ctx.pipeline().addFirst(SslHandler.class.getName(), sslCtx.newHandler(ctx.alloc()));
                    // 重新过一遍pipeline，拿到解密后的的http报文
                    ctx.pipeline().fireChannelRead(msg);
                    return;
                }
            }

            forwardData(ctx.channel(), msg);
        }
        // unknown msg
        else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("will close front connect '{}:{}', front handler cause exception",
                host, port, cause);
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 初始化连接
     * 初始化实例变量
     * host,post,isHttps,outboundChannel,interceptorPipeline
     */
    private void initConnect(ChannelHandlerContext ctx, HttpRequest request) {
        // 获取真实服务器的host,port失败
        if (!initHostPort(request)) {
            log.warn("bad request, host,port is illegal! {}:{}", host, port);
            ctx.channel().close();
            return;
        }

        // 修改state
        if (HttpMethod.CONNECT.equals(request.method())) {
            if (config.getSsl() != null) {
                state = State.HTTPS_DECODE_RUNNING;
            } else {
                state = State.HTTPS_RUNNING;
            }
        } else {
            state = State.HTTP_RUNNING;
        }

        // 需要拦截http报文的情况
        if (state != State.HTTPS_RUNNING) {
            interceptorPipeline = buildHttpPipeline();
        }

        // http代理
        if (state == State.HTTP_RUNNING) {
            doConnect(ctx.channel(), logListener(f -> {
                // 远程连接成功
                if (f.isSuccess()) {
                    log.info("remote {}:{} connect success!", host, port);

                    interceptorPipeline.beforeRequest(ctx.channel(), f.channel(), request);
                    if (!bufMsg.isEmpty()) {
                        for (Object buf : bufMsg) {
                            log.debug("forward buffer data:{}", buf);
                            interceptorPipeline.beforeRequest(ctx.channel(), f.channel(), (HttpObject) buf);
                        }
                        bufMsg.clear();
                    }
                } else {
                    log.warn("http connect fail! {}:{} by {}", host, port, f.cause());
                    ctx.channel().close();
                }
            }));
        }
        // https代理
        else {
            ReferenceCountUtil.release(request);
            doConnect(ctx.channel(), logListener(f -> {
                // 远程连接成功
                if (f.isSuccess()) {
                    log.info("remote {}:{} connect success!", host, port);
                    ctx.writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, HTTPS_TUNNEL_BUILD_SUCCESS))
                            .addListener(logListener(f2 -> {
                                if (f2.isSuccess()) {
                                    log.debug("https tunnel build success");
                                    // https不解析
                                    f2.channel().pipeline().remove(HttpServerCodec.class.getName());
                                    f2.channel().read();
                                } else {
                                    log.error("https tunnel build fail(write response fail)", f2.cause());
                                    f2.channel().close();
                                }
                            }));
                } else {
                    Throwable cause = f.cause();
                    log.warn("https connect fail! {}:{} by:{}", host, port, cause.getMessage());
                    HttpResponse response;
                    if (cause instanceof ConnectTimeoutException) {
                        response = new DefaultFullHttpResponse(HTTP_1_1, GATEWAY_TIMEOUT);
                    } else {
                        response = new DefaultFullHttpResponse(HTTP_1_1, BAD_GATEWAY);
                    }
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                }
            }));
        }
    }


    /**
     * 建立远程连接
     */
    private void doConnect(Channel inboundChannel, GenericFutureListener<? extends Future<? super Void>> connectListener) {
        Bootstrap bootstrap = buildClientBootstrap(inboundChannel);
        if (config.getSecondProxy() != null) {
            // 交给二级代理解析域名
            bootstrap.resolver(NoopAddressResolverGroup.INSTANCE);
        }
        ChannelFuture future = bootstrap.connect(host, port);

        outboundChannel = future.channel();
        if (connectListener != null) {
            future.addListener(connectListener);
        }
    }

    /**
     * 数据转发
     */
    private void forwardData(Channel inboundChannel, Object msg) {
        if (outboundChannel != null && outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(msg)
                    .addListener(logListener(f -> {
                        if (f.isSuccess()) {
                            inboundChannel.read();
                        } else {
                            log.error("backend write fail", f.cause());
                            f.channel().close();
                        }
                    }));
        }
        //outboundChannel未连接完成时,消息缓存
        else {
            bufMsg.add(msg);
            log.debug("buffer size is:{}", bufMsg.size());
        }
    }

    /**
     * http拦截器pipeline
     */
    private HttpProxyInterceptorPipeline buildHttpPipeline() {
        HttpProxyInterceptor<HttpObject, HttpObject> tail = new HttpProxyInterceptorAdapter() {
            @Override
            public void beforeRequest(Channel inboundChannel, Channel outboundChannel, HttpObject request, HttpProxyInterceptorPipeline pipeline) {
                forwardData(inboundChannel, request);
            }
        };

        HttpProxyInterceptor<HttpObject, HttpObject> head = new HttpProxyInterceptorAdapter() {
            @Override
            public void beforeRequest(Channel inboundChannel, Channel outboundChannel, HttpObject request, HttpProxyInterceptorPipeline pipeline) throws Exception {
                //http代理 uri里包含整个地址
                if (state == State.HTTP_RUNNING && (request instanceof HttpRequest)) {
                    // http代理修改uri
                    HttpRequest httpRequest = (HttpRequest) request;
                    log.debug("current uri is:{} --> {}", httpRequest.uri(), new URL(httpRequest.uri()).getFile());
                    httpRequest.setUri(new URL(httpRequest.uri()).getFile());
                }

                // next
                pipeline.beforeRequest(inboundChannel, outboundChannel, request);
            }

            @Override
            public void afterResponse(Channel inboundChannel, Channel outboundChannel, HttpObject response, HttpProxyInterceptorPipeline pipeline) {
                inboundChannel.writeAndFlush(response).addListener(logListener(f -> {
                    if (f.isSuccess()) {
                        if (response instanceof HttpResponse) {
                            HttpResponse httpResponse = ((HttpResponse) response);
                            // 协议升级
                            if (httpResponse.status() == SWITCHING_PROTOCOLS) {
                                String upgrade = httpResponse.headers().get(HttpHeaderNames.UPGRADE);
                                log.debug("switch protocols to: {}", upgrade);

                                if (state == State.HTTPS_DECODE_RUNNING) {
                                    outboundChannel.pipeline().remove(SslHandler.class.getName());
                                }

                                state = State.HTTPS_RUNNING;
                                inboundChannel.pipeline().remove(HttpServerCodec.class.getName());
                                outboundChannel.pipeline().remove(HttpClientCodec.class.getName());
                            }
                        }

                        outboundChannel.read();
                    } else {
                        log.error("inbound write fail", f.cause());
                        f.channel().close();
                    }
                }));
            }
        };

        HttpProxyInterceptorPipeline pipeline = new HttpProxyInterceptorPipeline(head, tail);
        if (interceptorInitializer != null) {
            interceptorInitializer.init(pipeline);
        }
        return pipeline;
    }

    /**
     * 初始化需要代理的host,port
     */
    private boolean initHostPort(HttpRequest request) {
        String hostStr = request.headers().get(HttpHeaderNames.HOST);
        if (StringUtils.hasText(hostStr)) {
            int i;
            if ((i = hostStr.lastIndexOf(":")) > 0) {
                host = hostStr.substring(0, i);
                port = Integer.parseInt(hostStr.substring(i + 1));
            } else {
                host = hostStr;
            }
        }
        //header里没有Host，拿uri
        if (port == -1) {
            URI uri = URI.create(request.uri());
            port = uri.getPort();
            if (!StringUtils.hasText(host)) {
                host = uri.getHost();
            }
            if (port == -1) {
                if ("http".equals(uri.getScheme())) {
                    port = 80;
                } else if ("https".equals(uri.getScheme())) {
                    port = 443;
                }
            }
        }
        return StringUtils.hasText(host) && port != -1;
    }

}
