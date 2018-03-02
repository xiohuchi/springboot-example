package com.dianmi.utils.poi;

import com.alibaba.druid.util.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * created by www 2017/11/10 10:56
 */
public class ReadExcel {
    /**
     * @param excelFilePath
     * @return
     */
    public static List<List<String[]>> readRegularExcel2List(String excelFilePath) {
        List<List<String[]>> allList = new ArrayList<List<String[]>>();
        try {
            File file = new File(excelFilePath);
            FileInputStream is = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = null;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheet = workbook.getSheetAt(i);
                int rowLength = sheet.getRow(0).getLastCellNum();
                List<String[]> list = new ArrayList<String[]>();
                for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
                    Row row = sheet.getRow(j);
                    if (null != row) {
                        String[] strArr = new String[rowLength];
                        for (int k = 0; k < row.getLastCellNum(); k++) {
                            if (null != row.getCell(k)) {
                                Cell cell = row.getCell(k);
                                if (null != cell) {
                                    cell.setCellType(CellType.STRING);
                                    strArr[k] = cell.getStringCellValue().trim();
                                } else {
                                    strArr[k] = "";
                                }
                            }
                        }
                        int flag = 0;
                        for (int k = 0; k < strArr.length; k++) {
                            if (!StringUtils.isEmpty(strArr[k]))
                                flag += 1;
                        }
                        if (flag != 0) {
                            list.add(strArr);
                        }
                    }
                }
                allList.add(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allList;
    }

    public static List<List<String[]>> readIrregularExcel2List(String excelFilePath) {
        List<List<String[]>> allList = new ArrayList<List<String[]>>();
        try {
            File file = new File(excelFilePath);
            FileInputStream is = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = null;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheet = workbook.getSheetAt(i);
                List<String[]> list = new ArrayList<String[]>();
                for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
                    Row row = sheet.getRow(j);
                    if (null != row) {
                        String[] strArr = new String[row.getLastCellNum()];
                        for (int k = 0; k < row.getLastCellNum(); k++) {
                            if (null != row.getCell(k)) {
                                Cell cell = row.getCell(k);
                                if (null != cell) {
                                    cell.setCellType(CellType.STRING);
                                    strArr[k] = cell.getStringCellValue().trim();
                                } else {
                                    strArr[k] = "";
                                }
                            }
                        }
                        int flag = 0;
                        for (int k = 0; k < strArr.length; k++) {
                            if (!StringUtils.isEmpty(strArr[k]))
                                flag += 1;
                        }
                        if (flag != 0) {
                            list.add(strArr);
                        }
                    }
                }
                allList.add(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allList;
    }


    public static List<List<String[]>> readExcelList(String excelFilePath) {
        List<List<String[]>> allList = new ArrayList<>();
        try {


            XSSFWorkbook wb = new XSSFWorkbook(excelFilePath);

            XSSFSheet sheet = null;

            XSSFSheet sheetAt = wb.getSheetAt(0);
            for (Row row : sheetAt) {
                for (Cell cell : row) {
                    CellRangeAddress ca = ReadExcel.getMergedRegionValue(sheetAt, cell);
                    if (ca != null) {
                        System.out.println("起始行:" + ca.getFirstRow());
                        System.out.println("结束行:" + ca.getFirstRow());
                    }
                    System.out.println("当前下标：" + row.getRowNum());
                    System.out.println("++++++" + row.getLastCellNum());
                    System.out.println("++++++" + row.getFirstCellNum());
                }
            }

//
//            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
//                sheet = wb.getSheetAt(i);
//                List<String[]> list = new ArrayList<>();
//                for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
//                    Row row = sheet.getRow(j + 1);
//                    if (null != row) {
//                        CellRangeAddress ca = null;
//                        String[] strArr = new String[row.getLastCellNum()];
//                        for (int k = 0; k < row.getLastCellNum(); k++) {
//                            if (null != row.getCell(k)) {
//                                Cell cell = row.getCell(k);
//                                ca = ReadExcel.getMergedRegionValue(sheet, cell);
//                                if (null != cell) {
//                                    cell.setCellType(CellType.STRING);
//                                    strArr[k] = cell.getStringCellValue().trim();
//                                } else {
//                                    strArr[k] = "";
//                                }
//                            }
//                        }
//                        int flag = 0;
//                        for (int k = 0; k < strArr.length; k++) {
//                            if (!StringUtils.isEmpty(strArr[k]))
//                                flag += 1;
//                        }
//                        if (flag != 0) {
//                            if (ca != null) {
//                                if (strArr.length >= ca.getFirstRow() || strArr.length <= ca.getLastRow()) {
//                                    System.out.println("------------------" + ca.getFirstRow());
//                                    System.out.println("------------------" + ca.getLastRow());
//
//                                }
//                            }
//                            list.add(strArr);
//                        }
//                    }
//                }
//                allList.add(list);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allList;
    }

    public static CellRangeAddress getMergedRegionValue(XSSFSheet sheet, Cell cell) {
        // 获得一个 sheet 中合并单元格的数量
        int sheetmergerCount = sheet.getNumMergedRegions();
        // 便利合并单元格
        for (int i = 0; i < sheetmergerCount; i++) {
            // 获得合并单元格
            CellRangeAddress ca = sheet.getMergedRegion(i);
            // 获得合并单元格的起始行, 结束行, 起始列, 结束列
            int firstC = ca.getFirstColumn();
            int firstR = ca.getFirstRow();

            if (cell.getColumnIndex() == firstC && cell.getRowIndex() == firstR) {
                return ca;
            }
        }
        return null;
    }


}