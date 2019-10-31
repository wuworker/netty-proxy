package com.wxl.proxy.admin.cmd;

import com.wxl.proxy.admin.cmd.annotation.EmptyOptionsSupplier;
import org.apache.commons.cli.Options;
import org.springframework.util.Assert;

import java.util.function.Supplier;

/**
 * Create by wuxingle on 2019/10/30
 * 命令定义
 */
public class DefaultAmdDefinition implements AmdDefinition {

    private String description;

    private Class<? extends Amd> type;

    private Supplier<Options> options;

    private boolean supportCmdline;

    DefaultAmdDefinition(Class<? extends Amd> type) {
        this("", type, null, false);
    }

    DefaultAmdDefinition(String description, Class<? extends Amd> type,
                         Supplier<Options> options, boolean supportCmdline) {
        Assert.notNull(type, "amd class can not null");
        this.description = description == null ? "" : description;
        this.type = type;
        this.options = options;
        this.supportCmdline = supportCmdline;

        if (this.options == null) {
            this.options = new EmptyOptionsSupplier();
        }
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
    public Supplier<Options> options() {
        return options;
    }
}
