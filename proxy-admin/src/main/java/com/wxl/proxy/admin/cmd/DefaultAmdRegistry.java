package com.wxl.proxy.admin.cmd;

import org.springframework.core.SimpleAliasRegistry;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Create by wuxingle on 2019/10/29
 * 命令注册中心
 */
public class DefaultAmdRegistry extends SimpleAliasRegistry
        implements AmdRegistry {

    private static final Pattern AMD_NAME_PATTERN = Pattern.compile("[a-zA-z_]+\\w*");

    private Map<String, AmdDefinition> amdRegistry = new ConcurrentHashMap<>();

    private final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);

    /**
     * 注册命令
     */
    @Override
    public void register(AmdDefinition definition) {
        checkDefinition(definition);
        String name = definition.name();
        AmdDefinition old = amdRegistry.putIfAbsent(name, definition);
        if (old != null) {
            throw new IllegalStateException("already has cmd named:" + name);
        }
    }

    /**
     * 获取命令定义
     */
    @Override
    public AmdDefinition getDefinition(String name) {
        return amdRegistry.get(name);
    }

    /**
     * 获取命令定义
     */
    @Override
    public Optional<AmdDefinition> getSafeDefinition(String name) {
        return Optional.ofNullable(amdRegistry.get(name));
    }

    /**
     * 获取所有命令
     */
    @Override
    public List<String> getAllNames() {
        return new ArrayList<>(amdRegistry.keySet());
    }

    /**
     * 获取命令定义
     */
    @Override
    public Map<String, AmdDefinition> getAllDefinitions() {
        return Collections.unmodifiableMap(amdRegistry);
    }

    /**
     * 获取注册的个数
     */
    @Override
    public int getAmdCount() {
        return amdRegistry.size();
    }

    /**
     * 获取所有别名
     */
    @Override
    public List<String> getAllAlias() {
        return new ArrayList<>(aliasMap.keySet());
    }

    @Override
    public void registerAlias(String name, String alias) {
        checkName(alias);
        super.registerAlias(name, alias);
        aliasMap.put(alias, name);
    }

    @Override
    public void removeAlias(String alias) {
        super.removeAlias(alias);
        aliasMap.remove(alias);
    }

    @Override
    protected boolean allowAliasOverriding() {
        return true;
    }

    protected void checkName(String name) {
        Assert.hasText(name, "amd name can not empty!");
        boolean matches = AMD_NAME_PATTERN.matcher(name).matches();
        if (!matches) {
            throw new IllegalArgumentException("amd name must match [0-9a-zA-Z_] and not start with number!");
        }
    }

    protected void checkDefinition(AmdDefinition definition) {
        checkName(definition.name());
        Assert.notNull(definition.type(), "amd definition type can not null");
    }
}
