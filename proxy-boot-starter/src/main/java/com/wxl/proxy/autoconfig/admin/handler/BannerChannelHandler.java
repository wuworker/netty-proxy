package com.wxl.proxy.autoconfig.admin.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.util.StringUtils;

import static com.wxl.proxy.ProxySystemConstants.DEFAULT_LINE_SPLIT;
import static com.wxl.proxy.ProxySystemConstants.PROXY_VERSION;

/**
 * Create by wuxingle on 2019/11/9
 * 打印banner
 */
@ChannelHandler.Sharable
public class BannerChannelHandler extends ChannelInboundHandlerAdapter {

    private String banner;

    public BannerChannelHandler(String banner) {
        this.banner = banner;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (StringUtils.hasText(banner)) {
            ctx.write(banner);
        }
        if (PROXY_VERSION != null) {
            ctx.write("Version: " + PROXY_VERSION + DEFAULT_LINE_SPLIT);
        }
        ctx.writeAndFlush(DEFAULT_LINE_SPLIT);
        ctx.fireChannelActive();
    }
}

