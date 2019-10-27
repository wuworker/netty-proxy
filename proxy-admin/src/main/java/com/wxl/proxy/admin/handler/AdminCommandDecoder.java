package com.wxl.proxy.admin.handler;

import com.wxl.proxy.admin.cmd.AdminCommand;
import com.wxl.proxy.admin.cmd.AdminCommandParser;
import com.wxl.proxy.admin.cmd.impl.NoOpCommand;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Create by wuxingle on 2019/10/27
 * 管理员命令decoder
 */
@ChannelHandler.Sharable
public class AdminCommandDecoder extends MessageToMessageDecoder<String> {

    private AdminCommandParser parser;

    public AdminCommandDecoder(AdminCommandParser parser) {
        this.parser = parser;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        if (StringUtils.hasText(msg)) {
            AdminCommand cmd = parser.parse(msg);
            out.add(cmd);
        } else {
            out.add(new NoOpCommand());
        }
    }
}


