package com.wxl.proxy.admin.cmd;

/**
 * Create by wuxingle on 2019/10/26
 * 管理命令
 * admin command
 */
public interface Amd {

    /**
     * 命令执行
     */
    AmdResult invoke(AmdContext context) throws AmdInvokeException;

    /**
     * 命令名
     */
    String name();
}
