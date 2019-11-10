package com.wxl.proxy.admin.cmd;

import org.apache.commons.cli.Options;

/**
 * Create by wuxingle on 2019/10/28
 * 命令定义
 */
public interface AmdDefinition {

    /**
     * 命令名
     */
    String name();

    /**
     * 获取命令描述
     */
    String description();

    /**
     * 获取命令类型
     */
    Class<? extends Amd> type();


    /**
     * 是否支持有选项的构造器
     */
    boolean supportCmdline();


    /**
     * 命令选项
     */
    Options options();

    /**
     * 用法
     */
    String usage();


    /**
     * 是否必须参数
     */
    boolean requireArgs();

}
