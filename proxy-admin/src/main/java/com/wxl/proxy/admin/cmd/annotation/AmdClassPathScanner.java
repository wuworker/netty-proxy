package com.wxl.proxy.admin.cmd.annotation;

import com.wxl.proxy.admin.cmd.Amd;
import com.wxl.proxy.admin.cmd.AmdDefinition;
import com.wxl.proxy.admin.cmd.AmdDefinitionBuilder;
import com.wxl.proxy.admin.cmd.AmdRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Create by wuxingle on 2019/10/28
 * 扫描命令类，并注册
 */
@Slf4j
public class AmdClassPathScanner {

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private AmdRegistry registry;

    private ResourcePatternResolver resourcePatternResolver;

    private MetadataReaderFactory metadataReaderFactory;

    private List<String> basePackages;

    public AmdClassPathScanner(AmdRegistry registry) {
        this(registry, Collections.emptyList(), null, null);
    }

    public AmdClassPathScanner(AmdRegistry registry, List<String> basePackages) {
        this(registry, basePackages, null, null);

    }

    public AmdClassPathScanner(AmdRegistry registry, List<String> basePackages,
                               ResourcePatternResolver resourcePatternResolver,
                               MetadataReaderFactory metadataReaderFactory) {
        Assert.notNull(registry, "registry can not null");
        Assert.notNull(basePackages, "scan base package can not null");
        this.registry = registry;
        this.basePackages = Collections.unmodifiableList(basePackages);
        this.resourcePatternResolver = resourcePatternResolver;
        this.metadataReaderFactory = metadataReaderFactory;
    }

    /**
     * 扫描命令类并注册
     */
    public void scan() {
        int size = scan(basePackages.toArray(new String[0]));
        log.debug("register amd size:{}", size);
    }

    public int scan(String... basePackages) {
        int count = registry.getAmdCount();
        for (String basePackage : basePackages) {
            doScan(basePackage);
        }
        return registry.getAmdCount() - count;
    }


    private void doScan(String basePackage) {
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                    AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                    if (hasAommandAnnotation(annotationMetadata)) {

                        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                                annotationMetadata.getAnnotationAttributes(Aommand.class.getName()));

                        String name = attributes.getString("name");
                        AmdDefinition amdDefinition = AmdDefinitionBuilder.of(annotationMetadata);

                        registry.register(name, amdDefinition);
                    }
                } else {
                    log.warn("ignore unreadable resource:{}", resource);
                }
            }
        } catch (IOException e) {
            throw new BeanDefinitionStoreException("I/O failure during Amd classpath scanning", e);
        }
    }


    private boolean hasAommandAnnotation(AnnotationMetadata metadata) {
        return metadata.hasAnnotation(Aommand.class.getName());
    }

    protected ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }

    protected MetadataReaderFactory getMetadataReaderFactory() {
        if (this.metadataReaderFactory == null) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory();
        }
        return this.metadataReaderFactory;
    }
}
