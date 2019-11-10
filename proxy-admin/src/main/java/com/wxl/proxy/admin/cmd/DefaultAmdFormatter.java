package com.wxl.proxy.admin.cmd;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static com.wxl.proxy.ProxySystemConstants.DEFAULT_CHARSET;

/**
 * Create by wuxingle on 2019/10/26
 * 命令格式化
 */
public class DefaultAmdFormatter implements AmdFormatter {

    private HelpFormatter formatter;

    public DefaultAmdFormatter() {
        this(new HelpFormatter());
    }

    public DefaultAmdFormatter(HelpFormatter helpFormatter) {
        Assert.notNull(helpFormatter, "getAmdFormatter can not null!");
        this.formatter = helpFormatter;
    }

    @Order
    public String format(AmdDefinition definition) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Options options = definition.options();
        if (options == null) {
            options = new Options();
        }

        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(out, DEFAULT_CHARSET))) {
            formatter.printHelp(printWriter,
                    formatter.getWidth(),
                    definition.usage(),
                    "description: " + definition.description(),
                    options,
                    formatter.getLeftPadding(),
                    formatter.getDescPadding(),
                    null,
                    false);
        }
        byte[] bytes = out.toByteArray();
        return new String(bytes, DEFAULT_CHARSET);
    }
}
