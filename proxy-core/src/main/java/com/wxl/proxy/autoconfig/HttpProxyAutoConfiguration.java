package com.wxl.proxy.autoconfig;

import com.wxl.proxy.exception.BeanConfigException;
import com.wxl.proxy.http.HttpProxyConfig;
import com.wxl.proxy.http.HttpProxyServer;
import com.wxl.proxy.http.interceptor.HttpProxyInterceptorInitializer;
import com.wxl.proxy.http.proxy.SecondProxyConfig;
import com.wxl.proxy.http.ssl.SslConfig;
import com.wxl.proxy.properties.HttpProxyProperties;
import com.wxl.proxy.properties.HttpProxyProperties.SecondProxyProperties;
import com.wxl.proxy.properties.HttpProxyProperties.SslProperties;
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

import static com.wxl.proxy.properties.HttpProxyProperties.HTTP_PROXY_PREFIX;

/**
 * Create by wuxingle on 2019/9/1
 * http隧道代理配置
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = HTTP_PROXY_PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties({ProxyProperties.class, HttpProxyProperties.class})
public class HttpProxyAutoConfiguration implements ResourceLoaderAware {

    private final ProxyProperties proxyProperties;

    private final HttpProxyProperties httpProperties;

    private ResourceLoader resourceLoader;

    public HttpProxyAutoConfiguration(ProxyProperties proxyProperties,
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
        SslProperties ssl = httpProperties.getSsl();
        SslConfig sslConfig = null;
        if (ssl != null) {
            sslConfig = buildSslConfig(ssl);
        }

        // 二级代理
        SecondProxyProperties secondProxyProp = httpProperties.getSecondProxy();
        SecondProxyConfig secondProxyConfig = null;
        if (secondProxyProp != null) {
            secondProxyConfig = buildSecondProxyConfig(secondProxyProp);
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

        String serverName = httpProperties.getName();
        if (!StringUtils.hasText(serverName)) {
            serverName = "http-proxy";
        }

        Integer bindPort = httpProperties.getBindPort();
        if (bindPort == null || bindPort <= 0 || bindPort > 0xffff) {
            throw new BeanConfigException("proxy.http.bind-port",
                    "bind port is illegal");
        }

        HttpProxyConfig config = HttpProxyConfig.builder()
                .serverName(serverName)
                .bindPort(bindPort)
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


    /**
     * ssl配置,用于https解密
     */
    private SslConfig buildSslConfig(SslProperties ssl) {
        SslConfig.SslConfigBuilder builder = SslConfig.builder();

        // ca证书
        String certPath = ssl.getCaCertPath();
        if (!StringUtils.hasText(certPath)) {
            throw new BeanConfigException("proxy.http.ssl.ca-cert-path",
                    "ca cert path can not empty when ssl is enabled",
                    "Please to autoconfig ca cert or remove 'proxy.http.ssl' autoconfig if you won't decrypt https");
        }
        try {
            X509Certificate cert = CertUtils.loadCert(resourceLoader.getResource(certPath));

            builder.issuer(CertUtils.getIssuer(cert))
                    .subject(CertUtils.getSubject(cert))
                    .caNotBefore(cert.getNotBefore())
                    .caNotAfter(cert.getNotAfter());
        } catch (IOException e) {
            throw new BeanConfigException("proxy.http.ssl.ca-cert-path",
                    "ca cert read fail", e);
        } catch (CertificateException e) {
            throw new BeanConfigException("proxy.http.ssl.ca-cert-path",
                    "ca cert illegal! must is x509",
                    "You can use openssl to generate x509 cert. Like:\n"
                            + "\topenssl req -new -x509 -days 365 -key ca_private.pem -out ca.cer",
                    e);
        }

        // ca私钥,用于给动态生成的网站SSL证书签证
        String caPriKeyPath = ssl.getCaPrivateKeyPath();
        if (!StringUtils.hasText(caPriKeyPath)) {
            throw new BeanConfigException("proxy.http.ssl.ca-private-key-path",
                    "ca private key path can not empty when ssl is enabled",
                    "Please to autoconfig ca private key or remove 'proxy.http.ssl' autoconfig");
        }
        try {
            PrivateKey privateKey = CertUtils.loadRsaPriKey(resourceLoader.getResource(caPriKeyPath));
            builder.caPrivateKey(privateKey);
        } catch (IOException e) {
            throw new BeanConfigException("proxy.http.ssl.ca-private-key-path",
                    "ca private key read fail!",
                    "Please check ca private key is exist", e);
        } catch (InvalidKeySpecException e) {
            throw new BeanConfigException("proxy.http.ssl.ca-private-key-path",
                    "ca private key illegal! must is pkcs8 and der format",
                    "You can use openssl to generate rsa key.\n"
                            + "\topenssl genrsa -out private_key.pem 2048\n"
                            + "Then convert to pkcs8 and der.\n"
                            + "\topenssl pkcs8 -topk8 -in private_key.pem -out private_key.der -nocrypt -outform der",
                    e);
        }

        // 服务端ssl握手的公私钥对
        KeyPair keyPair = CertUtils.genRsaKeyPair();
        return builder.serverPriKey(keyPair.getPrivate())
                .serverPubKey(keyPair.getPublic())
                .build();
    }

    /**
     * 二级代理配置
     */
    private SecondProxyConfig buildSecondProxyConfig(SecondProxyProperties secondProxy) {
        if (secondProxy.getType() == null) {
            throw new BeanConfigException("proxy.http.second-proxy.type",
                    "second proxy type can not null",
                    "Please autoconfig proxy type in [http/socks4/socks5] or "
                            + "remove 'proxy.http.second-proxy' autoconfig if you won't use second proxy");
        }
        if (!StringUtils.hasText(secondProxy.getHost())) {
            throw new BeanConfigException("proxy.http.second-proxy.host",
                    "host can not empty");
        }
        if (secondProxy.getPort() == null || secondProxy.getPort() <= 0 || secondProxy.getPort() > 0xffff) {
            throw new BeanConfigException("proxy.http.second-proxy.port",
                    "port is illegal");
        }

        InetSocketAddress address = new InetSocketAddress(secondProxy.getHost(), secondProxy.getPort());

        return SecondProxyConfig.builder()
                .type(secondProxy.getType())
                .address(address)
                .username(secondProxy.getUsername())
                .password(secondProxy.getPassword())
                .build();
    }

}
