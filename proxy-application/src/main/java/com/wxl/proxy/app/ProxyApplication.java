package com.wxl.proxy.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Create by wuxingle on 2019/8/17
 */
@SpringBootApplication
public class ProxyApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ProxyApplication.class);
        application.run(args);
    }

}
