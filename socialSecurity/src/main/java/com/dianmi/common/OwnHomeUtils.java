package com.dianmi.common;

import java.io.File;
import java.math.BigDecimal;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import com.dianmi.mapper.SupplierCostMapper;
import com.dianmi.utils.file.UploadFile;

/**
 * 费用工具类
 * 
 * @author www
 *
 */
@Component
public class OwnHomeUtils {

	/**
	 * @param file
	 * @return filePath(文件上传后的路径)
	 * @description 上传Excel文件
	 */
	public static String uploadFile(MultipartFile file) {
		String fileName = file.getOriginalFilename();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String fileExtension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
		String filePath = request.getSession().getServletContext().getRealPath("/") + "upload" + File.separator
				+ "excel" + File.separator;
		String newFileName = UUID.randomUUID() + fileExtension;
		try {
			UploadFile.upload(file, filePath, newFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath + newFileName;
	}

	/**
	 * 读取excel文件
	 * 
	 * @param sheet
	 * @return Integer 开始行号
	 */
	public static String getSpecificRow(Sheet sheet) {
		Integer line = null;
		String taxpayer = null;
		String years = null;
		String companySocialNum = null;
		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			Row rowLine = sheet.getRow(i);
			if (rowLine == null) {
				continue;
			}
			for (int j = rowLine.getFirstCellNum(); j < rowLine.getLastCellNum(); j++) {
				if (rowLine.getCell(j) == null) {
					rowLine.getCell(j).setCellValue("");
					continue;
				}
				rowLine.getCell(j).setCellType(CellType.STRING);
			}

			String str = rowLine.getCell(0).getStringCellValue() == "" ? "" : rowLine.getCell(0).getStringCellValue();
			if (str.contains("纳税人编码")) {
				taxpayer = rowLine.getCell(1).getStringCellValue().trim();
			}
			if (str.contains("单位社保号")) {
				companySocialNum = rowLine.getCell(1).getStringCellValue();
				String year = rowLine.getCell(5).getStringCellValue() == "" ? ""
						: rowLine.getCell(5).getStringCellValue();
				years = year.substring(0, 4).trim() + "-" + year.substring(5).trim();
			}
			if ("姓名".equals(str)) {
				line = rowLine.getRowNum();
				break;
			}
		}
		return line + "," + taxpayer + "," + companySocialNum + "," + years;
	}

	/**
	 * 读取excel文件
	 * @param sheet
	 * @return Integer 开始行号
	 */
	public static String getSZSpecificRow(Sheet sheet) {
		Integer line = null;
		String number = null;
		String payMonth = null;
		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			Row rowLine = sheet.getRow(i);
			if (rowLine != null) {
				for (int j = rowLine.getFirstCellNum(); j < rowLine.getLastCellNum(); j++) {
					if (rowLine.getCell(j) == null) {
						rowLine.getCell(j).setCellValue("");
						continue;
					}
					rowLine.getCell(j).setCellType(CellType.STRING);
				}
				String years = rowLine.getCell(0).getStringCellValue() == null ? ""
						: rowLine.getCell(0).getStringCellValue();
				if (years.contains("台账年月") || years.contains("台帐年月")) {
					payMonth = years.replace("台账年月:", "").replace("台账年月：", "").replace("台帐年月：", "").replace("台帐年月：", "")
							.trim();
					payMonth = payMonth.replace("年", "-").replace("月", " ").trim();
					break;
				}
			} else {
				continue;
			}
		}
		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			Row rowLine = sheet.getRow(i);
			if (rowLine != null) {
				for (int j = rowLine.getFirstCellNum(); j < rowLine.getLastCellNum(); j++) {
					if (rowLine.getCell(j) == null) {
						rowLine.getCell(j).setCellValue("");
						continue;
					}
					rowLine.getCell(j).setCellType(CellType.STRING);
				}
				String numInfo = rowLine.getCell(0).getStringCellValue();
				String customerId = rowLine.getCell(1).getStringCellValue();
				String str = rowLine.getCell(2).getStringCellValue();
				String strNum = rowLine.getCell(3).getStringCellValue();

				if (numInfo.contains("单位编号")) {
					number = numInfo.replace("单位编号：", "").replace("单位编号:", "").trim();
				}
				if ("姓名".equals(str) && "个人编号".equals(customerId) && "身份证号".equals(strNum)) {
					line = rowLine.getRowNum();
					break;
				}
			} else {
				continue;
			}

		}
		// System.out.println(payMonth);
		return number + "," + line + "," + payMonth;
	}

	/**
	 * 判断供应商增员状态
	 */
	public static Boolean getUpdateAddResult(SupplierCostMapper supplierCostMapper, String city, Integer supplierId,
			String month, String certificateNumber) {
		Boolean flag = false;
		city = city.trim();
		certificateNumber = certificateNumber.trim();
		month = month.trim();
		String addEmployeeName = supplierCostMapper.selectEmployeeName(city, supplierId, month, certificateNumber);
		if (addEmployeeName != null && addEmployeeName != "") {
			flag = true;
		}
		return flag;
	}

	/**
	 * 判断供应商减员状态
	 */
	public static Boolean getUpdateMinusResult(SupplierCostMapper supplierCostMapper, String city, Integer supplierId,
			String month, String certificateNumber) {
		Boolean flag = false;
		city = city.trim();
		certificateNumber = certificateNumber.trim();
		month = month.trim();
		String minusEmployeeName = supplierCostMapper.selectEmployeeName(city, supplierId, month, certificateNumber);
		if (minusEmployeeName == null || minusEmployeeName == "") {
			flag = true;
		}
		return flag;
	}

	// double相加
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	// double相乘
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时， 精确到小数点以后10位，以后的数字四舍五入。 @param v1 被除数 @param v2
	 * 除数 @return 两个参数的商
	 */
	/*
	 * public static double div(double v1, double v2) {
	 * 
	 * return div(v1, v2,DEF_DIV_SCALE);
	 * 
	 * }
	 */

	/**
	 * 提供（相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。 @param v1 被除数 @param v2
	 * 除数 @param scale 表示表示需要精确到小数点以后几位。 @return 两个参数的商
	 */

	public static double div(double v1, double v2, int scale) {

		if (scale < 0) {

			throw new IllegalArgumentException("The scale must be a positive integer or zero");

		}

		BigDecimal b1 = new BigDecimal(Double.toString(v1));

		BigDecimal b2 = new BigDecimal(Double.toString(v2));

		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

	}

	/**
	 * 提供精确的小数位四舍五入处理。 @param v 需要四舍五入的数字 @param scale 小数点后保留几位 @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

}