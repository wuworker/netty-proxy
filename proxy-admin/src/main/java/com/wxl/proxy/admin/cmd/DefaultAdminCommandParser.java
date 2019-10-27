package com.wxl.proxy.admin.cmd;

import org.apache.commons.cli.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by wuxingle on 2019/10/26
 * 默认命令解析
 */
public class DefaultAdminCommandParser implements AdminCommandParser {

    private static final Pattern CMD_PATTERN = Pattern.compile("(\".+?\")|('.+?')|([^\\s])+");

    private AdminCommandFactoryRegister register;

    public DefaultAdminCommandParser(AdminCommandFactoryRegister register) {
        Assert.notNull(register, "command factory register can not null");
        this.register = register;
    }

    /**
     * 命令解析
     *
     * @param cmdLine like: ls -l
     */
    @Override
    public AdminCommand parse(String cmdLine) throws CommandParseException {
        if (!StringUtils.hasText(cmdLine)) {
            throw new CommandParseException("cmdLine can not empty!");
        }

        // 命令
        String cmd;
        // 选项
        List<String> args = new ArrayList<>();
        Matcher matcher = CMD_PATTERN.matcher(cmdLine);
        if (matcher.find()) {
            cmd = matcher.group();
        } else {
            throw new CommandParseException("cmdLine '" + cmdLine + "' is illegal!");
        }
        while (matcher.find()) {
            args.add(matcher.group());
        }

        // 获取命令工厂
        Supplier<AdminCommand> factory = register.getFactory(cmd);
        if (factory == null) {
            throw new CommandParseException("unknown cmd '" + cmd + "'!");
        }
        AdminCommand adminCommand = factory.get();
        if (adminCommand == null) {
            throw new CommandParseException("server error! cmd '" + cmd + "' factory return null!");
        }

        // 带有选项的命令
        if (adminCommand instanceof AdminOptionsCommand) {
            AdminOptionsCommand optionsCommand = (AdminOptionsCommand) adminCommand;
            Options options = optionsCommand.options();

            try {
                CommandLine commandLine = createParser().parse(options, args.toArray(new String[0]));
                optionsCommand.setCommandLine(commandLine);
            } catch (ParseException e) {
                throw new CommandParseException(e.getMessage());
            }
        }

        return adminCommand;
    }


    protected CommandLineParser createParser() {
        return new DefaultParser();
    }

}
