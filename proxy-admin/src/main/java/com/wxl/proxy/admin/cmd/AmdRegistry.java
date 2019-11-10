package com.wxl.proxy.admin.cmd;

import org.springframework.core.AliasRegistry;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Create by wuxingle on 2019/10/28
 * 命令注册中心
 */
public interface AmdRegistry extends AliasRegistry {

    /**
     * 注册命令
     */
    void register(AmdDefinition definition);

    /**
     * 获取命令定义
     */
    AmdDefinition getDefinition(String name);

    /**
     * 获取命令定义
     */
    Optional<AmdDefinition> getSafeDefinition(String name);

    /**
     * 获取所有命令
     */
    List<String> getAllNames();


    /**
     * 获取命令定义
     */
    Map<String, AmdDefinition> getAllDefinitions();

    /**
     * 获取注册的个数
     */
    int getAmdCount();

    /**
     * 获取所有别名
     */
    List<String> getAllAlias();

    /**
     * 获取最终的名字
     */
    String canonicalName(String name);
}
