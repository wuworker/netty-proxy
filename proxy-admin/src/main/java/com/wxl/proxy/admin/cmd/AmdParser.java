package com.wxl.proxy.admin.cmd;

/**
 * Create by wuxingle on 2019/10/26
 * 命令解析
 */
public interface AmdParser {

    /**
     * 命令解析
     *
     * @param cmdLine like: ls -l
     */
    Amd parse(String cmdLine) throws AmdParseException;

}
