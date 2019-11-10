package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.*;
import com.wxl.proxy.admin.cmd.result.HelpResult;
import org.apache.commons.cli.CommandLine;

/**
 * Create by wuxingle on 2019/10/29
 * 自带help的管理命令
 */
public abstract class AbstractAmd implements Amd {

    private CommandLine cmdline;

    protected AbstractAmd() {
    }

    protected AbstractAmd(CommandLine cmdline) {
        this.cmdline = cmdline;
    }

    @Override
    public final AmdResult invoke(AmdContext context) throws AmdInvokeException {
        if (cmdline != null && cmdline.hasOption("h")) {
            AmdDefinition definition = AmdDefinitionBuilder.of(getClass());
            AmdFormatter formatter = context.getAmdFormatter();
            return new HelpResult(definition, formatter);
        }
        try {
            return invoke(cmdline, context);
        } catch (RuntimeException e) {
            throw new AmdInvokeException(e.getMessage(), e);
        }
    }


    protected abstract AmdResult invoke(CommandLine cmdline, AmdContext context) throws AmdInvokeException;
}

