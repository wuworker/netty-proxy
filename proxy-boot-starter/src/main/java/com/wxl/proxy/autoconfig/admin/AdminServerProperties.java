package com.wxl.proxy.autoconfig.admin;

import com.wxl.proxy.admin.AdminTelnetServer;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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

    private String serverName = AdminTelnetServer.DEFAULT_SERVER_NAME;

}

