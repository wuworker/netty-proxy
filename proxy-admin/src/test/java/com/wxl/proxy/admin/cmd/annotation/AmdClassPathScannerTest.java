package com.wxl.proxy.admin.cmd.annotation;

import com.wxl.proxy.admin.cmd.Amd;
import com.wxl.proxy.admin.cmd.DefaultAmdRegistry;
import com.wxl.proxy.admin.cmd.impl.LsAmd;
import org.junit.Test;

/**
 * Create by wuxingle on 2019/10/30
 */
public class AmdClassPathScannerTest {

    @Test
    public void scan() {

        AmdClassPathScanner scanner = new AmdClassPathScanner(new DefaultAmdRegistry());
        int scan = scanner.scan("com.wxl.proxy.admin.cmd.impl");
        System.out.println("register size:" + scan);
    }

    @Test
    public void test2() {
        System.out.println(Amd.class.isAssignableFrom(LsAmd.class));
    }
}

