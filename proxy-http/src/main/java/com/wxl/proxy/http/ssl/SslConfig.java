package com.wxl.proxy.http.ssl;

import io.netty.handler.ssl.SslContext;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

/**
 * Created by wuxingle on 2019/9/24.
 * http代理的ssl配置，用https解密
 */
@Getter
@ToString(exclude = {"caPrivateKey", "serverPriKey", "serverPubKey", "clientSslContext"})
@Builder
public class SslConfig {

    /**
     * ca私钥
     */
    private PrivateKey caPrivateKey;

    private String issuer;

    private String subject;

    private Date caNotBefore;

    private Date caNotAfter;

    /**
     * 服务端公私钥
     */
    private PrivateKey serverPriKey;

    private PublicKey serverPubKey;

    /**
     * 和真实服务连接的ssl
     */
    private SslContext clientSslContext;
}
