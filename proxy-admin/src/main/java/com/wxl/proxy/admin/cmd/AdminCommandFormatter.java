package com.wxl.proxy.admin.cmd;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Create by wuxingle on 2019/10/26
 * 命令格式化
 */
public interface AdminCommandFormatter {

    /**
     * 格式化命令
     */
    String format(AdminCommand cmd, Charset charset);

    default String format(AdminCommand cmd) {
        return format(cmd, StandardCharsets.UTF_8);
    }
}

