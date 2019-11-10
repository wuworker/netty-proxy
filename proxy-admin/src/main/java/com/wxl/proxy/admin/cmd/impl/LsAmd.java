package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.AmdContext;
import com.wxl.proxy.admin.cmd.AmdInvokeException;
import com.wxl.proxy.admin.cmd.AmdResult;
import com.wxl.proxy.admin.cmd.annotation.Aommand;
import com.wxl.proxy.admin.cmd.result.ListResult;
import com.wxl.proxy.admin.cmd.result.TableResult;
import com.wxl.proxy.admin.statistics.ProxyServerRegistry;
import com.wxl.proxy.server.ProxyServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2019/10/26
 * ls命令
 */
@Aommand(name = "ls", desc = "List All Proxy Server",
        optionsMethod = "getOptions", usage = "ls [-options]")
public class LsAmd extends AbstractAmd {

    private static Option h = Option.builder("h")
            .longOpt("help")
            .desc("help message")
            .build();

    private static Option l = Option.builder("l")
            .longOpt("list")
            .desc("list detail")
            .build();

    /**
     * 命令选项
     */
    public static Options getOptions() {
        Options options = new Options();
        options.addOption(h)
                .addOption(l);
        return options;
    }

    public LsAmd() {
    }

    public LsAmd(CommandLine cmdline) {
        super(cmdline);
    }

    /**
     * 命令执行
     */
    @Override
    public AmdResult invoke(CommandLine cmdline, AmdContext context) throws AmdInvokeException {
        ProxyServerRegistry proxyServerRegistry = context.getProxyServerRegistry();
        if (cmdline != null) {
            if (cmdline.hasOption("l")) {
                TableResult table = new TableResult();
                table.setTitle("name", "proxy-type", "listen-port", "type", "status");

                for (ProxyServer<?> proxyServer : proxyServerRegistry.getPermanent()) {
                    table.nextRow(5)
                            .addColumn(proxyServer.name())
                            .addColumn(proxyServer.getClass().getSimpleName())
                            .addColumn(proxyServer.bindPort())
                            .addColumn("permanent")
                            .addColumn(proxyServer.isRunning() ? "running" : "close");
                }

                for (ProxyServer<?> proxyServer : proxyServerRegistry.getTemporary()) {
                    table.nextRow(5)
                            .addColumn(proxyServer.name())
                            .addColumn(proxyServer.getClass().getSimpleName())
                            .addColumn(proxyServer.bindPort())
                            .addColumn("temporary")
                            .addColumn(proxyServer.isRunning() ? "running" : "close");
                }

                return table;
            }
        }

        List<String> collect = proxyServerRegistry.getRunning().stream()
                .map(ProxyServer::name)
                .collect(Collectors.toList());
        return new ListResult(collect);
    }
}
