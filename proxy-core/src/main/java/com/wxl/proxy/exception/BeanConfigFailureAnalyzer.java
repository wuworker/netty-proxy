package com.wxl.proxy.exception;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Created by wuxingle on 2019/9/27.
 * 配置错误的FailureAnalyzer
 */
public class BeanConfigFailureAnalyzer extends AbstractFailureAnalyzer<BeanConfigException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, BeanConfigException cause) {
        String configKey = cause.getConfigKey();
        String desc = configKey != null ? "Bad autoconfig '" + configKey + "', " + cause.getMessage() :
                cause.getMessage();
        return new FailureAnalysis(desc, cause.action(), cause);
    }
}
