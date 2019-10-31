package com.wxl.proxy.autoconfig.admin;

import com.wxl.proxy.admin.AdminTelnetServer;
import com.wxl.proxy.admin.cmd.*;
import com.wxl.proxy.admin.cmd.annotation.AmdClassPathScanner;
import com.wxl.proxy.admin.handler.AdminChannelInitializer;
import com.wxl.proxy.autoconfig.exception.BeanConfigException;
import com.wxl.proxy.autoconfig.server.EventLoopGroupManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.wxl.proxy.autoconfig.admin.AdminServerProperties.ADMIN_SERVER_PREFIX;

/**
 * Create by wuxingle on 2019/10/27
 * 管理员服务配置
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = ADMIN_SERVER_PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties({AdminServerProperties.class})
public class AdminServerAutoConfiguration {

    private AdminServerProperties properties;

    @Autowired
    public AdminServerAutoConfiguration(AdminServerProperties properties) {
        this.properties = properties;
    }

    /**
     * admin telnet服务
     */
    @Bean
    @ConditionalOnMissingBean
    public AdminChannelInitializer adminChannelInitializer(AmdParser parser) {
        int maxCmdLength = properties.getMaxCmdLength();
        String tips = properties.getTips();
        return new AdminChannelInitializer(maxCmdLength, tips, parser);
    }

    @Bean
    public AdminTelnetServer adminTelnetServer(AdminChannelInitializer channelInitializer,
                                               EventLoopGroupManager groupManager) {
        Integer bindPort = properties.getBindPort();
        if (bindPort == null || bindPort <= 0 || bindPort > 0xffff) {
            throw new BeanConfigException("proxy.admin.bind-port",
                    "bind port is illegal");
        }

        return new AdminTelnetServer(bindPort, channelInitializer,
                groupManager.getBossGroup(), groupManager.getWorkGroup());
    }

    /**
     * 命令格式化
     */
    @Bean
    @ConditionalOnMissingBean
    public AmdFormatter amdFormatter() {
        return new DefaultAmdFormatter();
    }

    /**
     * 命令注册中心
     */
    @Bean
    @ConditionalOnMissingBean
    public AmdRegistry amdRegistry() {
        return new DefaultAmdRegistry();
    }

    /**
     * 命令解析
     */
    @Bean
    @ConditionalOnMissingBean
    public AmdParser amdParser(AmdRegistry register) {
        return new DefaultAmdParser(register);
    }

    /**
     * 扫描命令类
     */
    @Bean(initMethod = "scan")
    @ConditionalOnMissingBean
    public AmdClassPathScanner amdClassPathScanner(AmdRegistry registry) {
        List<String> amdBasePackages = properties.getAmdBasePackages();
        List<String> amdAddBasePackages = properties.getAmdAddBasePackages();
        Set<String> packages = new LinkedHashSet<>();
        if (!CollectionUtils.isEmpty(amdBasePackages)) {
            packages.addAll(amdBasePackages);
        }
        if (!CollectionUtils.isEmpty(amdAddBasePackages)) {
            packages.addAll(amdAddBasePackages);
        }
        return new AmdClassPathScanner(registry, new ArrayList<>(packages));
    }
}
