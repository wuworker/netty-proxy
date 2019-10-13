package com.wxl.proxy.log;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.wxl.proxy.log.MDCUtils.*;

/**
 * Create by wuxingle on 2019/8/18
 * channel handler增加log connectId
 */
@Slf4j
public abstract class ChannelHandlerLogEnhance {

    private ChannelHandlerLogEnhance() {
    }

    /**
     * 动态代理增强
     */
    @SuppressWarnings("unchecked")
    public static <T extends ChannelHandler> T proxy(ClassLoader loader, T handler) {
        return (T) Proxy.newProxyInstance(loader, new Class[]{
                ChannelInboundHandler.class,
                ChannelOutboundHandler.class
        }, new ChannelLogInvocationHandler(handler));
    }

    public static <T extends ChannelHandler> T proxy(T handler) {
        return proxy(ChannelHandlerLogEnhance.class.getClassLoader(), handler);
    }

    /**
     * 适配器增强
     */
    public static ChannelInboundHandler adapter(ChannelInboundHandler handler) {
        if (handler instanceof ChannelOutboundHandler) {
            return (ChannelInboundHandler) adapter((ChannelHandler) handler);
        }
        if (handler instanceof ChannelInboundLogAdapter) {
            return handler;
        }

        return new ChannelInboundLogAdapter(handler);
    }

    public static ChannelOutboundHandler adapter(ChannelOutboundHandler handler) {
        if (handler instanceof ChannelInboundHandler) {
            return (ChannelOutboundHandler) adapter((ChannelHandler) handler);
        }
        if (handler instanceof ChannelOutboundLogAdapter) {
            return handler;
        }

        return new ChannelOutboundLogAdapter(handler);
    }


    public static ChannelHandler adapter(ChannelHandler handler) {
        if (handler instanceof ChannelDuplexLogAdapter) {
            return handler;
        }

        if (handler instanceof ChannelInboundHandler && handler instanceof ChannelOutboundHandler) {
            return new ChannelDuplexLogAdapter(handler);
        }
        if (handler instanceof ChannelInboundHandler) {
            return adapter((ChannelInboundHandler) handler);
        }
        if (handler instanceof ChannelOutboundHandler) {
            return adapter((ChannelOutboundHandler) handler);
        }
        return new ChannelHandlerLogAdapter(handler);
    }

    /**
     * channel handler log adapter
     */
    private static class ChannelHandlerLogAdapter implements ChannelHandler {

        private final ChannelHandler handler;

        ChannelHandlerLogAdapter(ChannelHandler handler) {
            this.handler = handler;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            logConnectId(ctx, () -> handler.handlerAdded(ctx));
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            logConnectId(ctx, () -> handler.handlerRemoved(ctx));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logConnectId(ctx, () -> handler.exceptionCaught(ctx, cause));
        }
    }

