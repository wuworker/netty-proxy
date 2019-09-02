package com.wxl.proxy.log;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.slf4j.MDC;

import static com.wxl.proxy.server.ProxyServer.*;

/**
 * Create by wuxingle on 2019/8/26
 * log MDC
 */
class MDCUtils {

    private static final char CONNECT_SPLIT = '-';

    private static final String CONNECT_ID = "connectId";

    static void logServerConnectId(String name, CheckRunnable runnable)
            throws Exception {
        try {
            MDC.put(CONNECT_ID, name);
            runnable.run();
        } finally {
            MDC.remove(CONNECT_ID);
        }
    }

    /**
     * 记录当前连接id到log
     */
    static void logConnectId(ChannelHandlerContext ctx, CheckRunnable runnable)
            throws Exception {
        logConnectId(ctx.channel(), runnable);
    }

    static void logConnectId(Channel channel, CheckRunnable runnable)
            throws Exception {
        boolean flag = true;
        try {
            flag = putConnectId(channel);
            runnable.run();
        } finally {
            if (flag) {
                removeConnectId();
            }
        }
    }

    static boolean putConnectId(ChannelHandlerContext ctx) {
        return putConnectId(ctx.channel());
    }

    /**
     * connectId组成
     * name-frontChannelId-backendChannelId
     */
    static boolean putConnectId(Channel channel) {
        String connectId = MDC.get(CONNECT_ID);
        if (connectId != null) {
            return false;
        }

        Attribute<String> connectIdAttr = channel.attr(ATTR_CONNECT_ID);
        connectId = connectIdAttr.get();
        if (connectId == null) {
            StringBuilder sb = new StringBuilder();
            //name
            Attribute<String> nameAttr = channel.attr(ATTR_PROXY_NAME);
            if (nameAttr.get() != null) {
                sb.append(nameAttr.get()).append(CONNECT_SPLIT);
            }

            //frontChannelId
            Attribute<Channel> frontChannelAttr = channel.attr(ATTR_FRONT_CHANNEL);
            if (frontChannelAttr.get() != null) {
                sb.append(frontChannelAttr.get().id().asShortText()).append(CONNECT_SPLIT);
            }

            connectId = sb.append(channel.id().asShortText()).toString();
            connectIdAttr.set(connectId);
        }

        MDC.put(CONNECT_ID, connectId);
        return true;
    }

    static void removeConnectId() {
        MDC.remove(CONNECT_ID);
    }


    interface CheckRunnable {
        void run() throws Exception;
    }

}


