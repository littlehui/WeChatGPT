package com.idaymay.dzt.common.utils.file.excels;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2021/12/6 16:49
 */
public class ExcelUtil {

//    public static void main(String[] args) {
//        ExcelResult excelResult = readExcelByPath("F:\\contend.xlsx",3,2);
//        System.out.println(excelResult.getExcelValues().length);
//    }

    /**
     * 从文件路径读取Excel
     *
     * @param path
     * @param cellCountPerRow
     * @param startRow
     * @return org.tinycode.utils.common.excels.ExcelResult
     * @author littlehui
     * @date 2021/12/6 16:57
     */
    public static ExcelResult readExcelByPath(String path, int cellCountPerRow, int startRow) {
        XSSFWorkbook xwb = null;
        try {
            OPCPackage pkg = OPCPackage.open(path);
            xwb = new XSSFWorkbook(pkg);
            pkg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readExcel(xwb, cellCountPerRow, startRow);
    }

    /**
     * 从Excel文件读取内容
     *
     * @param file
     * @param cellCountPerRow
     * @param startRow
     * @return org.tinycode.utils.common.excels.ExcelResult
     * @author littlehui
     * @date 2021/12/6 16:55
     */
    public static ExcelResult readExcelFromFile(File file, int cellCountPerRow, int startRow) {
        XSSFWorkbook xwb = null;
        try {
            OPCPackage pkg = OPCPackage.openOrCreate(file);
            xwb = new XSSFWorkbook(pkg);
            pkg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readExcel(xwb, cellCountPerRow, startRow);
    }

    /**
     * 从XSSFWorkbook文件读取Excel
     *
     * @param xwb
     * @param cellCountPerRow
     * @param startRow
     * @return org.tinycode.utils.common.excels.ExcelResult
     * @author littlehui
     * @date 2021/12/6 16:56
     */
    public static ExcelResult readExcel(XSSFWorkbook xwb, int cellCountPerRow, int startRow) {
        // 读取第一章表格内容
        XSSFSheet sheet = xwb.getSheetAt(0);
        if (sheet == null) {
            throw new RuntimeException("excel的第一个tab页为空");
        }
        // 定义 row、cell
        XSSFRow row;
        int rowCount = 0;
        int realRowIndex = 0;
        String[][] excelValues = new String[sheet.getPhysicalNumberOfRows() - startRow + 1][cellCountPerRow];
        // 循环输出表格中的内容
        for (int i = sheet.getFirstRowNum(); i < sheet.getPhysicalNumberOfRows(); i++) {
            //获取行
            if (i < (startRow - 1)) {
                continue;
            }
            row = sheet.getRow(i);
            for (int cellIndex = 0; cellIndex < cellCountPerRow; cellIndex++) {
                String cellValue = "";
                if (row == null) {
                    excelValues[realRowIndex][cellIndex] = "";
                } else {
                    if (row.getCell(cellIndex) != null) {
                        row.getCell(cellIndex).setCellType(Cell.CELL_TYPE_STRING);
                        cellValue = row.getCell(cellIndex).getRichStringCellValue().getString();
                    }
                    excelValues[realRowIndex][cellIndex] = cellValue;
                }
            }
            realRowIndex++;
            rowCount++;
        }
        ExcelResult result = new ExcelResult(excelValues, rowCount, cellCountPerRow);
        return result;
    }

    public static String[] objectToArrays(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> values = new ArrayList();
        for (Field field : fields) {
            // 拿到该属性的getset方法
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

    private static String getMethodName(String fildeName) throws Exception {
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }

}