    private static class ChannelInboundLogAdapter extends ChannelHandlerLogAdapter
            implements ChannelInboundHandler {

        private final ChannelInboundHandler handler;

        ChannelInboundLogAdapter(ChannelInboundHandler handler) {
            super(handler);
            this.handler = handler;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            logConnectId(ctx, () -> handler.channelRegistered(ctx));
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            logConnectId(ctx, () -> handler.channelUnregistered(ctx));
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            logConnectId(ctx, () -> handler.channelActive(ctx));
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            logConnectId(ctx, () -> handler.channelInactive(ctx));
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            logConnectId(ctx, () -> handler.channelRead(ctx, msg));
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            logConnectId(ctx, () -> handler.channelReadComplete(ctx));
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            logConnectId(ctx, () -> handler.userEventTriggered(ctx, evt));
        }

        @Override
        public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
            logConnectId(ctx, () -> handler.channelWritabilityChanged(ctx));
        }
    }

    private static class ChannelOutboundLogAdapter extends ChannelHandlerLogAdapter
            implements ChannelOutboundHandler {

        private final ChannelOutboundHandler handler;

        ChannelOutboundLogAdapter(ChannelOutboundHandler handler) {
            super(handler);
            this.handler = handler;
        }

        @Override
        public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            logConnectId(ctx, () -> handler.bind(ctx, localAddress, promise));
        }

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            logConnectId(ctx, () -> handler.connect(ctx, remoteAddress, localAddress, promise));
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            logConnectId(ctx, () -> handler.disconnect(ctx, promise));
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            logConnectId(ctx, () -> handler.close(ctx, promise));
        }

        @Override
        public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            logConnectId(ctx, () -> handler.deregister(ctx, promise));
        }

        @Override
        public void read(ChannelHandlerContext ctx) throws Exception {
            logConnectId(ctx, () -> handler.read(ctx));
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            logConnectId(ctx, () -> handler.write(ctx, msg, promise));
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            logConnectId(ctx, () -> handler.flush(ctx));
        }
    }

    private static class ChannelDuplexLogAdapter extends ChannelInboundLogAdapter
            implements ChannelOutboundHandler {

        private final ChannelOutboundHandler handler;

        ChannelDuplexLogAdapter(ChannelHandler handler) {
            super((ChannelInboundHandler) handler);
            this.handler = (ChannelOutboundHandler) handler;
        }

        @Override
        public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            logConnectId(ctx, () -> handler.bind(ctx, localAddress, promise));
        }

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            logConnectId(ctx, () -> handler.connect(ctx, remoteAddress, localAddress, promise));
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            logConnectId(ctx, () -> handler.disconnect(ctx, promise));
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            logConnectId(ctx, () -> handler.close(ctx, promise));
        }

        @Override
        public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            logConnectId(ctx, () -> handler.deregister(ctx, promise));
        }

        @Override
        public void read(ChannelHandlerContext ctx) throws Exception {
            logConnectId(ctx, () -> handler.read(ctx));
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            logConnectId(ctx, () -> handler.write(ctx, msg, promise));
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            logConnectId(ctx, () -> handler.flush(ctx));
        }
    }


    /**
     * 给ChannelHandler方法增加connectId
     */
    private static class ChannelLogInvocationHandler implements InvocationHandler {

        private static Set<String> channelMethods;

        private static Map<String, Set<String>> proxyMethodsCache = new ConcurrentHashMap<>();

        static {
            channelMethods = new HashSet<>(20);
            channelMethods.addAll(Arrays.stream(ChannelInboundHandler.class.getMethods())
                    .map(Method::getName).collect(Collectors.toList()));
            channelMethods.addAll(Arrays.stream(ChannelOutboundHandler.class.getMethods())
                    .map(Method::getName).collect(Collectors.toList()));
            channelMethods = Collections.unmodifiableSet(channelMethods);
        }

        private ChannelHandler channelHandler;

        private Set<String> proxyMethod;

        ChannelLogInvocationHandler(ChannelHandler channelHandler) {
            this.channelHandler = channelHandler;

            String className = channelHandler.getClass().getName();
            proxyMethod = proxyMethodsCache.get(className);
            if (proxyMethod == null) {
                proxyMethod = new HashSet<>();
                Method[] declaredMethods = channelHandler.getClass().getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    if (channelMethods.contains(declaredMethod.getName())) {
                        proxyMethod.add(declaredMethod.getName());
                    }
                }
                proxyMethodsCache.put(className, proxyMethod);
                log.debug("proxy log method is:{}", proxyMethod);
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (!proxyMethod.contains(method.getName())) {
                return method.invoke(channelHandler, args);
            }

            // 第一个参数必定是ChannelHandlerContext
            ChannelHandlerContext ctx = (ChannelHandlerContext) args[0];

            boolean flag = true;
            try {
                flag = putConnectId(ctx);
                return method.invoke(channelHandler, args);
            } finally {
                if (flag) {
                    removeConnectId();
                }
            }
        }

    }
}


