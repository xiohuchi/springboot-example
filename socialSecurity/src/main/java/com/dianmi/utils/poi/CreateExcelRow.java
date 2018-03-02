package com.dianmi.utils.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * @author zhiwei loong
 */
public class CreateExcelRow {

	public void insertRows(String filePath, String sheetName, int point, String[] values) {
		Workbook wb = createWorkbook(filePath);
		Sheet sheet = wb.getSheet(sheetName);
		Row row = createRow(sheet, point);
		for (int i = 0; i < values.length; i++) {
			row.createCell(i).setCellValue(values[i]);
		}
		saveExcel(wb, filePath);
	}

	/**
	 * @param wb
	 */
	private void saveExcel(Workbook wb, String excelPath) {
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(excelPath);
			wb.write(fileOut);
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	private Workbook createWorkbook(String excelPath) {
		Workbook wb = null;
		FileInputStream fis = null;
		File f = new File(excelPath);
		try {
			if (f != null) {
				fis = new FileInputStream(f);
				// wb = new XSSFWorkbook(fis);
				wb = WorkbookFactory.create(fis);
			}
		} catch (Exception e) {
			return null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return wb;
	}

	/**
	 * @param sheet
	 * @param rowIndex
	 * @return
	 */
	private Row createRow(Sheet sheet, Integer rowIndex) {
		Row row = null;
		if (sheet.getRow(rowIndex) != null) {
			int lastRowNo = sheet.getLastRowNum();
			sheet.shiftRows(rowIndex, lastRowNo, 1);
		}
		row = sheet.createRow(rowIndex);
		return row;
	}
}