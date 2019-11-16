package com.wxl.proxy.admin.cmd.result;

import com.wxl.proxy.admin.cmd.AmdDefinitionBuilder;
import com.wxl.proxy.admin.cmd.impl.AliasAmd;
import com.wxl.proxy.admin.cmd.impl.CloseAmd;
import com.wxl.proxy.admin.cmd.impl.CreateAmd;
import com.wxl.proxy.admin.cmd.impl.LsAmd;
import org.junit.Test;

/**
 * Create by wuxingle on 2019/10/31
 */
public class HelpResultTest {


    @Test
    public void test() {
        HelpResult helpResult = new HelpResult(AmdDefinitionBuilder.of(CreateAmd.class));
        System.out.println(helpResult);
        System.out.println(helpResult.toString().endsWith("\n"));
    }
}