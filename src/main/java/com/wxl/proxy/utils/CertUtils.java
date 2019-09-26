package com.wxl.proxy.utils;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by wuxingle on 2019/9/24.
 * 证书，密钥相关
 */
public class CertUtils {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成rsa密钥对
     */
    public static KeyPair genRsaKeyPair() {
        return genRsaKeyPair(2048);
    }

    public static KeyPair genRsaKeyPair(int keySize) {
        KeyPairGenerator generator;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        generator.initialize(keySize, new SecureRandom());
        return generator.genKeyPair();
    }

    /**
     * 加载rsa私钥
     * pkcs8，der格式
     */
    public static PrivateKey loadRsaPriKey(Resource resource) throws IOException, InvalidKeySpecException {
        byte[] data = new byte[1300];
        try (InputStream in = resource.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int len;
            while ((len = in.read(data)) > 0) {
                out.write(data, 0, len);
            }

            return loadRsaPriKey(out.toByteArray());
        }
    }

    public static PrivateKey loadRsaPriKey(byte[] der) throws InvalidKeySpecException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 加载ca证书
     */
    public static X509Certificate loadCert(Resource resource) throws IOException, CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        return (X509Certificate) cf.generateCertificate(resource.getInputStream());
    }

    public static X509Certificate loadCert(InputStream in) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        return (X509Certificate) cf.generateCertificate(in);
    }

    /**
     * 获取cert里的issuer
     */
    public static String getIssuer(X509Certificate cert) {
        //读出来顺序是反的需要反转下
        String issuer = cert.getIssuerDN().toString();
        String[] items = issuer.split(", ");
        return IntStream.rangeClosed(1, items.length)
                .mapToObj(i -> items[items.length - i])
                .collect(Collectors.joining(", "));
    }

    /**
     * 获取cert里的subject
     */
    public static String getSubject(X509Certificate cert) {
        String issuer = cert.getSubjectDN().toString();
        String[] items = issuer.split(", ");
        return IntStream.rangeClosed(1, items.length)
                .mapToObj(i -> items[items.length - i])
                .collect(Collectors.joining(", "));
    }

    /**
     * 动态生成ca证书
     */
    public static X509Certificate genCert(String issuer, String subject, PrivateKey caPriKey, Date caNotBefore,
                                          Date caNotAfter, PublicKey publicKey, String... hosts) throws CertificateException {
        // 修改subject里的CN为当前域名
        boolean cn = false;
        if (hosts.length > 0) {
            StringBuilder sb = new StringBuilder(subject.length());
            for (String kv : subject.split(",")) {
                String[] kvArr = kv.split("=");
                if (kvArr.length >= 1) {
                    String k = kvArr[0].trim();
                    if ("CN".equalsIgnoreCase(k)) {
                        cn = true;
                        sb.append("CN=").append(hosts[0]).append(", ");
                        continue;
                    }
                }
                sb.append(kv.trim()).append(", ");
            }
            if (!cn) {
                sb.append("CN=").append(hosts[0]);
                subject = sb.toString();
            } else if (sb.length() > 2) {
                subject = sb.substring(0, sb.length() - 2);
            }
        }

        JcaX509v3CertificateBuilder jv3Builder = new JcaX509v3CertificateBuilder(new X500Name(issuer),
                // 证书序列号
                new BigInteger(String.valueOf(System.currentTimeMillis() + new Random().nextInt(1000)), 10),
                caNotBefore,
                caNotAfter,
                new X500Name(subject),
                publicKey);
        // SAN扩展证书支持多域名
        GeneralName[] generalNames = new GeneralName[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            generalNames[i] = new GeneralName(GeneralName.dNSName, hosts[i]);
        }
        try {
            GeneralNames subjectAltName = new GeneralNames(generalNames);
            jv3Builder.addExtension(Extension.subjectAlternativeName, false, subjectAltName);
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(caPriKey);
            return new JcaX509CertificateConverter().getCertificate(jv3Builder.build(signer));
        } catch (CertIOException | OperatorCreationException e) {
            throw new CertificateException(e);
        }
    }
}
