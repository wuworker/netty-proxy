package com.wxl.proxy.http;

import com.wxl.proxy.ProxyApplication;
import com.wxl.proxy.common.ServerChannelInitializer;
import com.wxl.proxy.config.HttpProxyConfiguration;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorInitializer;
import com.wxl.proxy.tcp.TcpProxyConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 * Created by wuxingle on 2019/9/16.
 */
@ActiveProfiles("httpChannelInit")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProxyApplication.class, HttpChannelInitTest.Config.class})
public class HttpChannelInitTest {

    @Configuration
    public static class Config {
        @Bean("serverInit1")
        public ServerChannelInitializer<HttpProxyConfig> serverChannelInitializer1() {
            return (channel, config) -> {
                System.out.println("server config1 is:" + config);
            };
        }

        @Bean("serverInit2")
        public ServerChannelInitializer<HttpProxyConfig> serverChannelInitializer2() {
            return (channel, config) -> {
                System.out.println("server config2 is:" + config);
            };
        }

        @Bean("serverInit3")
        public ServerChannelInitializer<TcpProxyConfig> serverChannelInitializer3() {
            return (channel, config) -> {
                System.out.println("server config3 is:" + config);
            };
        }

        @Bean("interceptorInitializer1")
        public HttpProxyInterceptorInitializer interceptorInitializer() {
            return pipeline -> {
                System.out.println("11111111111111111111111111111111111111111");
            };
        }

        @Bean("interceptorInitializer2")
        public HttpProxyInterceptorInitializer interceptorInitializer2() {
            return pipeline -> {
                System.out.println("22222222222222222222222222222222222222222222222");
            };
        }
    }


    @Test
    public void test() throws Exception {
        new CountDownLatch(1).await();
    }

}
