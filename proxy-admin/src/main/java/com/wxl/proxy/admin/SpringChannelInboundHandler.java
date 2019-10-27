package com.wxl.proxy.admin;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import org.springframework.context.ApplicationContext;

/**
 * Create by wuxingle on 2019/10/27
 * 可以获取spring的ApplicationContext
 */
public class SpringChannelInboundHandler extends ChannelInboundHandlerAdapter {

    protected ApplicationContext applicationContext;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Attribute<ApplicationContext> attr = ctx.channel().attr(AdminTelnetServer.ATTR_SPRING_APPLICATION_CONTEXT);
        applicationContext = attr.get();
        if (applicationContext == null) {
            throw new IllegalStateException("server error! admin channel handler can not get spring ApplicationContext!");
        }
        super.channelRegistered(ctx);
    }


}
