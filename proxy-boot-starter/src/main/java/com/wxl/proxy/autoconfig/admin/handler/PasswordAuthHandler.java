package com.wxl.proxy.autoconfig.admin.handler;

import com.wxl.proxy.ProxySystemConstants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;

/**
 * Create by wuxingle on 2019/11/16
 * 授权校验
 */
public class PasswordAuthHandler extends SimpleChannelInboundHandler<String> {

    private static final int MAX_ALLOW_ERROR_PWD_COUNT = 3;

    private static final String DEFAULT_PASSWORD_TIPS = "password: ";

    private static final String DEFAULT_PWD_ERROR_TIPS = "incorrect password";

    private int errorCount = 0;

    private String password;

    public PasswordAuthHandler(String password) {
        this.password = password;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(DEFAULT_PASSWORD_TIPS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (Objects.equals(password, msg)) {
            ctx.fireChannelActive();
            ctx.channel().pipeline().remove(PasswordAuthHandler.class.getName());
            return;
        }
        if (++errorCount >= MAX_ALLOW_ERROR_PWD_COUNT) {
            ctx.close();
        } else {
            ctx.write(DEFAULT_PWD_ERROR_TIPS);
            ctx.write(ProxySystemConstants.DEFAULT_LINE_SPLIT);
            ctx.writeAndFlush(DEFAULT_PASSWORD_TIPS);
        }
    }

}
