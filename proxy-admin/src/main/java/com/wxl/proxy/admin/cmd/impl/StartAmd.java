package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.AmdContext;
import com.wxl.proxy.admin.cmd.AmdInvokeException;
import com.wxl.proxy.admin.cmd.AmdResult;
import com.wxl.proxy.admin.cmd.annotation.Aommand;
import com.wxl.proxy.admin.cmd.result.EmptyResult;
import com.wxl.proxy.admin.statistics.ProxyServerRegistry;
import com.wxl.proxy.server.ProxyServer;
import org.apache.commons.cli.CommandLine;

/**
 * Create by wuxingle on 2019/11/3
 * 启动代理
 */
@Aommand(name = "start", desc = "Start Proxy Server",
        requireArgs = true, usage = "start proxyName...")
public class StartAmd extends AbstractAmd {

    public StartAmd() {
        super();
    }

    public StartAmd(CommandLine cmdline) {
        super(cmdline);
    }


    @Override
    protected AmdResult invoke(CommandLine cmdline, AmdContext context) throws AmdInvokeException {
        if (cmdline == null) {
            throw new AmdInvokeException("'start' must has cmdLine");
        }
        String[] args = cmdline.getArgs();
        if (args.length == 0) {
            throw new AmdInvokeException("'start' must has one args,usage: start proxyName");
        }
        ProxyServerRegistry proxyServerRegistry = context.getProxyServerRegistry();
        for (String name : args) {
            ProxyServer<?> proxyServer = proxyServerRegistry.getOptional(name).orElseThrow(
                    () -> new AmdInvokeException("proxy server is not exist:" + name));
            synchronized (proxyServer) {
                if (proxyServer.isRunning()) {
                    throw new AmdInvokeException("proxy server is already running:" + name);
                }
                proxyServer.start();
            }
        }
        return new EmptyResult();
    }
}
