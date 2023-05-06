package com.idaymay.dzt.common.utils.file.excels;

import java.util.Iterator;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2021/12/6 16:58
 */
public class ExcelResult implements Iterable<String[]> {

    private int rowCount;

    private int cellCountPerRow;

    private String[][] excelValues;

    public ExcelResult(String[][] excelValues, int rowCount, int cellCountPerRow) {
        this.excelValues = excelValues;
        this.rowCount = rowCount;
        this.cellCountPerRow = cellCountPerRow;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getCellCountPerRow() {
        return cellCountPerRow;
    }

    public void setCellCountPerRow(int cellCountPerRow) {
        this.cellCountPerRow = cellCountPerRow;
    }

    public String[][] getExcelValues() {
        return excelValues;
    }

    public void setExcelValues(String[][] excelValues) {
        this.excelValues = excelValues;
    }

    @Override
    public Iterator<String[]> iterator() {
        Iterator<String[]> iterator = new Iterator<String[]>() {
            // index of next element to return
            int cursor;

            @Override
            public boolean hasNext() {
                return cursor < rowCount;
            }

            @Override
            public String[] next() {
                String[] next = new String[cellCountPerRow];
                for (int i = 0; i < cellCountPerRow; i++) {
                    next[i] = excelValues[cursor][i];
                }
                cursor++;
                return next;
            }

            @Override
            public void remove() {
                if (hasNext()) {
                    for (int i = cursor; i < rowCount - 1; i++) {
                        for (int r = 0; r < cellCountPerRow; r++) {
                            if (excelValues[i + 1] != null) {
                                excelValues[i][r] = excelValues[i + 1][r];
                            }
                        }
                    }
                    cursor--;
                    rowCount--;
                }
            }
        };
        return iterator;
    }
}
