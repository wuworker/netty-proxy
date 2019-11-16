package com.wxl.proxy.autoconfig.admin;

import com.wxl.proxy.admin.AdminTelnetServer;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.*;

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

    /**
     * 默认服务名
     */
    private String serverName = AdminTelnetServer.DEFAULT_SERVER_NAME;

    /**
     * 管理服务绑定端口
     */
    private Integer bindPort;

    /**
     * 授权密码
     */
    private String password;

    /**
     * 命令提示符
     */
    private String tips = "admin> ";

    /**
     * 打印banner
     */
    private Resource adminBanner = new ClassPathResource("proxy-admin.banner");


    /**
     * 管理命令相关
     */
    private AmdProperties amd = new AmdProperties();


    @Data
    public static class AmdProperties {

        /**
         * 管理命令包路径
         */
        private List<String> amdBasePackages = Collections.singletonList("com.wxl.proxy.admin.cmd.impl");

        private List<String> amdAddBasePackages = new ArrayList<>();

        /**
         * 命令别名
         */
        private Map<String, String> alias = new HashMap<>();
    }
}

