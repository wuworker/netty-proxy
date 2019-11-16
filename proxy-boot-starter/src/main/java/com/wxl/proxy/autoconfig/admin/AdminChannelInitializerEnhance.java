package com.wxl.proxy.autoconfig.admin;

import com.wxl.proxy.admin.cmd.AmdParser;
import com.wxl.proxy.admin.handler.AdminChannelHandler;
import com.wxl.proxy.admin.handler.AdminChannelInitializer;
import com.wxl.proxy.admin.handler.AmdDecoder;
import com.wxl.proxy.autoconfig.admin.handler.BannerChannelHandler;
import com.wxl.proxy.autoconfig.admin.handler.PasswordAuthHandler;
import io.netty.channel.socket.SocketChannel;
import org.springframework.util.StringUtils;

/**
 * Create by wuxingle on 2019/11/9
 * channel initializer 加强
 */
class AdminChannelInitializerEnhance extends AdminChannelInitializer {

    private BannerChannelHandler bannerChannelHandler;

    private String password;

    AdminChannelInitializerEnhance(AmdParser amdParser, String banner, String password) {
        super(amdParser);
        this.bannerChannelHandler = new BannerChannelHandler(banner);
        this.password = password;
    }

    AdminChannelInitializerEnhance(String tips, AmdParser amdParser,
                                   String banner, String password) {
        super(tips, amdParser);
        this.bannerChannelHandler = new BannerChannelHandler(banner);
        this.password = password;
    }


    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        super.initChannel(ch);
        ch.pipeline().addBefore(AdminChannelHandler.class.getName(),
                BannerChannelHandler.class.getName(), bannerChannelHandler);
        if (StringUtils.hasText(password)) {
            ch.pipeline().addBefore(AmdDecoder.class.getName(),
                    PasswordAuthHandler.class.getName(), new PasswordAuthHandler(password));
        }
    }
}
