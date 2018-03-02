package com.dianmi.utils.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author zhiwei loong
 */
public class CopyExcel {
	@SuppressWarnings("resource")
	public static void copy(String fromFilePath, String toFilePath) {
		try {
			File file = new File(fromFilePath);
			FileInputStream is = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(is);
			Sheet sheet = null;
			SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(), 100);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				sheet = workbook.getSheetAt(i);// 获取第一个sheet
				// int rowLength = sheet.getRow(0).getLastCellNum();
				// 创建第一个sheet（页），命名
				Sheet copySheet = wb.createSheet(sheet.getSheetName());
				for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
					Row row = sheet.getRow(j);
					Row copyRow = copySheet.createRow((short) j);
					if (null != row) {
						for (int k = 0; k < row.getLastCellNum(); k++) {
							if (null != row.getCell(k)) {
								Cell cell = row.getCell(k);
								switch (cell.getCellTypeEnum()) {
								case STRING:
									copyRow.createCell(k).setCellValue(cell.getRichStringCellValue().getString());
									break;
								case NUMERIC:
									copyRow.createCell(k).setCellValue(cell.getNumericCellValue());
									break;
								case BOOLEAN:
									copyRow.createCell(k).setCellValue(cell.getBooleanCellValue());
									break;
								case FORMULA:
									copyRow.createCell(k).setCellValue(cell.getCellFormula());
									break;
								case BLANK:
									copyRow.createCell(k).setCellValue("");
									break;
								default:
								}
							}
						}
					}
				}
			}
			try {
				FileOutputStream fileOut = new FileOutputStream(toFilePath);
				wb.write(fileOut);
				fileOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			e.printStackTrace();
		}
	}
}