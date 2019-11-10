package com.wxl.proxy.admin.cmd.annotation;

import java.lang.annotation.*;

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
     * 用法
     */
    String usage() default "";

    /**
     * 命令选项
     */
    String optionsMethod() default "";

    /**
     * 是否必须参数
     */
    boolean requireArgs() default false;
}
