package com.wxl.proxy.autoconfig.admin;

import com.wxl.proxy.admin.statistics.ProxyServerRegistry;
import com.wxl.proxy.server.ProxyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create by wuxingle on 2019/11/6
 * 统计相关配置
 */
@Slf4j
@Configuration
public class AdminStatisticsAutoConfiguration {


    @Bean
    public ProxyServerRegistry proxyServerRegistry(ObjectProvider<ProxyServer<?>> proxyServers) {
        return new ProxyServerRegistry(proxyServers);
    }
}
