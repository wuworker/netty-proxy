package com.wxl.proxy.tcp;

import com.wxl.proxy.server.LoopResources;
import com.wxl.proxy.server.NioLoopResources;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * Create by wuxingle on 2019/10/15
 */
public class TcpProxyServerTest {


    @Test
    public void test1() throws Exception {
        TcpProxyConfig config = TcpProxyConfig.builder()
                .serverName("redis-proxy")
                .connectTimeout(Duration.ofSeconds(5))
                .bindPort(8888)
                .remoteAddress(new InetSocketAddress("localhost", 6379))
                .build();

        LoopResources loopResources = new NioLoopResources();
        TcpLoopResource tcpLoopResource = TcpLoopResource.create(loopResources);

        TcpProxyServer server = new TcpProxyServer(config, tcpLoopResource);
        server.start();

        new CountDownLatch(1).await();

        tcpLoopResource.release();
        loopResources.release();
    }

}