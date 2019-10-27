package com.wxl.proxy.admin.cmd;

/**
 * Create by wuxingle on 2019/10/27
 * 命令执行结果
 */
public interface AdminCommandResult {


    String LINE_SEPARATOR = System.getProperties().getProperty("line.separator");

    /**
     * 结果格式化
     */
    String format();
}

