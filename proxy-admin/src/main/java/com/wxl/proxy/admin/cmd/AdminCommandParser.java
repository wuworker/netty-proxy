package com.wxl.proxy.admin.cmd;

/**
 * Create by wuxingle on 2019/10/26
 * 命令解析
 */
public interface AdminCommandParser {

    /**
     * 命令解析
     *
     * @param cmdLine like: ls -l
     */
    AdminCommand parse(String cmdLine) throws CommandParseException;

}
