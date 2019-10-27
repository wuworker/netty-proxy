package com.wxl.proxy.admin;

import com.wxl.proxy.admin.handler.AdminChannelInitializer;
import com.wxl.proxy.log.ServerLoggingHandler;
import com.wxl.proxy.server.AbstractSimpleServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

/**
 * Create by wuxingle on 2019/10/26
 * 管理员telnet服务
 */
public class AdminTelnetServer extends AbstractSimpleServer
        implements SmartLifecycle, ApplicationContextAware {

    public static final String DEFAULT_SERVER_NAME = "admin-manager";

    static final AttributeKey<ApplicationContext> ATTR_SPRING_APPLICATION_CONTEXT =
            AttributeKey.valueOf("springApplicationContext");

    private ApplicationContext applicationContext;

    private AdminChannelInitializer channelInitializer;

    private String name;

    private int bindPort;

    public AdminTelnetServer(int bindPort, AdminChannelInitializer channelInitializer,
                             EventLoopGroup boosGroup, EventLoopGroup workGroup) {
        this(bindPort, channelInitializer, DEFAULT_SERVER_NAME, boosGroup, workGroup);
    }

    public AdminTelnetServer(int bindPort, AdminChannelInitializer channelInitializer, String name,
                             EventLoopGroup boosGroup, EventLoopGroup workGroup) {
        super(bindPort, boosGroup, workGroup);
        this.bindPort = bindPort;
        this.channelInitializer = channelInitializer;
        this.name = name;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int bindPort() {
        return bindPort;
    }

    @Override
    protected void configBootstrap(ServerBootstrap bootstrap) {
        bootstrap.childAttr(ATTR_SPRING_APPLICATION_CONTEXT, applicationContext);
    }

    /**
     * server channel handler 初始化
     */
    @Override
    protected void initServerChannel(ServerSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ServerLoggingHandler(name));
    }

    /**
     * client channel handler 初始化
     */
    @Override
    protected void initClientChannel(SocketChannel ch) throws Exception {
        channelInitializer.initChannel(ch);
    }
}
