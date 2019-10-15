package com.wxl.proxy.http.ssl;

import com.wxl.proxy.http.utils.CertUtils;
import lombok.extern.slf4j.Slf4j;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxingle on 2019/9/24.
 * 动态生成的https证书池
 */
@Slf4j
public class CertPool {

    private static Map<String, X509Certificate> certCache = new ConcurrentHashMap<>();

    public static X509Certificate getCert(String host, SslConfig config) throws CertificateException {
        X509Certificate cert = certCache.get(host);
        if (cert != null) {
            return cert;
        }
        cert = CertUtils.genCert(config.getIssuer(), config.getSubject(), config.getCaPrivateKey(),
                config.getCaNotBefore(), config.getCaNotAfter(), config.getServerPubKey(), host);
        X509Certificate old = certCache.putIfAbsent(host, cert);
        if (old != null) {
            cert = old;
        }
        return cert;
    }

}
