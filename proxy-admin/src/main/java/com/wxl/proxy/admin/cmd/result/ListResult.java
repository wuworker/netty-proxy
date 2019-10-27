package com.wxl.proxy.admin.cmd.result;

import com.wxl.proxy.admin.cmd.AdminCommandResult;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2019/10/27
 * 列表结果
 */
public class ListResult implements AdminCommandResult {

    private Collection<String> list;

    public ListResult(Collection<String> list) {
        this.list = list;
    }

    @Override
    public String format() {
        if (CollectionUtils.isEmpty(list)) {
            return LINE_SEPARATOR;
        }
        return list.stream().collect(Collectors.joining(LINE_SEPARATOR));
    }
}
