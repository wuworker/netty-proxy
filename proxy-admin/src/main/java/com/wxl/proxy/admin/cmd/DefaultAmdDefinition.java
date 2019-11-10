package com.wxl.proxy.admin.cmd;

import org.apache.commons.cli.Options;
import org.springframework.util.Assert;

/**
 * Create by wuxingle on 2019/10/30
 * 命令定义
 */
public class DefaultAmdDefinition implements AmdDefinition {

    private String name;

    private String description;

    private Class<? extends Amd> type;

    private Options options;

    private boolean supportCmdline;

    private String usage;

    private boolean requireArgs;

    DefaultAmdDefinition(String name, Class<? extends Amd> type) {
        this(name, "", type, null, false, "", false);
    }

    DefaultAmdDefinition(String name, String description, Class<? extends Amd> type,
                         Options options, boolean supportCmdline,
                         String usage, boolean requireArgs) {
        Assert.notNull(type, "amd class can not null");
        this.name = name;
        this.description = description == null ? "" : description;
        this.type = type;
        this.options = options;
        this.supportCmdline = supportCmdline;
        this.usage = usage;
        this.requireArgs = requireArgs;
    }

    /**
     * 命令名
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * 获取命令描述
     */
    @Override
    public String description() {
        return description;
    }

    /**
     * 获取命令类型
     */
    @Override
    public Class<? extends Amd> type() {
        return type;
    }

    /**
     * 是否支持有选项的构造器
     */
    @Override
    public boolean supportCmdline() {
        return supportCmdline;
    }

    /**
     * 命令选项
     */
    @Override
    public Options options() {
        return options;
    }

    /**
     * 用法
     */
    @Override
    public String usage() {
        return usage;
    }

    /**
     * 是否必须参数
     */
    @Override
    public boolean requireArgs() {
        return requireArgs;
    }
}
