package com.idaymay.dzt.common.utils.file.excels;

/**
 * Excel单个Item描述
 * @author littlehui
 * @date 2021/12/6 16:48
 * @version 1.0
 */
public class ExcelItem {
    /**
     * 单元格内容
     */
    private String itemValue;

    /**
     * 列偏移量
     */
    private int colOffset = 1;


    public ExcelItem(String itemValue, int colOffset) {
        this.itemValue = itemValue;
        this.colOffset = colOffset;
    }

    public ExcelItem(String itemValue) {
        this.itemValue = itemValue;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }


    public int getColOffset() {
        return colOffset;
    }

    public void setColOffset(int colOffset) {
        this.colOffset = colOffset;
    }
}
