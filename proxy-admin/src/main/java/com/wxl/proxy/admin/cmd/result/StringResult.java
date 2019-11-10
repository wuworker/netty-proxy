package com.wxl.proxy.admin.cmd.result;

import com.wxl.proxy.admin.cmd.AmdResult;

/**
 * Create by wuxingle on 2019/11/3
 * 字符串结果
 */
public class StringResult implements AmdResult {

    private String result;

    public StringResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return result;
    }
}
