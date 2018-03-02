package com.dianmi.utils.excel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

public final class ExcelStyleBuilder {
	private XSSFCellStyle cellStyle;
	private Workbook workBook;
	private Font font;

	public XSSFCellStyle getCellStyle() {
		return cellStyle;
	}

	public ExcelStyleBuilder(Workbook workBook) {
		this.workBook = workBook;
		this.cellStyle = (XSSFCellStyle) workBook.createCellStyle();
		this.font = workBook.createFont();
		this.cellStyle.setFont(this.font);
		setFontName("宋体");
	}

	// 通用型表头
	public ExcelStyleBuilder getCellTitleStyle() {
		// 一、设置背景色：
		XSSFColor color = new XSSFColor(new java.awt.Color(221, 217, 196));
		cellStyle.setFillForegroundColor(color);// 设置背景色
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// 二、设置边框:
		addBorder();
		setFontSize((short) 11);
		// 五、设置列宽:
		// sheet.setColumnWidth(0, 3766); //第一个参数代表列id(从0开始),第2个参数代表宽度值
		// 六、设置自动换行:
		// cellStyle.setWrapText(true);//设置自动换行
		return this;
	}

	public ExcelStyleBuilder getCollectStyle() {
		font.setColor(Font.COLOR_RED);
		return this;
	}

	// 二、设置边框
	public ExcelStyleBuilder addBorder() {
		cellStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		cellStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		cellStyle.setBorderTop(BorderStyle.THIN);// 上边框
		cellStyle.setBorderRight(BorderStyle.THIN);// 右边框
		return this;
	}

	// 设置字体
	public ExcelStyleBuilder setFontName(String fontName) {
		font.setFontName(fontName);
		return this;
	}

	// 设置字体
	public ExcelStyleBuilder setBold(boolean bold) {
		font.setBold(bold);
		return this;
	}

	// 设置水平
	public ExcelStyleBuilder setHorizontalAlignment(HorizontalAlignment align) {
		cellStyle.setAlignment(align);
		return this;
	}

	// 设置居中
	public ExcelStyleBuilder setVerticalAlignment(VerticalAlignment align) {
		cellStyle.setVerticalAlignment(align);
		return this;
	}

	// 设置字体大小
	public ExcelStyleBuilder setFontSize(short fontSize) {
		font.setFontHeightInPoints(fontSize);
		return this;
	}

}
