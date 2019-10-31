package com.wxl.proxy.admin.cmd.result;

import com.wxl.proxy.admin.cmd.AmdResult;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.wxl.proxy.ProxySystemConstants.DEFAULT_LINE_SPLIT;

/**
 * Create by wuxingle on 2019/10/27
 * 列表结果
 */
public class ListResult implements AmdResult {

    private Collection<String> list;

    public ListResult(Collection<String> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        if (CollectionUtils.isEmpty(list)) {
            return DEFAULT_LINE_SPLIT;
        }
        return list.stream().collect(Collectors.joining(DEFAULT_LINE_SPLIT));
    }
}
