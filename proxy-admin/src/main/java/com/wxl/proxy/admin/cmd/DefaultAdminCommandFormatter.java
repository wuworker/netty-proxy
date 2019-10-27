package com.wxl.proxy.admin.cmd;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * Create by wuxingle on 2019/10/26
 * 命令格式化
 */
public class DefaultAdminCommandFormatter implements AdminCommandFormatter {

    private HelpFormatter formatter;

    public DefaultAdminCommandFormatter() {
        this(new HelpFormatter());
    }

    public DefaultAdminCommandFormatter(HelpFormatter helpFormatter) {
        Assert.notNull(helpFormatter, "formatter can not null!");
        this.formatter = helpFormatter;
    }

    @Order
    public String format(AdminCommand cmd, Charset charset) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Options options = cmd instanceof AdminOptionsCommand ?
                ((AdminOptionsCommand) cmd).options()
                : new Options();

        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(out, charset))) {
            formatter.printHelp(printWriter,
                    formatter.getWidth(),
                    cmd.name(),
                    cmd.desc(),
                    options,
                    formatter.getLeftPadding(),
                    formatter.getDescPadding(),
                    null,
                    true);
        }
        byte[] bytes = out.toByteArray();
        return new String(bytes, charset);
    }
}
