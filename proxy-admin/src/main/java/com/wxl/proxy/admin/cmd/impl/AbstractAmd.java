package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.Amd;
import org.apache.commons.cli.CommandLine;

/**
 * Create by wuxingle on 2019/10/29
 * 管理命令
 */
public abstract class AbstractAmd implements Amd {

    private String name;

    private CommandLine cmdline;

    public AbstractAmd(String name) {
        this.name = name;
    }

    public AbstractAmd(String name, CommandLine cmdline) {
        this.name = name;
        this.cmdline = cmdline;
    }

    /**
     * 命令名
     */
    @Override
    public String name() {
        return name;
    }


    protected CommandLine commandLine() {
        return cmdline;
    }
}
