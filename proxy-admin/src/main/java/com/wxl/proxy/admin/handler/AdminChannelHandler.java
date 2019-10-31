package com.wxl.proxy.admin.handler;

import com.wxl.proxy.admin.SpringChannelInboundHandler;
import com.wxl.proxy.admin.cmd.*;
import com.wxl.proxy.admin.cmd.result.TableResult;
import com.wxl.proxy.server.ProxyServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

import static com.wxl.proxy.ProxySystemConstants.DEFAULT_LINE_SPLIT;

/**
 * Create by wuxingle on 2019/10/26
 * telnet命令处理
 */
@Slf4j
public class AdminChannelHandler extends SpringChannelInboundHandler {

    private static final String BANNER_MESSAGE = "===============================" + DEFAULT_LINE_SPLIT
            + "| Welcome to Use Proxy Server |" + DEFAULT_LINE_SPLIT
            + "===============================" + DEFAULT_LINE_SPLIT
            + "useful command is list:" + DEFAULT_LINE_SPLIT;

    private String tips;

    private AmdContext amdContext;

    public AdminChannelHandler() {
    }

    public AdminChannelHandler(String tips) {
        this.tips = tips;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel is active:{}", ctx.channel().remoteAddress());
        amdContext = new DefaultAmdContext(applicationContext);

        ctx.write(BANNER_MESSAGE);

        AmdRegistry amdRegistry = applicationContext.getBean(AmdRegistry.class);
        Map<String, AmdDefinition> definitions = amdRegistry.getAllDefinitions();

        TableResult result = new TableResult();
        result.setTitle("command", "description");
        for (Map.Entry<String, AmdDefinition> entry : definitions.entrySet()) {
            result.nextRow().addColumn(entry.getKey())
                    .addColumn(entry.getValue().description());
        }

        ctx.write(result.toString());
        ctx.write(DEFAULT_LINE_SPLIT);
        ctx.write(DEFAULT_LINE_SPLIT);
        ctx.write(tips);
        ctx.flush();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel is close:{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Amd cmd = (Amd) msg;

        AmdResult result = cmd.invoke(amdContext);

        String res = result.toString();
        if (StringUtils.hasText(res)) {
            ctx.write(res);
            ctx.write(DEFAULT_LINE_SPLIT);
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
            ctx.writeAndFlush(cause.getMessage() + DEFAULT_LINE_SPLIT);
        } else if (cause instanceof DecoderException) {
            Throwable realCause = cause.getCause();
            if (realCause instanceof AmdParseException) {
                ctx.writeAndFlush(realCause.getMessage() + DEFAULT_LINE_SPLIT);
            } else {
                ctx.writeAndFlush("cmdLine is illegal!" + DEFAULT_LINE_SPLIT);
            }
        } else if (cause instanceof AmdInvokeException) {
            ctx.writeAndFlush("server error!" + cause.getMessage() + DEFAULT_LINE_SPLIT);
        } else {
            log.error("'{}' handler cause exception",
                    ctx.channel().remoteAddress(), cause);
        }
    }

    static class DefaultAmdContext implements AmdContext {

        private AmdFormatter amdFormatter;

        private ApplicationContext applicationContext;

        public DefaultAmdContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
            this.amdFormatter = applicationContext.getBean(AmdFormatter.class);
        }

        @Override
        public Collection<ProxyServer> proxyServers() {
            Map<String, ProxyServer> beans = applicationContext.getBeansOfType(ProxyServer.class);
            return beans.values();
        }

        @Override
        public AmdFormatter formatter() {
            return amdFormatter;
        }
    }

}
