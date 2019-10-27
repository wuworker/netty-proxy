package com.wxl.proxy.admin.cmd.result;

import com.wxl.proxy.admin.cmd.AdminCommand;
import com.wxl.proxy.admin.cmd.AdminCommandFormatter;
import com.wxl.proxy.admin.cmd.AdminCommandResult;
import com.wxl.proxy.admin.cmd.DefaultAdminCommandFormatter;
import org.springframework.util.Assert;

/**
 * Create by wuxingle on 2019/10/27
 * 帮助结果
 */
public class HelpResult implements AdminCommandResult {

    private AdminCommandFormatter formatter;

    private AdminCommand command;

    public HelpResult(AdminCommand command) {
        this(new DefaultAdminCommandFormatter(), command);
    }

    public HelpResult(AdminCommandFormatter formatter, AdminCommand command) {
        Assert.notNull(formatter, "formatter can not null");
        Assert.notNull(command, "command can not null");
        this.formatter = formatter;
        this.command = command;
    }

    @Override
    public String format() {
        return formatter.format(command);
    }
}
