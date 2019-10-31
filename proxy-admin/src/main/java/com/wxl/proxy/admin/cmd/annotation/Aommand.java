package com.wxl.proxy.admin.cmd.annotation;

import org.apache.commons.cli.Options;

import java.lang.annotation.*;
import java.util.function.Supplier;

/**
 * Create by wuxingle on 2019/10/28
 * 命令注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aommand {

    /**
     * 命令
     */
    String name();

    /**
     * 描述
     */
    String desc() default "";

    /**
     * 选项类
     */
    Class<? extends Supplier<Options>> options() default EmptyOptionsSupplier.class;
}
