package com.wxl.proxy.admin.cmd.annotation;

import org.apache.commons.cli.Options;

import java.util.function.Supplier;

/**
 * Create by wuxingle on 2019/10/29
 * 获取命令选项
 */
public class OptionsSupplier implements Supplier<Options> {

    protected Options options = new Options();

    @Override
    public Options get() {
        return options;
    }
}
