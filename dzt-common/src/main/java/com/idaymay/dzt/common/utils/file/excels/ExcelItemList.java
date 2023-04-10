package com.idaymay.dzt.common.utils.file.excels;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel行
 *
 * @author littlehui
 * @version 1.0
 * @date 2021/12/6 16:49
 */
public class ExcelItemList {
    /**
     * 是否为头,头会加粗字体
     */
    private boolean isHead = false;

    List<ExcelItem> itemList = new ArrayList<>();

    /**
     * 行跨度,默认1
     */
    private Integer rowPlace = 1;

    public ExcelItemList(boolean isHead) {
        this.isHead = isHead;
    }

    public ExcelItemList(Integer rowPlace) {
        if (rowPlace == 0) {
            rowPlace = 1;
        }

        this.rowPlace = rowPlace;
    }

    public ExcelItemList() {
    }

    public ExcelItemList addItem(String value) {
        itemList.add(new ExcelItem(value));
        return this;
    }

    public ExcelItemList addItem(String value, int col) {
        itemList.add(new ExcelItem(value, col));
        return this;
    }

    /**
     * y轴偏移量
     *
     * @return
     */
    public int countTotalColOffset() {
        int totalOffset = 0;
        for (ExcelItem item : itemList) {
            totalOffset = totalOffset + item.getColOffset();
        }
        return totalOffset;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean isHead) {
        this.isHead = isHead;
    }

    public Integer getRowPlace() {
        return rowPlace;
    }

    public void setRowPlace(Integer rowPlace) {
        this.rowPlace = rowPlace;
    }
}
