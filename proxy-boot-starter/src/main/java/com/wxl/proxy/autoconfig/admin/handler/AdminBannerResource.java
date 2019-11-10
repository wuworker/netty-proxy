package com.wxl.proxy.autoconfig.admin.handler;

import com.wxl.proxy.ProxySystemConstants;
import org.springframework.core.io.ByteArrayResource;

import static com.wxl.proxy.ProxySystemConstants.DEFAULT_LINE_SPLIT;

/**
 * Create by wuxingle on 2019/11/9
 * 管理员默认banner
 */
public class AdminBannerResource extends ByteArrayResource {

    public static final byte[] BANNER = (DEFAULT_LINE_SPLIT +
            " _                     " + DEFAULT_LINE_SPLIT +
            "|_._ _    /\\ _._ _ o._ " + DEFAULT_LINE_SPLIT +
            "| | (_>\\//--(_| | ||| |" + DEFAULT_LINE_SPLIT +
            "       /               " + DEFAULT_LINE_SPLIT)
            .getBytes(ProxySystemConstants.DEFAULT_CHARSET);

    public static void main(String[] args) {
        System.out.println(new String(BANNER));
    }

    public AdminBannerResource() {
        super(BANNER);
    }

    public AdminBannerResource(String description) {
        super(BANNER, description);
    }
}
