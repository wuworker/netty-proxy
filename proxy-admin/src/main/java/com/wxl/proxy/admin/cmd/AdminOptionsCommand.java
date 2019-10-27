package com.wxl.proxy.admin.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * Create by wuxingle on 2019/10/26
 * 有选项的命令
 */
public interface AdminOptionsCommand extends AdminCommand {

    /**
     * 命令选项
     */
    Options options();


    /**
     * 设置当前选项
     */
    void setCommandLine(CommandLine line);
}

