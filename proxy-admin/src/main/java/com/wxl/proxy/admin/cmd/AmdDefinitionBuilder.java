package com.wxl.proxy.admin.cmd;

import com.wxl.proxy.admin.cmd.annotation.Aommand;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Create by wuxingle on 2019/10/31
 * 命令定义构造者
 */
public class AmdDefinitionBuilder {

    private static Map<String, AmdDefinition> definitionCache = new ConcurrentHashMap<>();

    /**
     * 自定义创建
     */
    public static AmdDefinitionBuilder custom() {
        return new AmdDefinitionBuilder();
    }

    /**
     * 根据AnnotationMetadata创建
     */
    @SuppressWarnings("unchecked")
    public static AmdDefinition of(AnnotationMetadata metadata) {
        Assert.notNull(metadata, "metadata can not null");
        String className = metadata.getClassName();
        AmdDefinition definition = definitionCache.get(className);
        if (definition == null) {
            Class<?> type;
            try {
                type = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("can not found amd class", e);
            }
            if (!Amd.class.isAssignableFrom(type)) {
                throw new IllegalStateException("annotate @Aommand class must implement Amd.class:" + type);
            }

            definition = ofNoCache((Class<? extends Amd>) type);
            definitionCache.put(className, definition);
        }
        return definition;
    }

    /**
     * 根据class创建
     */
    public static AmdDefinition of(Class<? extends Amd> type) {
        Assert.notNull(type, "type can not null");
        String name = type.getName();
        AmdDefinition definition = definitionCache.get(name);
        if (definition == null) {
            definition = ofNoCache(type);
            definitionCache.put(name, definition);
        }
        return definition;
    }


    public static void clearCache() {
        definitionCache.clear();
    }

    private static AmdDefinition ofNoCache(Class<? extends Amd> type) {
        AnnotationMetadata metadata = new StandardAnnotationMetadata(type);
        if (!metadata.isConcrete() || !metadata.isIndependent()) {
            throw new IllegalStateException("amd can not create by type:" + type);
        }
        if (!metadata.hasAnnotation(Aommand.class.getName())) {
            throw new IllegalStateException("amd must annotate @Aommand:" + type);
        }

        boolean supportCmdLine = ClassUtils.hasConstructor(type, String.class, CommandLine.class);
        if (!supportCmdLine && !ClassUtils.hasConstructor(type, String.class)) {
            throw new IllegalStateException("amd constructor must is (String.class) " +
                    "or (String.class,CommandLine.class)");
        }

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(Aommand.class.getName()));

        String desc = attributes.getString("desc");
        Class<? extends Supplier<Options>> options = attributes.getClass("options");
        Supplier<Options> supplier;
        try {
            supplier = options.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("create amd options fail! " + options, e);
        }

        return custom().type(type)
                .description(desc)
                .options(supplier)
                .supportCmdline(supportCmdLine)
                .build();
    }

    private String description;

    private Class<? extends Amd> type;

    private Supplier<Options> options;

    private boolean supportCmdline;

    public AmdDefinitionBuilder() {
    }

    public AmdDefinitionBuilder description(String description) {
        this.description = description;
        return this;
    }

    public AmdDefinitionBuilder type(Class<? extends Amd> type) {
        this.type = type;
        return this;
    }

    public AmdDefinitionBuilder options(Supplier<Options> options) {
        this.options = options;
        return this;
    }

    public AmdDefinitionBuilder supportCmdline(boolean supportCmdline) {
        this.supportCmdline = supportCmdline;
        return this;
    }

    public AmdDefinition build() {
        return new DefaultAmdDefinition(description, type, options, supportCmdline);
    }
}
