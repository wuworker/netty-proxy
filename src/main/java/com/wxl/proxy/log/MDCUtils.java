package com.wxl.proxy.log;

import com.wxl.proxy.server.ProxyServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.internal.SystemPropertyUtil;
import org.slf4j.MDC;

/**
 * Create by wuxingle on 2019/8/26
 * log MDC
 */
public class MDCUtils {

    static boolean LOG_CONNECT_ID = SystemPropertyUtil.getBoolean(
            "proxy.log.connectId", true);

    private static final char CONNECT_SPLIT = '-';

    private static final String CONNECT_ID = "connectId";

    /**
     * 记录当前连接id到log
     */
    public static void logConnectId(ChannelHandlerContext ctx, CheckRunnable runnable)
            throws Exception {
        logConnectId(ctx.channel(), runnable);
    }

    public static void logConnectId(Channel channel, CheckRunnable runnable)
            throws Exception {
        if (LOG_CONNECT_ID) {
            try {
                putConnectId(channel);
                runnable.run();
            } finally {
                removeConnectId();
            }
        } else {
            runnable.run();
        }
    }

    static void putConnectId(ChannelHandlerContext ctx) {
        putConnectId(ctx.channel());
    }

    static void putConnectId(Channel channel) {
        Attribute<String> connectIdAttr = channel.attr(ProxyServer.ATTR_CONNECT_ID);
        String connectId = connectIdAttr.get();
        if (connectId == null) {
            StringBuilder sb = new StringBuilder();
            Attribute<String> nameAttr = channel.attr(ProxyServer.ATTR_PROXY_NAME);
            if (nameAttr.get() != null) {
                sb.append(nameAttr.get()).append(CONNECT_SPLIT);
            }
            Attribute<Channel> frontChannelAttr = channel.attr(ProxyServer.ATTR_FRONT_CHANNEL);
            if (frontChannelAttr.get() != null) {
                sb.append(frontChannelAttr.get().id().asShortText()).append(CONNECT_SPLIT);
            }
            sb.append(channel.id().asShortText());

            connectId = sb.toString();
            connectIdAttr.set(connectId);
        }

        MDC.put(CONNECT_ID, connectId);
    }

    static void removeConnectId() {
        MDC.remove(CONNECT_ID);
    }


    public interface CheckRunnable {
        void run() throws Exception;
    }

}


