package com.wxl.proxy.admin.handler;

import com.wxl.proxy.admin.cmd.AmdParser;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.util.Assert;

import static com.wxl.proxy.ProxySystemConstants.DEFAULT_CHARSET;

/**
 * Create by wuxingle on 2019/10/27
 * admin command channel initializer
 */
public class AdminChannelInitializer {

    private static final String DEFAULT_TIPS = "> ";

    private static final int DEFAULT_CMD_MAX_LEN = 1024;

    private String tips;

    private StringDecoder decoder;

    private StringEncoder encoder;

    private AmdDecoder commandDecoder;

    public AdminChannelInitializer(AmdParser amdParser) {
        this(DEFAULT_TIPS, amdParser);
    }

    public AdminChannelInitializer(String tips, AmdParser amdParser) {
        Assert.notNull(amdParser, "parser can not null!");
        this.tips = tips;
        this.decoder = new StringDecoder(DEFAULT_CHARSET);
        this.encoder = new StringEncoder(DEFAULT_CHARSET);
        this.commandDecoder = new AmdDecoder(amdParser);
    }

    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(DelimiterBasedFrameDecoder.class.getName(),
                new DelimiterBasedFrameDecoder(DEFAULT_CMD_MAX_LEN, Delimiters.lineDelimiter()))
                .addLast(StringDecoder.class.getName(), decoder)
                .addLast(StringEncoder.class.getName(), encoder)
                .addLast(AmdDecoder.class.getName(), commandDecoder)
                .addLast(AdminChannelHandler.class.getName(), adminChannelHandler(tips));

    }


    protected ChannelHandler adminChannelHandler(String tips) {
        return new AdminChannelHandler(tips);
    }

}


