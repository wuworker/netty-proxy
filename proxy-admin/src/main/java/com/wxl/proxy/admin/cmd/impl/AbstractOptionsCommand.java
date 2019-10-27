package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.AdminOptionsCommand;
import lombok.Getter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * Create by wuxingle on 2019/10/26
 * 有选项的命令
 */
public abstract class AbstractOptionsCommand implements AdminOptionsCommand {

    protected static Options options = new Options();

    @Getter
    protected CommandLine commandLine;

    public AbstractOptionsCommand() {
    }

    public AbstractOptionsCommand(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    /**
     * 命令选项
     */
    @Override
    public final Options options() {
        return options;
    }

    /**
     * 设置当前选项
     */
    @Override
    public void setCommandLine(CommandLine line) {
        this.commandLine = line;
    }
}
