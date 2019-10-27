package com.wxl.proxy.http;

import com.wxl.proxy.http.interceptor.HttpProxyInterceptor;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorPipeline;
import com.wxl.proxy.http.ssl.SslConfig;
import com.wxl.proxy.http.utils.CertUtils;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * Create by wuxingle on 2019/10/15
 */
public class HttpProxyServerTest {

    /**
     * http/https代理
     */
    @Test
    public void testSimple() throws Exception {
        HttpProxyConfig config = HttpProxyConfig.builder()
                .serverName("http")
                .bindPort(8888)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        HttpProxyServer server = new HttpProxyServer(config, boss, work);

        server.start();

        new CountDownLatch(1).await();

        boss.shutdownGracefully();
        work.shutdownGracefully();
    }

    /**
     * http/https代理
     * https解密
     * 浏览器需要导入root_ca.cer证书为可信
     */
    @Test
    public void testHttpsDecode() throws Exception {

        X509Certificate cert = CertUtils.loadCert(new ClassPathResource("root_ca.cer"));
        PrivateKey privateKey = CertUtils.loadRsaPriKey(new ClassPathResource("ca_private_key.der"));
        KeyPair keyPair = CertUtils.genRsaKeyPair();

        SslConfig sslConfig = SslConfig.builder()
                .issuer(CertUtils.getIssuer(cert))
                .subject(CertUtils.getSubject(cert))
                .caNotBefore(cert.getNotBefore())
                .caNotAfter(cert.getNotAfter())
                .caPrivateKey(privateKey)
                .serverPriKey(keyPair.getPrivate())
                .serverPubKey(keyPair.getPublic())
                .clientSslContext(SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build())
                .build();

        HttpProxyConfig config = HttpProxyConfig.builder()
                .serverName("http")
                .bindPort(8888)
                .connectTimeout(Duration.ofSeconds(5))
                .ssl(sslConfig)
                .build();

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        HttpProxyServer server = new HttpProxyServer(config, boss, work);

        // http报文解析
        server.setHttpInterceptorInitializer(pipeline ->
                pipeline.addLast(new HttpProxyInterceptor<HttpObject, HttpObject>() {
                    @Override
                    public void beforeRequest(Channel inboundChannel,
                                              Channel outboundChannel,
                                              HttpObject request,
                                              HttpProxyInterceptorPipeline pipeline) throws Exception {
                        System.out.println(request);
                        pipeline.beforeRequest(inboundChannel, outboundChannel, request);
                    }

                    @Override
                    public void afterResponse(Channel inboundChannel,
                                              Channel outboundChannel,
                                              HttpObject response,
                                              HttpProxyInterceptorPipeline pipeline) throws Exception {
                        System.out.println(response);
                        pipeline.afterResponse(inboundChannel, outboundChannel, response);
                    }
                })
        );

        server.start();

        new CountDownLatch(1).await();

        boss.shutdownGracefully();
        work.shutdownGracefully();
    }
}