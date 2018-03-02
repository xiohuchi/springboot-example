package com.dianmi.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author zhiwei loong
 * @Date 2017年11月24日 下午3:48:22
 * @Description
 */
public class CreateNewExcel {

	public static void main(String args[]) {
		createExcel();
	}

	public static void createExcel() {
		try {

			Workbook wb = new XSSFWorkbook();
			;
			Sheet sheet = wb.createSheet("sheet1");
			FileOutputStream fos = new FileOutputStream("C:/我的文档/excel.xlsx");
			wb.write(fos);
			fos.close();
		} catch (EncryptedDocumentException | IOException e) {
			e.printStackTrace();
		}
	}
}