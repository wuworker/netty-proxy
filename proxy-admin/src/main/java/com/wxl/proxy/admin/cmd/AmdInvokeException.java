package com.wxl.proxy.admin.cmd;

/**
 * Create by wuxingle on 2019/10/26
 * 命令执行异常
 */
@SuppressWarnings("serial")
public class AmdInvokeException extends RuntimeException {

    public AmdInvokeException(String message) {
        this(message, null, false);
    }

    public AmdInvokeException(String message, Throwable cause) {
        this(message, cause, false);
    }

    public AmdInvokeException(String message, Throwable cause, boolean writableStackTrace) {
        super(message, cause, false, writableStackTrace);
    }
}
