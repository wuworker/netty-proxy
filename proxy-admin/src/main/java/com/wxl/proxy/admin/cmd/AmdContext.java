package com.wxl.proxy.admin.cmd;

import com.wxl.proxy.server.ProxyServer;

import java.util.Collection;

/**
 * Create by wuxingle on 2019/10/26
 * 命令执行上下文
 */
public interface AmdContext {

    /**
     * 代理服务列表
     */
    Collection<ProxyServer> proxyServers();

    /**
     * 命令格式工具
     */
    AmdFormatter formatter();
}
