package com.wxl.proxy.admin.cmd;

import com.wxl.proxy.admin.cmd.impl.LsCommand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Create by wuxingle on 2019/10/26
 * 命令注册中心
 */
public class DefaultCommandFactoryRegister implements AdminCommandFactoryRegister {

    private Map<String, Supplier<? extends AdminCommand>> registry = new ConcurrentHashMap<>();

    public DefaultCommandFactoryRegister() {
        register(LsCommand.COMMAND_NAME, LsCommand::new);
    }

    @Override
    public <S extends AdminCommand> void register(String cmd, Supplier<S> factory) {
        Supplier<?> old = registry.putIfAbsent(cmd, factory);
        if (old != null) {
            throw new IllegalStateException("the '" + cmd + "' already in registry");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends AdminCommand> Supplier<S> getFactory(String cmd) {
        return (Supplier<S>) registry.get(cmd);
    }

}
