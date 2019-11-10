package com.wxl.proxy.admin.cmd;

import org.junit.Test;
import org.springframework.core.AliasRegistry;
import org.springframework.core.SimpleAliasRegistry;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Create by wuxingle on 2019/11/3
 */
public class DefaultAmdRegistryTest {


    @Test
    public void test(){
       SimpleAliasRegistry registry = new DefaultAmdRegistry();

        registry.registerAlias("ls","list");
        registry.registerAlias("ls","list2");

        System.out.println(registry.isAlias("list"));
        registry.registerAlias("list","ls2");
        System.out.println(Arrays.toString(registry.getAliases("ls")));
        System.out.println(registry.canonicalName("ls2"));

    }
}