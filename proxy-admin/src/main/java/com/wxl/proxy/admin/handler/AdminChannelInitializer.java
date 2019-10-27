package com.wxl.proxy.admin.handler;

import com.wxl.proxy.admin.cmd.AdminCommandParser;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

/**
 * Create by wuxingle on 2019/10/27
 * admin command channel initializer
 */
public class AdminChannelInitializer {

    private static final String DEFAULT_TIPES = "> ";

    private static final int DEFAULT_CMD_MAX_LEN = 1024;

    private int cmdMaxLen;

    private String tips;

    private StringDecoder decoder;

    private StringEncoder encoder;

    private AdminCommandDecoder commandDecoder;

    public AdminChannelInitializer(AdminCommandParser commandParser) {
        this(DEFAULT_CMD_MAX_LEN, "> ", commandParser);
    }

    public AdminChannelInitializer(int cmdMaxLen, String tips,
                                   AdminCommandParser commandParser) {
        Assert.isTrue(cmdMaxLen > 0, "cmd maxLen must is > 0!");
        Assert.notNull(commandParser, "parser can not null!");
        this.cmdMaxLen = cmdMaxLen;
        this.tips = tips;
        this.decoder = new StringDecoder(StandardCharsets.UTF_8);
        this.encoder = new StringEncoder(StandardCharsets.UTF_8);
        this.commandDecoder = new AdminCommandDecoder(commandParser);
    }

    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(cmdMaxLen, Delimiters.lineDelimiter()))
                .addLast(decoder)
                .addLast(commandDecoder)
                .addLast(encoder)
                .addLast(adminChannelHandler(tips));

    }


    protected ChannelHandler adminChannelHandler(String tips) {
        return new AdminChannelHandler(tips);
    }

}


