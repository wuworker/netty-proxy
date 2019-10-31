package com.wxl.proxy.admin.cmd.result;

import com.wxl.proxy.admin.cmd.AmdDefinition;
import com.wxl.proxy.admin.cmd.AmdFormatter;
import com.wxl.proxy.admin.cmd.AmdResult;
import com.wxl.proxy.admin.cmd.DefaultAmdFormatter;
import org.springframework.util.Assert;

/**
 * Create by wuxingle on 2019/10/27
 * 帮助结果
 */
public class HelpResult implements AmdResult {

    private String name;

    private AmdFormatter formatter;

    private AmdDefinition definition;

    public HelpResult(String name, AmdDefinition definition) {
        this(name, definition, new DefaultAmdFormatter());
    }

    public HelpResult(String name, AmdDefinition definition, AmdFormatter formatter) {
        Assert.notNull(definition, "command definition can not null");
        Assert.notNull(formatter, "formatter can not null");
        this.name = name;
        this.formatter = formatter;
        this.definition = definition;
    }

    @Override
    public String toString() {
        return formatter.format(name, definition).trim();
    }
}
