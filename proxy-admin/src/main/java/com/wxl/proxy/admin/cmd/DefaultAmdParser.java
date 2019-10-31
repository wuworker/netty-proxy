package com.wxl.proxy.admin.cmd;

import org.apache.commons.cli.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by wuxingle on 2019/10/26
 * 默认命令解析
 */
public class DefaultAmdParser implements AmdParser {

    private static final Pattern CMD_PATTERN = Pattern.compile("(\".+?\")|('.+?')|([^\\s])+");

    private AmdRegistry registry;

    public DefaultAmdParser(AmdRegistry registry) {
        Assert.notNull(registry, "command register can not null");
        this.registry = registry;
    }

    /**
     * 命令解析
     *
     * @param cmdLine like: ls -l
     */
    @Override
    public Amd parse(String cmdLine) throws AmdParseException {
        if (!StringUtils.hasText(cmdLine)) {
            throw new AmdParseException("cmdLine can not empty!");
        }

        // 命令
        String cmd;
        // 选项
        List<String> args = new ArrayList<>();
        Matcher matcher = CMD_PATTERN.matcher(cmdLine);
        if (matcher.find()) {
            cmd = matcher.group();
        } else {
            throw new AmdParseException("cmdLine '" + cmdLine + "' is illegal!");
        }
        while (matcher.find()) {
            args.add(matcher.group());
        }

        // 获取命令定义
        AmdDefinition definition = registry.getSafeDefinition(cmd)
                .orElseThrow(() -> new AmdParseException("unknown cmd '" + cmd + "'!"));
        Options options = definition.options().get();

        CommandLine cmdline = null;
        if (options != null) {
            try {
                cmdline = createParser().parse(options, args.toArray(new String[0]));
            } catch (ParseException e) {
                throw new AmdParseException(e.getMessage());
            }
        }

        // instance
        Class<? extends Amd> type = definition.type();
        try {
            if (definition.supportCmdline()) {
                return type.getConstructor(String.class, CommandLine.class).newInstance(cmd, cmdline);
            }

            return type.getConstructor(String.class).newInstance(cmd);
        } catch (NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            throw new AmdParseException("cmd instant fail:" + type.getName(), e);
        }
    }

    protected CommandLineParser createParser() {
        return new DefaultParser();
    }

}
