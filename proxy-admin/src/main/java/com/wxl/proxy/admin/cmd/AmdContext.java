package com.wxl.proxy.admin.cmd;

import com.wxl.proxy.admin.statistics.ProxyServerRegistry;
import io.netty.channel.Channel;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * Create by wuxingle on 2019/10/26
 * 命令执行上下文
 */
public interface AmdContext extends ListableBeanFactory {

    /**
     * 获取当前channel
     */
    Channel channel();

    /**
     * 代理服务注册中心
     */
    ProxyServerRegistry getProxyServerRegistry();

    /**
     * 命令格式工具
     */
    AmdFormatter getAmdFormatter();

    /**
     * 命令注册工厂
     */
    AmdRegistry getAmdRegistry();


    ApplicationContext getApplicationContext();
}
