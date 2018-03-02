package com.dianmi.excel;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.alibaba.druid.util.StringUtils;

/**
 * @author zhiwei loong
 * @Date 2017年11月24日 下午1:51:10
 * @Description
 */
public class ReadExcelToStrArr {
	public static void main(String[] args) {
		String filePath = "C:/Users/www/Desktop/费用单/深圳.xls";
		List<List<String[]>> allList = readExcel2List(filePath);
		List<String[]> l1 = allList.get(0);
		for (int i = 0; i < l1.size(); i++) {
			System.out.println("行号：" + i + "--------------------------------------");
			String[] strArr = l1.get(i);
			String xuhao = l1.get(i)[0] == null?"":l1.get(i)[0];
			boolean flag = xuhao.equals("序号");
			if(flag){
				System.out.println("这是表头行，数据从这里获取...");
			}
			for (int j = 0; j < strArr.length; j++) {
				if (j == 2) {
					if (strArr[j] == null || strArr[j].equals("")) {
						strArr[j] = "";
					}
				}
				System.out.print(strArr[j] + "\t");
			}
			System.out.println();
		}
	}

	public static List<List<String[]>> readExcel2List(String excelFilePath) {
		List<List<String[]>> allList = new ArrayList<List<String[]>>();
		try {
			File file = new File(excelFilePath);
			FileInputStream is = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(is);
			Sheet sheet = null;
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				sheet = workbook.getSheetAt(i);
				// int rowLength = sheet.getRow(0).getLastCellNum();
				// System.out.println(rowLength);
				List<String[]> list = new ArrayList<String[]>();
				for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
					Row row = sheet.getRow(j);
					if (null != row) {
						String[] strArr = new String[row.getLastCellNum()];
						// String[] strArr = new String[rowLength];
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
}