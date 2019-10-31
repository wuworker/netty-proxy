package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.*;
import com.wxl.proxy.admin.cmd.annotation.Aommand;
import com.wxl.proxy.admin.cmd.annotation.OptionsSupplier;
import com.wxl.proxy.admin.cmd.result.HelpResult;
import com.wxl.proxy.admin.cmd.result.ListResult;
import com.wxl.proxy.admin.cmd.result.TableResult;
import com.wxl.proxy.server.ProxyServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2019/10/26
 * ls命令
 */
@Aommand(name = "ls", desc = "List All Proxy Server Name", options = LsAmd.LsOptions.class)
public class LsAmd extends AbstractAmd {

    public static class LsOptions extends OptionsSupplier {

        private static Option h = Option.builder("h")
                .longOpt("help")
                .desc("help message")
                .build();

        private static Option l = Option.builder("l")
                .longOpt("list")
                .desc("list detail")
                .build();

        public LsOptions() {
            options.addOption(h)
                    .addOption(l);
        }
    }

    public LsAmd(String name, CommandLine cmdline) {
        super(name, cmdline);
    }

    /**
     * 命令执行
     */
    @Override
    public AmdResult invoke(AmdContext context) throws AmdInvokeException {
        CommandLine cmdline = commandLine();
        if (cmdline != null) {
            if (cmdline.hasOption("h")) {
                AmdDefinition definition = AmdDefinitionBuilder.of(LsAmd.class);
                AmdFormatter formatter = context.formatter();
                return new HelpResult(name(), definition, formatter);
            }
            if (cmdline.hasOption("l")) {
                Collection<ProxyServer> proxyServers = context.proxyServers();
                TableResult table = new TableResult();
                table.setTitle("name", "type", "listenPort");

                for (ProxyServer<?> proxyServer : proxyServers) {
                    table.nextRow(3)
                            .addColumn(proxyServer.name())
                            .addColumn(proxyServer.getClass().getSimpleName())
                            .addColumn(String.valueOf(proxyServer.getConfig().getBindPort()));
                }

                return table;
            }
        }

        List<String> collect = context.proxyServers().stream()
                .map(ProxyServer::name)
                .collect(Collectors.toList());
        return new ListResult(collect);
    }
}
