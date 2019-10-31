package com.wxl.proxy.admin.cmd.result;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Create by wuxingle on 2019/10/31
 */
public class TableResultTest {


    @Test
    public void test(){
        TableResult result = new TableResult();
        result.setTitle("t1","t2","t333");
        result.nextRow().addColumn("abc")
                .addColumn("defgfgf")
                .addColumn("8779789er7878");
        result.nextRow()
                .addColumn("dsfsdfsd")
                .addColumn("9383845")
                .addColumn("0dfsdfsfshfjsdhfhdfsdf");
        result.nextRow()
                .addColumn("5466546")
                .addColumn("df")
                .addColumn("fds7ff");
        System.out.println(result);
        System.out.println(result.toString().endsWith("\n"));
    }
}