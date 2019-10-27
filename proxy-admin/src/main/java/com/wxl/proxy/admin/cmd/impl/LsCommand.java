package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.AdminCommandFormatter;
import com.wxl.proxy.admin.cmd.AdminCommandResult;
import com.wxl.proxy.admin.cmd.CommandContext;
import com.wxl.proxy.admin.cmd.CommandInvokeException;
import com.wxl.proxy.admin.cmd.result.HelpResult;
import com.wxl.proxy.admin.cmd.result.ListResult;
import com.wxl.proxy.admin.cmd.result.TableResult;
import com.wxl.proxy.server.ProxyServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2019/10/26
 * ls命令
 */
public class LsCommand extends AbstractOptionsCommand {

    public static final String COMMAND_NAME = "ls";

    private static final String COMMAND_DESC = "List All Proxy Server Name";


    static {
        Option l = Option.builder("l")
                .longOpt("list")
                .desc("list detail")
                .build();

        Option h = Option.builder("h")
                .longOpt("help")
                .desc("help message")
                .build();

        options.addOption(l);
        options.addOption(h);
    }

    public LsCommand() {
    }

    public LsCommand(CommandLine commandLine) {
        super(commandLine);
    }

    /**
     * 命令执行
     */
    @Override
    public AdminCommandResult invoke(CommandContext context) throws CommandInvokeException {
        if (commandLine != null) {
            if (commandLine.hasOption("h")) {
                AdminCommandFormatter formatter = context.formatter();
                return new HelpResult(formatter, this);
            }
            if (commandLine.hasOption("l")) {
                Collection<ProxyServer> proxyServers = context.proxyServers();
                TableResult table = new TableResult();
                table.setTitle("name", "type", "listenPort", "startTime");

                for (ProxyServer<?> proxyServer : proxyServers) {
                    table.nextRow(4)
                            .addColumn(proxyServer.name())
                            .addColumn(proxyServer.getClass().getSimpleName())
                            .addColumn(String.valueOf(proxyServer.getConfig().getBindPort()))
                            .addColumn(new Date().toString());
                }

                return table;
            }
        }

        List<String> collect = context.proxyServers().stream()
                .map(ProxyServer::name)
                .collect(Collectors.toList());
        return new ListResult(collect);
    }

    /**
     * 命令名称
     */
    @Override
    public String name() {
        return COMMAND_NAME;
    }

    /**
     * 命令描述
     */
    @Override
    public String desc() {
        return COMMAND_DESC;
    }
}
