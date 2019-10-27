package com.wxl.proxy.admin;

import org.apache.commons.cli.*;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * Create by wuxingle on 2019/10/26
 */
public class CmdTest {

    @Test
    public void test1() throws Exception {

        Options options = new Options();

        Option c = Option.builder("c")
                .desc("统计字符")
                .build();

        Option h = Option.builder("h")
                .longOpt("help")
                .desc("显示帮助")
                .build();

        Option o = Option.builder("o")
                .hasArg()
                .argName("输出文件名")
                .desc("输出文件")
                .build();

        options.addOption(c)
                .addOption(h)
                .addOption(o);

        OptionGroup group = new OptionGroup();
        group.addOption(new Option("t", false, "test"));
        group.addOption(new Option("e", false, "eeee"));
        options.addOptionGroup(group);

        HelpFormatter formatter = new HelpFormatter();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter printWriter = new PrintWriter(out)) {
            formatter.printHelp(printWriter,
                    formatter.getWidth(),
                    "wc",
                    "header",
                    options,
                    formatter.getLeftPadding(),
                    formatter.getDescPadding(),
                    "foot",
                    true);
        }

        System.out.println(new String(out.toByteArray()));

        DefaultParser defaultParser = new DefaultParser();

        CommandLine cmd = defaultParser.parse(options, new String[]{
                "-c", "-o", "ahahah.txt"
        });
        if (cmd.hasOption("c")) {
            System.out.println("cccc");
        }
        if (cmd.hasOption("o")) {
            System.out.println(cmd.getArgList());
            System.out.println(cmd.getOptionValue("o"));
        }

    }

    @Test
    public void test2() {
        String property = System.getProperties().getProperty("line.separator");
        System.out.println("\n".equalsIgnoreCase(property));
        System.out.println("\r".equalsIgnoreCase(property));
        System.out.println("\r\n".equalsIgnoreCase(property));
    }
}
