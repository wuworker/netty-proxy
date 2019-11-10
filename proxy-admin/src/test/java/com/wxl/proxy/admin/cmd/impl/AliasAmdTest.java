package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.AmdDefinitionBuilder;
import com.wxl.proxy.admin.cmd.result.HelpResult;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Create by wuxingle on 2019/11/3
 */
public class AliasAmdTest {


    @Test
    public void test1(){
        HelpResult helpResult = new HelpResult(AmdDefinitionBuilder.of(AliasAmd.class));
        System.out.println(helpResult);
    }
}