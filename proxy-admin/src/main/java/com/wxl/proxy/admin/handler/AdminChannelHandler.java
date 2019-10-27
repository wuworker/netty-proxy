package com.wxl.proxy.admin.handler;

import com.wxl.proxy.admin.SpringChannelInboundHandler;
import com.wxl.proxy.admin.cmd.*;
import com.wxl.proxy.server.ProxyServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Create by wuxingle on 2019/10/26
 * telnet命令处理
 */
@Slf4j
public class AdminChannelHandler extends SpringChannelInboundHandler {

    private static final String HELLO_MESSAGE = "Welcome to Use Proxy Server!\n";

    private String tips;

    private CommandContext commandContext;

    public AdminChannelHandler() {
    }

    public AdminChannelHandler(String tips) {
        this.tips = tips;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel is active:{}", ctx.channel().remoteAddress());
        commandContext = new DefaultCommandContext(applicationContext);

        ctx.write(HELLO_MESSAGE);
        ctx.write(tips);
        ctx.flush();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel is close:{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        AdminCommand cmd = (AdminCommand) msg;
        AdminCommandResult result = cmd.invoke(commandContext);

        String res = result.format();
        if (StringUtils.hasText(res)) {
            ctx.write(res);
            if (!res.endsWith("\n")) {
                ctx.write("\n");
            }
        }

        if (StringUtils.hasText(tips)) {
            ctx.write(tips);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof TooLongFrameException) {
            ctx.writeAndFlush(cause.getMessage() + "\n");
        } else if (cause instanceof DecoderException) {
            Throwable realCause = cause.getCause();
            if (realCause instanceof CommandParseException) {
                ctx.writeAndFlush(realCause.getMessage() + "\n");
            } else {
                ctx.writeAndFlush("cmdLine is illegal!\n");
            }
        } else if (cause instanceof CommandInvokeException) {
            ctx.writeAndFlush("server error!" + cause.getMessage() + "\n");
        } else {
            log.error("'{}' handler cause exception",
                    ctx.channel().remoteAddress(), cause);
        }
    }

    static class DefaultCommandContext implements CommandContext {

        private ApplicationContext applicationContext;

        public DefaultCommandContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public Collection<ProxyServer> proxyServers() {
            Map<String, ProxyServer> beans = applicationContext.getBeansOfType(ProxyServer.class);
            return beans.values();
        }

        @Override
        public AdminCommandFormatter formatter() {
            return applicationContext.getBean(AdminCommandFormatter.class);
        }
    }

}
