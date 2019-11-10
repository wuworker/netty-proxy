package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.AmdContext;
import com.wxl.proxy.admin.cmd.AmdInvokeException;
import com.wxl.proxy.admin.cmd.AmdResult;
import com.wxl.proxy.admin.cmd.annotation.Aommand;
import com.wxl.proxy.admin.cmd.result.EmptyResult;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.apache.commons.cli.CommandLine;

import static com.wxl.proxy.ProxySystemConstants.DEFAULT_LINE_SPLIT;

/**
 * Create by wuxingle on 2019/11/10
 * 离开命令
 */
@Aommand(name = "quit", desc = "Quit Proxy Admin", requireArgs = false)
public class QuitAmd extends AbstractAmd {

    public QuitAmd() {
    }

    @Override
    protected AmdResult invoke(CommandLine cmdline, AmdContext context) throws AmdInvokeException {
        Channel channel = context.channel();
        channel.writeAndFlush("good bye" + DEFAULT_LINE_SPLIT).addListener(ChannelFutureListener.CLOSE);
        return new EmptyResult();
    }
}

