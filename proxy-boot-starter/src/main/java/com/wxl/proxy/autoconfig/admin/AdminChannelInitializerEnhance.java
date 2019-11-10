package com.wxl.proxy.autoconfig.admin;

import com.wxl.proxy.admin.cmd.AmdParser;
import com.wxl.proxy.admin.handler.AdminChannelHandler;
import com.wxl.proxy.admin.handler.AdminChannelInitializer;
import com.wxl.proxy.autoconfig.admin.handler.BannerChannelHandler;
import io.netty.channel.socket.SocketChannel;

/**
 * Create by wuxingle on 2019/11/9
 * channel initializer 加强
 */
public class AdminChannelInitializerEnhance extends AdminChannelInitializer {

    private BannerChannelHandler bannerChannelHandler;

    public AdminChannelInitializerEnhance(AmdParser amdParser, String banner) {
        super(amdParser);
        this.bannerChannelHandler = new BannerChannelHandler(banner);
    }

    public AdminChannelInitializerEnhance(int cmdMaxLen, String tips, AmdParser amdParser, String banner) {
        super(cmdMaxLen, tips, amdParser);
        this.bannerChannelHandler = new BannerChannelHandler(banner);
    }


    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        super.initChannel(ch);
        ch.pipeline().addBefore(AdminChannelHandler.class.getName(),
                BannerChannelHandler.class.getName(), bannerChannelHandler);
    }
}
