package com.wxl.proxy.admin.cmd;

/**
 * Create by wuxingle on 2019/10/26
 * 管理命令
 */
public interface AdminCommand {


    /**
     * 命令执行
     */
    AdminCommandResult invoke(CommandContext context) throws CommandInvokeException;

    /**
     * 命令名称
     */
    String name();


    /**
     * 命令描述
     */
    String desc();
}
