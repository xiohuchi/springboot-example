package com.dianmi.excel;

import java.util.List;

import com.dianmi.utils.poi.ReadExcel;

/**
 * created by www 2017/9/30 11:32
 */
public class ReadExcelTest {

	public static void main(String[] args) {
		String filePath = "C:/Users/www/Desktop/账单明细.xls";
		List<List<String[]>> allList = ReadExcelToStrArr.readExcel2List(filePath);
		List<String[]> l1 = allList.get(0);
		for (int i = 0; i < l1.size(); i++) {
			String[] strArr = l1.get(i);
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
}