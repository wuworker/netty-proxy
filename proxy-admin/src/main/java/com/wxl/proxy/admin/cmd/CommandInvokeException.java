package com.wxl.proxy.admin.cmd;

/**
 * Create by wuxingle on 2019/10/26
 * 命令执行异常
 */
@SuppressWarnings("serial")
public class CommandInvokeException extends RuntimeException {

    public CommandInvokeException() {
        super();
    }

    public CommandInvokeException(String message) {
        super(message);
    }

    public CommandInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandInvokeException(Throwable cause) {
        super(cause);
    }

    protected CommandInvokeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
