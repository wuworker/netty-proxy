package com.wxl.proxy.tcp;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
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
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        TcpProxyServer server = new TcpProxyServer(config, boss, work);
        server.start();

        new CountDownLatch(1).await();

        work.shutdownGracefully();
        boss.shutdownGracefully();
    }

}