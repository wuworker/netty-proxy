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

    private AmdFormatter formatter;

    private AmdDefinition definition;

    public HelpResult(AmdDefinition definition) {
        this(definition, new DefaultAmdFormatter());
    }

    public HelpResult(AmdDefinition definition, AmdFormatter formatter) {
        Assert.notNull(definition, "command definition can not null");
        Assert.notNull(formatter, "getAmdFormatter can not null");
        this.formatter = formatter;
        this.definition = definition;
    }

    @Override
    public String toString() {
        return formatter.format(definition).trim();
    }
}
