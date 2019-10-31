package com.wxl.proxy.admin.cmd.annotation;

import org.apache.commons.cli.Options;

import java.util.function.Supplier;

/**
 * Create by wuxingle on 2019/10/29
 * 空选项
 */
public class EmptyOptionsSupplier implements Supplier<Options> {

    @Override
    public Options get() {
        return null;
    }
}
