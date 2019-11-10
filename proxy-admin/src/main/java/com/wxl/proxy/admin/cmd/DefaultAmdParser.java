package com.wxl.proxy.admin.cmd;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by wuxingle on 2019/10/26
 * 默认命令解析
 */
@Slf4j
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
        List<String> cmds = parseCmdArgs(cmdLine);
        String cmd = cmds.remove(0);

        // 别名处理
        if (registry.isAlias(cmd)) {
            String realCmd = registry.canonicalName(cmd);
            List<String> realCmds = parseCmdArgs(realCmd);
            cmd = realCmds.remove(0);
            realCmds.addAll(cmds);
            cmds = realCmds;
        }
        String realCmd = cmd;
        String[] args = cmds.toArray(new String[0]);

        if (log.isDebugEnabled()) {
            log.debug("parse amd is:{},{}", realCmd, Arrays.asList(args));
        }

        // 获取命令定义
        AmdDefinition definition = registry.getSafeDefinition(realCmd)
                .orElseThrow(() -> new AmdParseException("unknown cmd '" + realCmd + "'!"));

        Class<? extends Amd> type = definition.type();
        try {
            if (!definition.supportCmdline()) {
                return type.getConstructor().newInstance();
            }

            // 有选项的情况
            Options options = definition.options();
            if (options == null) {
                options = new Options();
            }
            CommandLine cmdline = createParser().parse(options, args);
            return type.getConstructor(CommandLine.class).newInstance(cmdline);

        } catch (ParseException e) {
            throw new AmdParseException(e.getMessage());
        } catch (NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            throw new AmdParseException("cmd instant fail:" + type.getName(), e);
        }
    }

    /**
     * 解析为命令，选项
     */
    private List<String> parseCmdArgs(String line) throws AmdParseException {
        List<String> cmd = new ArrayList<>();

        Matcher matcher = CMD_PATTERN.matcher(line);
        // 命令
        if (matcher.find()) {
            cmd.add(matcher.group());
        } else {
            throw new AmdParseException("cmdLine '" + line + "' is illegal!");
        }

        // 选项
        while (matcher.find()) {
            cmd.add(matcher.group());
        }
        return cmd;
    }

    protected CommandLineParser createParser() {
        return new DefaultParser();
    }

}
