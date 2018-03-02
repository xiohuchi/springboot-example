package com.dianmi.utils.poi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author zhiwei loong
 */
public class WriteExcel {
	public static void main(String args[]) throws IOException {

	}

	@SuppressWarnings("resource")
	public void listWrite2Excel(List<Map<String, List<String[]>>> lists, String saveFilePathAndName) {
		for (int i = 0; i < lists.size(); i++) {
			// 创建excel工作簿
			Workbook wb = new HSSFWorkbook();
			Map<String, List<String[]>> map = lists.get(i);
			Set<String> keySet = map.keySet();
			Iterator<String> it = keySet.iterator();
			while (it.hasNext()) {
				String key = it.next();
				// 创建第一个sheet（页），命名为 new sheet
				Sheet sheet = wb.createSheet(key);
				// 创建一行，在页sheet上
				Row row = sheet.createRow((short) 0);
				List<String[]> list = map.get(key);
				for (int j = 0; j < list.size(); j++) {
					String[] rows = list.get(j);
					for (int k = 0; k < rows.length; k++) {
						String value = rows[k];
						row.createCell(k).setCellValue(value);
					}
				}
			}
			// 创建一个excel文件
			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream(saveFilePathAndName);
				// 把上面创建的工作簿输出到文件中
				wb.write(fileOut);
				// 关闭输出流
				fileOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}