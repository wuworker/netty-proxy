package com.wxl.proxy.admin.cmd;

/**
 * Create by wuxingle on 2019/10/26
 * 命令格式化
 */
public interface AmdFormatter {

    /**
     * 格式化命令
     */
    String format(String name, AmdDefinition definition);
}

