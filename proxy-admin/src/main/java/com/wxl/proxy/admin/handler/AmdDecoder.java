package com.wxl.proxy.admin.handler;

import com.wxl.proxy.admin.cmd.Amd;
import com.wxl.proxy.admin.cmd.AmdParser;
import com.wxl.proxy.admin.cmd.impl.NoOpAmd;
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
public class AmdDecoder extends MessageToMessageDecoder<String> {

    private AmdParser parser;

    public AmdDecoder(AmdParser parser) {
        this.parser = parser;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        if (StringUtils.hasText(msg)) {
            Amd cmd = parser.parse(msg);
            out.add(cmd);
        } else {
            out.add(new NoOpAmd());
        }
    }
}


