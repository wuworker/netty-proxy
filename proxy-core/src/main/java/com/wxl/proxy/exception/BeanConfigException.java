package com.wxl.proxy.exception;

import lombok.Getter;
import org.springframework.beans.BeansException;

/**
 * Created by wuxingle on 2019/9/26.
 * bean配置异常
 */
@SuppressWarnings("serial")
public class BeanConfigException extends BeansException implements ExceptionRepairAction {

    @Getter
    private final String configKey;

    private final String action;

    public BeanConfigException(String key, String msg) {
        super(msg);
        this.configKey = key;
        this.action = "Please check '" + configKey + "' autoconfig";
    }

    public BeanConfigException(String key, String msg, String action) {
        super(msg);
        this.configKey = key;
        this.action = action;
    }

    public BeanConfigException(String key, String msg, String action, Throwable cause) {
        super(msg, cause);
        this.configKey = key;
        this.action = action;
    }

    public BeanConfigException(String key, String msg, Throwable cause) {
        super(msg, cause);
        this.configKey = key;
        this.action = "Please check '" + configKey + "' autoconfig";
    }

    @Override
    public String action() {
        return action;
    }
}
