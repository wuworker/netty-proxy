package com.wxl.proxy.admin.cmd;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Create by wuxingle on 2019/10/26
 * 命令注册中心
 */
public interface AdminCommandFactoryRegister {


    <S extends AdminCommand> void register(String cmd, Supplier<S> factory);


    <S extends AdminCommand> Supplier<S> getFactory(String cmd);


    default <S extends AdminCommand> Optional<Supplier<S>> getSafeFactory(String cmd) {
        return Optional.ofNullable(getFactory(cmd));
    }
}

