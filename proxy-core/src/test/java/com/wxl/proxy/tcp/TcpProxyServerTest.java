package com.wxl.proxy.tcp;

import com.wxl.proxy.autoconfig.ProxyServerAutoConfiguration;
import com.wxl.proxy.autoconfig.TcpProxyAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 * Create by wuxingle on 2019/8/17
 * TcpProxyServer
 */
@ActiveProfiles("tcp")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProxyServerAutoConfiguration.class, TcpProxyAutoConfiguration.class})
public class TcpProxyServerTest {


    @Test
    public void testTcpProxy() throws Exception {
        new CountDownLatch(1).await();
    }

}


