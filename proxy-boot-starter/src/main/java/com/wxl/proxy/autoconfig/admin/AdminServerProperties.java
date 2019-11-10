package com.wxl.proxy.autoconfig.admin;

import com.wxl.proxy.admin.AdminTelnetServer;
import com.wxl.proxy.autoconfig.admin.handler.AdminBannerResource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wxl.proxy.autoconfig.server.ProxyProperties.PROXY_PREFIX;

/**
 * Create by wuxingle on 2019/10/27
 * 管理员相关配置
 */
@Data
@ConfigurationProperties(prefix = AdminServerProperties.ADMIN_SERVER_PREFIX)
public class AdminServerProperties {

    public static final String ADMIN_SERVER_PREFIX = PROXY_PREFIX + ".admin";

    private boolean enabled = true;

    private Integer bindPort;

    private String tips = "admin> ";

    private int maxCmdLength = 1024;

    private Resource adminBanner = new AdminBannerResource();

    private List<String> amdBasePackages = Collections.singletonList("com.wxl.proxy.admin.cmd.impl");

    private List<String> amdAddBasePackages = new ArrayList<>();

    private String serverName = AdminTelnetServer.DEFAULT_SERVER_NAME;

}

