package com.wxl.proxy.admin.cmd;

import org.springframework.core.SimpleAliasRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by wuxingle on 2019/10/29
 * 命令注册中心
 */
public class DefaultAmdRegistry extends SimpleAliasRegistry
        implements AmdRegistry {

    private Map<String, AmdDefinition> registry = new ConcurrentHashMap<>();


    /**
     * 注册命令
     */
    @Override
    public void register(String name, AmdDefinition definition) {
        AmdDefinition old = registry.putIfAbsent(name, definition);
        if (old != null) {
            throw new IllegalStateException("already has cmd named:" + name);
        }
    }

    /**
     * 获取命令定义
     */
    @Override
    public AmdDefinition getDefinition(String name) {
        return registry.get(name);
    }

    /**
     * 获取命令定义
     */
    @Override
    public Optional<AmdDefinition> getSafeDefinition(String name) {
        return Optional.ofNullable(registry.get(name));
    }

    /**
     * 获取所有命令
     */
    @Override
    public List<String> getAllNames() {
        return new ArrayList<>(registry.keySet());
    }

    /**
     * 获取命令定义
     */
    @Override
    public Map<String, AmdDefinition> getAllDefinitions() {
        return Collections.unmodifiableMap(registry);
    }

    /**
     * 获取注册的个数
     */
    @Override
    public int getAmdCount() {
        return registry.size();
    }
}
