package com.wxl.proxy.admin.cmd;

import org.junit.Test;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * Create by wuxingle on 2019/10/28
 */
public class ScanTest {

    @Test
    public void test(){
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(new BeanDefinitionRegistry() {
            @Override
            public void registerBeanDefinition(String s, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {

            }

            @Override
            public void removeBeanDefinition(String s) throws NoSuchBeanDefinitionException {

            }

            @Override
            public BeanDefinition getBeanDefinition(String s) throws NoSuchBeanDefinitionException {
                return null;
            }

            @Override
            public boolean containsBeanDefinition(String s) {
                return false;
            }

            @Override
            public String[] getBeanDefinitionNames() {
                return new String[0];
            }

            @Override
            public int getBeanDefinitionCount() {
                return 0;
            }

            @Override
            public boolean isBeanNameInUse(String s) {
                return false;
            }

            @Override
            public void registerAlias(String s, String s1) {

            }

            @Override
            public void removeAlias(String s) {

            }

            @Override
            public boolean isAlias(String s) {
                return false;
            }

            @Override
            public String[] getAliases(String s) {
                return new String[0];
            }
        });
        int scan = scanner.scan("com.wxl.proxy.admin.cmd.impl");

    }
}
