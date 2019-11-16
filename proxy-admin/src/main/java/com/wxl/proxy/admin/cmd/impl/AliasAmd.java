package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.AmdContext;
import com.wxl.proxy.admin.cmd.AmdInvokeException;
import com.wxl.proxy.admin.cmd.AmdRegistry;
import com.wxl.proxy.admin.cmd.AmdResult;
import com.wxl.proxy.admin.cmd.annotation.Aommand;
import com.wxl.proxy.admin.cmd.result.EmptyResult;
import com.wxl.proxy.admin.cmd.result.ListResult;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by wuxingle on 2019/11/3
 * 给命令起别名
 */
@Aommand(name = "alias", desc = "List Or Add Alias Command",
        optionsMethod = "getOptions",
        usage = "alias [-options] [newCmd otherCmd]. like 'alias ll \"ls -l\"'")
public class AliasAmd extends AbstractAmd {

    private static Option h = Option.builder("h")
            .longOpt("help")
            .desc("help message")
            .build();

    private static Option r = Option.builder("r")
            .longOpt("remove")
            .desc("remove alias")
            .hasArg()
            .argName("newCmd")
            .optionalArg(false)
            .build();

    /**
     * 命令选项
     */
    public static Options getOptions() {
        Options options = new Options();
        options.addOption(h)
                .addOption(r);
        return options;
    }

    public AliasAmd() {
    }

    public AliasAmd(CommandLine cmdline) {
        super(cmdline);
    }

    @Override
    public AmdResult invoke(CommandLine cmdline, AmdContext context) throws AmdInvokeException {
        AmdRegistry registry = context.getAmdRegistry();
        if (cmdline != null) {
            if (cmdline.hasOption("r")) {
                String alias = cmdline.getOptionValue("r");
                registry.removeAlias(alias);
                return new EmptyResult();
            }
            String[] args = cmdline.getArgs();
            if (args.length != 0) {
                if (args.length != 2) {
                    throw new AmdInvokeException("'alias' must have two args:key value");
                }
                registry.registerAlias(args[1].replaceAll("[\"']", ""), args[0]);
                return new EmptyResult();
            }
        }

        List<String> allAlias = registry.getAllAlias();
        List<String> result = new ArrayList<>(allAlias.size());
        for (String alias : allAlias) {
            String name = registry.canonicalName(alias);
            result.add(alias + "=" + name);
        }
        return new ListResult(result);
    }
}



