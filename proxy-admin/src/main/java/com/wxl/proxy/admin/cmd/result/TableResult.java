package com.wxl.proxy.admin.cmd.result;

import com.wxl.proxy.admin.cmd.AmdResult;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.wxl.proxy.ProxySystemConstants.DEFAULT_LINE_SPLIT;

/**
 * Create by wuxingle on 2019/10/27
 * 显示表格结果
 */
public class TableResult implements AmdResult {

    private String columnSplit;

    private List<String> title = new ArrayList<>();

    private List<List<String>> tables = new ArrayList<>();

    private int currentRow = -1;

    public TableResult() {
        this("    ");
    }

    public TableResult(String columnSplit) {
        this.columnSplit = columnSplit;
    }

    /**
     * 设置表格标题
     */
    public TableResult setTitle(String... title) {
        this.title.clear();
        this.title.addAll(Arrays.asList(title));
        return this;
    }

    /**
     * 获取标题
     */
    public List<String> getTitle() {
        return title;
    }

    /**
     * 下一行
     */
    public TableResult nextRow() {
        currentRow++;
        tables.add(new ArrayList<>());
        return this;
    }

    public TableResult nextRow(int size) {
        currentRow++;
        tables.add(new ArrayList<>(size));
        return this;
    }

    /**
     * 增加一列
     */
    public TableResult addColumn(String column) {
        if (currentRow == -1) {
            nextRow();
        }
        tables.get(currentRow).add(column);
        return this;
    }

    /**
     * 设置当前在第几行
     */
    public TableResult setCurrentRow(int index) {
        rowRangeCheck(index);
        currentRow = index;
        return this;
    }

    /**
     * 设置行列数据
     */
    public TableResult set(int row, int column, String str) {
        columnRangeCheck(row, column);
        List<String> list = tables.get(row);
        list.set(column, str);
        return this;
    }

    /**
     * 获取单元格数据
     */
    public String index(int row, int column) {
        columnRangeCheck(row, column);
        return tables.get(row).get(column);
    }


    @Override
    public String toString() {
        int columns = title.size();
        for (List<String> row : tables) {
            if (row.size() > columns) {
                columns = row.size();
            }
        }

        Map<Integer, Integer> columnMaxLen = new HashMap<>();
        for (int i = 0; i < title.size(); i++) {
            columnMaxLen.put(i, title.get(i).length());
        }
        for (int i = 0; i < columns; i++) {
            Integer len = columnMaxLen.getOrDefault(i, 0);
            for (List<String> row : tables) {
                if (row.size() > i) {
                    int l = row.get(i).length();
                    if (l > len) {
                        columnMaxLen.put(i, l);
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        appendRow(sb, title, columnMaxLen);

        if (!CollectionUtils.isEmpty(tables)) {
            sb.append(DEFAULT_LINE_SPLIT);
            Iterator<List<String>> it = tables.iterator();
            while (it.hasNext()) {
                List<String> table = it.next();
                appendRow(sb, table, columnMaxLen);
                if (it.hasNext()) {
                    sb.append(DEFAULT_LINE_SPLIT);
                }
            }
        }

        return sb.toString();
    }


    private void appendRow(StringBuilder sb, List<String> row, Map<Integer, Integer> columnMaxLen) {
        for (int i = 0, l = row.size(); i < l; i++) {
            Integer len = columnMaxLen.get(i);
            String s = row.get(i);
            fillSpace(sb, s, len);
            if (i < l - 1) {
                sb.append(columnSplit);
            }
        }
    }

    private void fillSpace(StringBuilder sb, String s, int len) {
        int spaceNum = (len - s.length()) / 2;
        for (int i = 0; i < spaceNum; i++) {
            sb.append(' ');
        }
        sb.append(s);
        for (int i = 0; i < spaceNum; i++) {
            sb.append(' ');
        }
    }

    private void rowRangeCheck(int row) {
        if (row < 0 || row >= tables.size()) {
            throw new IndexOutOfBoundsException("row index out of range:" + row);
        }
    }

    private void columnRangeCheck(int row, int column) {
        rowRangeCheck(row);
        List<String> list = tables.get(row);
        if (column < 0 || column >= list.size()) {
            throw new IndexOutOfBoundsException("column index out of range:" + column);
        }
    }

}
