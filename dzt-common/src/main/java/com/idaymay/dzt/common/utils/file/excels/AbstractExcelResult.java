package com.idaymay.dzt.common.utils.file.excels;

import org.apache.poi.hssf.usermodel.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel返回的对象
 * @author littlehui
 * @date 2021/12/6 16:47
 * @version 1.0
 */
public abstract class AbstractExcelResult<T> {

    protected String[] rowHeaders;

    protected ExcelResult excelResult;

    public AbstractExcelResult() {

    }

    public AbstractExcelResult(List<T> tLists) {
        T[] tlisArray = (T[]) tLists.toArray(new Object[tLists.size()]);
        String[][] tArrayArray = new String[tLists.size()][];
        for (int i = 0; i < tlisArray.length; i ++) {
            String[] needArray = AbstractExcelResult.objectToArrays(tlisArray[i]);
            tArrayArray[i] = needArray;
        }
        try {
            int headerLength = 0;
            initHeaders();
            if (tlisArray.length < 1 || tArrayArray.length < 1) {
                headerLength = rowHeaders.length;
            } else {
                headerLength = tArrayArray[0].length;
            }
            excelResult =new ExcelResult(tArrayArray, tlisArray.length, headerLength);
            if (excelResult.getCellCountPerRow() != rowHeaders.length) {
                throw new ExcelException("头列数和数据列数不匹配：需要" + excelResult.getCellCountPerRow() + "实际：" + rowHeaders.length);
            }
        } catch (ExcelException e) {
            e.printStackTrace();
        }
    }

    public AbstractExcelResult(ExcelResult excelResult, String[] rowHeaders) throws ExcelException {
        if (excelResult.getCellCountPerRow() != rowHeaders.length) {
            throw new ExcelException("头列数和数据列数不匹配：需要" + excelResult.getCellCountPerRow() + "实际：" + rowHeaders.length);
        }
        this.rowHeaders = rowHeaders;
        this.excelResult = excelResult;
    }

    public HSSFWorkbook writeExcelBook() throws UnsupportedEncodingException, ExcelException {
        // 生成提示信息
        // 产生工作簿对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        //产生工作表对象
        HSSFSheet sheet = workbook.createSheet();
        if (excelResult == null || rowHeaders == null) {
            throw new ExcelException("excelResult为null或者rowHeaders为null");
        }
        //创建头，第一行
        sheet = this.addHeader(sheet);
        for (int i=0; i<excelResult.getRowCount(); i++) {
            HSSFRow row = sheet.createRow(i+1);
            HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
            for (int j=0; j<excelResult.getCellCountPerRow(); j++) {
                hssfCellStyle.setWrapText(true);
                hssfCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                hssfCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                HSSFCell cell = row.createCell(j);
                //创建一列
                cell.setCellStyle(hssfCellStyle);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(excelResult.getExcelValues()[i][j]);
                if (i == 0) {
                    sheet.autoSizeColumn(j);
                }
            }
        }
        return workbook;
    }

    private HSSFSheet addHeader(HSSFSheet sheet) {
        //创建一行
        HSSFRow headerRow = sheet.createRow(0);
        for (int i=0; i<rowHeaders.length; i++) {
            HSSFFont hssfFont = sheet.getWorkbook().createFont();
            hssfFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            HSSFCellStyle hssfCellStyle = sheet.getWorkbook().createCellStyle();
            hssfCellStyle.setFont(hssfFont);
            //创建一列
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(rowHeaders[i]);
            cell.setCellStyle(hssfCellStyle);
            sheet.autoSizeColumn(i);
        }
        return sheet;
    }


    public static String[] objectToArrays(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> values = new ArrayList();
        for (Field field : fields) {
            //拿到该属性的getset方法
            Method m;
            try {
                m = obj.getClass().getMethod(
                        "get" + getMethodName(field.getName()));
                @SuppressWarnings("RedundantCast")
                Object val = (Object) m.invoke(obj);
                if (val == null) {
                    values.add("");
                } else {
                    values.add(val + "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return values.toArray(new String[values.size()]);
    }
    private static String getMethodName(String fildeName) throws Exception{
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }

    /**
     * header进行初始化
     */
    public abstract void initHeaders();
}
