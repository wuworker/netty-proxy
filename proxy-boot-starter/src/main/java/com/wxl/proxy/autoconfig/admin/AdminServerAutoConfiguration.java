package com.wxl.proxy.autoconfig.admin;

import com.wxl.proxy.admin.AdminTelnetServer;
import com.wxl.proxy.admin.cmd.*;
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
    public AdminChannelInitializer adminChannelInitializer(AdminCommandParser parser) {
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
    public AdminCommandFormatter adminCommandFormatter() {
        return new DefaultAdminCommandFormatter();
    }

    /**
     * 命令注册中心
     */
    @Bean
    @ConditionalOnMissingBean
    public AdminCommandFactoryRegister adminCommandFactoryRegister() {
        return new DefaultCommandFactoryRegister();
    }

    /**
     * 命令解析
     */
    @Bean
    @ConditionalOnMissingBean
    public AdminCommandParser adminCommandParser(AdminCommandFactoryRegister register) {
        return new DefaultAdminCommandParser(register);
    }


}
