package com.idaymay.dzt.common.utils.file.excels;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO 
 * @author littlehui
 * @date 2021/12/6 16:49
 * @version 1.0
 */
public class ExcelItemListWriter {

    public static final String CELL = "cell";

    public static final String HEADER = "header";

    public static final String TITLE = "title";

    List<ExcelItemListWithOffset> sheetValues = new ArrayList<>();

    /**
     * 添加一行
     * @param excelItemLists
     */
    public void addRows(List<ExcelItemList> excelItemLists , int startRow, int startCol) {
        sheetValues.add(new ExcelItemListWithOffset(excelItemLists , startRow, startCol));
    }

    public HSSFWorkbook writeToExcel() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        //产生工作表对象
        HSSFSheet sheet = workbook.createSheet();
        Map<String, CellStyle> styles = createStyles(workbook);
        for (ExcelItemListWithOffset excelItemListWithOffset : sheetValues) {
            int rowPos = excelItemListWithOffset.startRow;
            int colPos = excelItemListWithOffset.startCol;
            sheet.autoSizeColumn(colPos);
            for (ExcelItemList excelItemList : excelItemListWithOffset.excelItemLists) {
                int rowPlace = excelItemList.getRowPlace() ;
                for (ExcelItem item : excelItemList.itemList) {
                    HSSFRow row = sheet.getRow(rowPos);
                    if (row == null) {
                        row =  sheet.createRow(rowPos);
                    }
                    HSSFCell cell = row.createCell(colPos);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue(item.getItemValue());
                    if (excelItemList.isHead()) {
                        cell.setCellStyle(styles.get(HEADER));
                    }else{
                        cell.setCellStyle(styles.get(CELL));
                    }
                    int endRowPos = rowPos + (rowPlace > 0 ?(rowPlace  - 1) :0 );
                    int endColPos = colPos + item.getColOffset();
                    sheet.addMergedRegion(new CellRangeAddress(rowPos,endRowPos ,colPos,endColPos - 1));
                    colPos = endColPos;
                }
                rowPos = rowPlace + rowPos ;
                colPos = excelItemListWithOffset.startCol;
            }
        }
        return workbook;
    }

    private Map<String, CellStyle> createStyles(HSSFWorkbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();
        CellStyle style;
        HSSFFont titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(titleFont);
        styles.put(TITLE, style);
        style = wb.createCellStyle();
        HSSFFont font=wb.createFont();
        //HSSFColor.VIOLET.index //字体颜色
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short)11);
        //字体增粗
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setWrapText(true);
        styles.put(HEADER, style);
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setWrapText(true);
        styles.put(CELL, style);
        return styles;
    }

    private class ExcelItemListWithOffset{
        List<ExcelItemList> excelItemLists;
        int startRow;
        int startCol;

        public ExcelItemListWithOffset( List<ExcelItemList> excelItemLists, int startRow, int startCol) {
            this.excelItemLists = excelItemLists;
            this.startRow = startRow;
            this.startCol = startCol;
        }
    }

    public static void main(String[] args) throws IOException {
        ExcelItemListWriter  excelItemListWriter = new ExcelItemListWriter();
        List<ExcelItemList> itemLists = new ArrayList<>();
        itemLists.add(new ExcelItemList(true).addItem("序号").addItem("订单编号").addItem("下单时间").addItem("收货人信息", 4).addItem("订单金额").addItem("货品金额").addItem("配送费用").addItem("商品来源").addItem("订单状态")
                .addItem("商品序号").addItem("商品编号").addItem("商品名称").addItem("商户商品编号").addItem("商品单价").addItem("数量").addItem("款式").addItem("尺寸").addItem("小计"));
        itemLists.add(new ExcelItemList(true).addItem("序号").addItem("订单编号").addItem("下单时间").addItem("收货人信息", 4).addItem("订单金额").addItem("货品金额").addItem("配送费用").addItem("商品来源").addItem("订单状态")
                .addItem("商品序号").addItem("商品编号").addItem("商品名称").addItem("商户商品编号").addItem("商品单价").addItem("数量").addItem("款式").addItem("尺寸").addItem("小计"));

        List<ExcelItemList> itemLists1 = new ArrayList<>();
        itemLists1.add(new ExcelItemList(4).addItem("aeeeeeeeeeeeeeeeeeeee" ,1 ).addItem("bbbbbbbbbbbbbbbbb",1).addItem("cccccccccccccccccccccc",1));
        itemLists1.add(new ExcelItemList(4).addItem("aeeeeeeeeeeeeeeeeeeee" ,1 ).addItem("bbbbbbbbbbbbbbbbb",1).addItem("cccccccccccccccccccccc",1));
        itemLists1.add(new ExcelItemList(4).addItem("aeeeeeeeeeeeeeeeeeeee" ,1 ).addItem("bbbbbbbbbbbbbbbbb",1).addItem("cccccccccccccccccccccc",1));

        excelItemListWriter.addRows(itemLists , 0 , 0);
        excelItemListWriter.addRows(itemLists1 , itemLists.size() , 0);
        HSSFWorkbook sheet = excelItemListWriter.writeToExcel();
        FileOutputStream fileOut = new FileOutputStream("d:/workbook.xls");
        sheet.write(fileOut);
        fileOut.close();
    }

}
