package com.wxl.proxy.config;

import com.wxl.proxy.http.HttpProxyConfig;
import com.wxl.proxy.http.HttpProxyServer;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorInitializer;
import com.wxl.proxy.http.proxy.SecondProxyConfig;
import com.wxl.proxy.http.ssl.SslConfig;
import com.wxl.proxy.properties.HttpProxyProperties;
import com.wxl.proxy.properties.ProxyProperties;
import com.wxl.proxy.server.EventLoopGroupManager;
import com.wxl.proxy.utils.CertUtils;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2019/9/1
 * http隧道代理配置
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = HttpProxyProperties.HTTP_PROXY_PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties({ProxyProperties.class, HttpProxyProperties.class})
public class HttpProxyConfiguration implements ResourceLoaderAware {

    private final ProxyProperties proxyProperties;

    private final HttpProxyProperties httpProperties;

    private ResourceLoader resourceLoader;

    public HttpProxyConfiguration(ProxyProperties proxyProperties,
                                  HttpProxyProperties httpProperties) {
        this.proxyProperties = proxyProperties;
        this.httpProperties = httpProperties;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public HttpProxyServer httpProxyServer(EventLoopGroupManager groupManager,
                                           ObjectProvider<HttpProxyInterceptorInitializer> interceptorInitializers) {
        Duration connectTimeout = httpProperties.getConnectTimeout();
        if (connectTimeout == null) {
            connectTimeout = proxyProperties.getConnectTimeout();
        }

        // ssl配置,用于https解密
        HttpProxyProperties.SslProperties ssl = httpProperties.getSsl();
        SslConfig sslConfig = null;
        if (ssl != null && ssl.isEnabled()) {
            SslConfig.SslConfigBuilder builder = SslConfig.builder();

            // ca证书
            String certPath = ssl.getCaCertPath();
            if (StringUtils.isEmpty(certPath)) {
                throw new IllegalStateException("ca cert can not empty");
            }
            try {
                X509Certificate cert = CertUtils.loadCert(resourceLoader.getResource(certPath));

                builder.issuer(CertUtils.getIssuer(cert))
                        .subject(CertUtils.getSubject(cert))
                        .caNotBefore(cert.getNotBefore())
                        .caNotAfter(cert.getNotAfter());
            } catch (IOException | CertificateException e) {
                throw new IllegalStateException("cert is illegal", e);
            }

            // ca私钥,用于给动态生成的网站SSL证书签证
            String caPriKeyPath = ssl.getCaPrivateKeyPath();
            if (StringUtils.isEmpty(caPriKeyPath)) {
                throw new IllegalStateException("ca pri key can not empty");
            }
            try {
                PrivateKey privateKey = CertUtils.loadRsaPriKey(resourceLoader.getResource(caPriKeyPath));
                builder.caPrivateKey(privateKey);
            } catch (IOException | InvalidKeySpecException e) {
                throw new IllegalStateException("ca private key is illegal", e);
            }

            // 服务端ssl握手的公私钥对
            KeyPair keyPair = CertUtils.genRsaKeyPair();
            sslConfig = builder.serverPriKey(keyPair.getPrivate())
                    .serverPubKey(keyPair.getPublic())
                    .build();
        }

        // 二级代理
        HttpProxyProperties.SecondProxyProperties secondProxyProp = httpProperties.getSecondProxy();
        SecondProxyConfig secondProxyConfig = null;
        if (secondProxyProp != null && secondProxyProp.getType() != null) {
            InetSocketAddress address = new InetSocketAddress(secondProxyProp.getHost(), secondProxyProp.getPort());

            secondProxyConfig = SecondProxyConfig.builder()
                    .type(secondProxyProp.getType())
                    .address(address)
                    .username(secondProxyProp.getUsername())
                    .password(secondProxyProp.getPassword())
                    .build();
        }

        // 代理与真实服务器连接的ssl
        SslContext clientSslContext;
        try {
            clientSslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } catch (SSLException e) {
            throw new IllegalStateException(e);
        }

        HttpProxyConfig config = HttpProxyConfig.builder()
                .serverName(httpProperties.getName())
                .bindPort(httpProperties.getBindPort())
                .clientSslContext(clientSslContext)
                .ssl(sslConfig)
                .secondProxy(secondProxyConfig)
                .connectTimeout(connectTimeout)
                .build();

        log.debug("create http proxy server:{}", config);

        HttpProxyServer server = new HttpProxyServer(config, groupManager.getBossGroup(), groupManager.getWorkGroup());

        // http拦截器
        List<HttpProxyInterceptorInitializer> initializers = interceptorInitializers.orderedStream().collect(Collectors.toList());
        if (!initializers.isEmpty()) {
            server.setInterceptorInitializer(pipeline -> {
                for (HttpProxyInterceptorInitializer initializer : initializers) {
                    initializer.init(pipeline);
                }
            });
        }
        return server;
    }

}
