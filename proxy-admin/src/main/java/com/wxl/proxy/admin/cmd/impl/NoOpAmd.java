package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.Amd;
import com.wxl.proxy.admin.cmd.AmdContext;
import com.wxl.proxy.admin.cmd.AmdInvokeException;
import com.wxl.proxy.admin.cmd.AmdResult;
import com.wxl.proxy.admin.cmd.result.EmptyResult;

/**
 * Create by wuxingle on 2019/10/27
 * 无操作命令
 */
public class NoOpAmd implements Amd {

    private static final AmdResult EMPTY_RESULT = new EmptyResult();

    /**
     * 命令执行
     */
    @Override
    public AmdResult invoke(AmdContext context) throws AmdInvokeException {
        return EMPTY_RESULT;
    }


    /**
     * 命令名
     */
    @Override
    public String name() {
        return "NoOp";
    }
}
