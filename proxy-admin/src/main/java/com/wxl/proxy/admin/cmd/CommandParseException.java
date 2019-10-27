package com.wxl.proxy.admin.cmd;

/**
 * Create by wuxingle on 2019/10/26
 * 命令解析异常
 */
@SuppressWarnings("serial")
public class CommandParseException extends RuntimeException {

    public CommandParseException() {
        super();
    }

    public CommandParseException(String message) {
        super(message);
    }

    public CommandParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandParseException(Throwable cause) {
        super(cause);
    }

    protected CommandParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
