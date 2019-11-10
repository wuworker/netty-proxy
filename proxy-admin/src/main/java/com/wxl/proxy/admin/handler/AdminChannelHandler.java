package com.wxl.proxy.admin.handler;

import com.wxl.proxy.admin.SpringChannelInboundHandler;
import com.wxl.proxy.admin.cmd.*;
import com.wxl.proxy.admin.cmd.result.TableResult;
import com.wxl.proxy.admin.statistics.ProxyServerRegistry;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Map;

import static com.wxl.proxy.ProxySystemConstants.DEFAULT_LINE_SPLIT;

/**
 * Create by wuxingle on 2019/10/26
 * telnet命令处理
 */
@Slf4j
public class AdminChannelHandler extends SpringChannelInboundHandler {

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
        amdContext = new DefaultAmdContext(ctx.channel(), applicationContext);

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
            ctx.write(cause.getMessage() + DEFAULT_LINE_SPLIT);
        } else if (cause instanceof DecoderException) {
            Throwable realCause = cause.getCause();
            if (realCause instanceof AmdParseException) {
                ctx.write(realCause.getMessage() + DEFAULT_LINE_SPLIT);
            } else {
                ctx.write("cmdLine is illegal!" + DEFAULT_LINE_SPLIT);
            }
        } else if (cause instanceof AmdInvokeException) {
            ctx.write(cause.getMessage() + DEFAULT_LINE_SPLIT);
        } else {
            log.error("'{}' handler cause exception",
                    ctx.channel().remoteAddress(), cause);
            ctx.write("server error!" + cause.getMessage() + DEFAULT_LINE_SPLIT);
        }
        if (StringUtils.hasText(tips)) {
            ctx.write(tips);
        }
    }

    static class DefaultAmdContext extends AbstractAmdContext {

        private Channel channel;

        public DefaultAmdContext(Channel channel, ApplicationContext applicationContext) {
            super(applicationContext);
            this.channel = channel;
        }

        @Override
        public Channel channel() {
            return channel;
        }

        @Override
        public ProxyServerRegistry getProxyServerRegistry() {
            return getApplicationContext().getBean(ProxyServerRegistry.class);
        }

        @Override
        public AmdFormatter getAmdFormatter() {
            return getApplicationContext().getBean(AmdFormatter.class);
        }

        @Override
        public AmdRegistry getAmdRegistry() {
            return getApplicationContext().getBean(AmdRegistry.class);
        }
    }

}
