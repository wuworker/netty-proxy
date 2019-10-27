package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.AdminCommand;
import com.wxl.proxy.admin.cmd.AdminCommandResult;
import com.wxl.proxy.admin.cmd.CommandContext;
import com.wxl.proxy.admin.cmd.CommandInvokeException;
import com.wxl.proxy.admin.cmd.result.EmptyResult;

/**
 * Create by wuxingle on 2019/10/27
 * 无操作命令
 */
public class NoOpCommand implements AdminCommand {

    /**
     * 命令执行
     */
    @Override
    public AdminCommandResult invoke(CommandContext context) throws CommandInvokeException {
        return new EmptyResult();
    }

    /**
     * 命令名称
     */
    @Override
    public String name() {
        return "NoOp";
    }

    /**
     * 命令描述
     */
    @Override
    public String desc() {
        return "NoOp operation";
    }
}
