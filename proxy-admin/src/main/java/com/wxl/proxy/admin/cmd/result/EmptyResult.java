package com.wxl.proxy.admin.cmd.result;

import com.wxl.proxy.admin.cmd.AdminCommandResult;

/**
 * Create by wuxingle on 2019/10/27
 * 空结果
 */
public class EmptyResult implements AdminCommandResult {

    /**
     * 结果格式化
     */
    @Override
    public String format() {
        return "";
    }
}
