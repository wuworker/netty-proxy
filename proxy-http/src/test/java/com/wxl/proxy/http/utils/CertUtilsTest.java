package com.wxl.proxy.http.utils;

import org.bouncycastle.asn1.x500.X500Name;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Create by wuxingle on 2019/10/15
 */
public class CertUtilsTest {

    @Test
    public void testLoadRsaPriKey() throws Exception {
        PrivateKey key = CertUtils.loadRsaPriKey(new ClassPathResource("ca_private_key.der"));
        System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
    }

    @Test
    public void testLoadCa() throws Exception{
        X509Certificate cert = CertUtils.loadCert(new ClassPathResource("test_ca.cer"));
        System.out.println(cert.getIssuerDN());
        System.out.println(CertUtils.getIssuer(cert));
    }

    @Test
    public void testGenCa() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        KeyPair keyPair = generator.genKeyPair();

        X509Certificate cert = CertUtils.genCert("C=CN,ST=ZheJiang,L=HangZhou,O=wxl,OU=test,CN=wxlRoot",
                "C=CN,ST=ZheJiang,L=HangZhou,O=wxl,OU=test,CN=xxxx",
                keyPair.getPrivate(),
                new Date(System.currentTimeMillis() - 1000
                        * 60 * 60 * 24),
                new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24
                        * 365 * 32),
                keyPair.getPublic(), "www.wxl.com");

        System.out.println(Base64.getMimeEncoder().encodeToString(cert.getEncoded()));
    }

    @Test
    public void testX500() {
        X500Name x500Name = new X500Name("C=CN,ST=ZheJiang,L=HangZhou,O=wxl,OU=test,CN=www.wxl.com");
        System.out.println(x500Name);
    }
}