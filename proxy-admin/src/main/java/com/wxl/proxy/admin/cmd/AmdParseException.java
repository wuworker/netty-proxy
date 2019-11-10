package com.wxl.proxy.admin.cmd;

/**
 * Create by wuxingle on 2019/10/26
 * 命令解析异常
 */
@SuppressWarnings("serial")
public class AmdParseException extends Exception {


    public AmdParseException(String message) {
        this(message, null, false);
    }

    public AmdParseException(String message, Throwable cause) {
        this(message, cause, false);
    }

    public AmdParseException(String message, Throwable cause, boolean writableStackTrace) {
        super(message, cause, false, writableStackTrace);
    }
}
