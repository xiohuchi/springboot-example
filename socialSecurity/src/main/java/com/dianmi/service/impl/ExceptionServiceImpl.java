package com.dianmi.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.mapper.JianyuanYichangMapper;
import com.dianmi.mapper.ZengyuanYichangMapper;
import com.dianmi.model.JianyuanYichang;
import com.dianmi.model.ZengyuanYichang;
import com.dianmi.service.ExceptionService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * @author zhiwei loong
 * @Date 2017年11月23日 下午4:31:07
 * @Description
 */
@Service
@SuppressWarnings("all")
public class ExceptionServiceImpl implements ExceptionService {

	@Autowired
	private ZengyuanYichangMapper zengyuanYichangMapper;
	@Autowired
	private JianyuanYichangMapper jianyuanYichangMapper;

	/**
	 * @param yearMonth
	 * @param userId
	 * @param type
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	@PostMapping("/all")
	public ResultJson allException(String yearMonth, Integer userId, Integer type, Integer currPage, Integer pageSize,
			String importDate) {
		if (StringUtils.isEmpty(yearMonth) || null == userId || null == type || null == currPage || null == pageSize) {
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		}
		PageHelper.startPage(currPage, pageSize);
		PageInfo<?> pageInfo = null;
		if (null == importDate || importDate.equals(importDate)) {
			importDate = null;
		}
		if (1 == type) {
			List<ZengyuanYichang> list = zengyuanYichangMapper.selectAll(yearMonth, userId, importDate);
			pageInfo = new PageInfo<>(list);
		} else if (2 == type) {
			List<JianyuanYichang> list = jianyuanYichangMapper.selectAll(yearMonth, userId, importDate);
			pageInfo = new PageInfo<>(list);
		}
		return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
	}

	/**
	 * 导出增减员异常文件
	 */
	public String createExceptionFile(String yearMonth, int userId, String importDate) {
		List<ZengyuanYichang> zengyuanYichangList = zengyuanYichangMapper.selectAll(yearMonth, userId, importDate);
		List<JianyuanYichang> jinayuanYichangList = jianyuanYichangMapper.selectAll(yearMonth, userId, importDate);
		// 创建excel工作簿
		SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(), 100);
		// 创建样式
		CellStyle style = wb.createCellStyle();
		// 设置背景色
		style.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
		// 设置背景图案
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setVerticalAlignment(VerticalAlignment.CENTER); // 居中
		style.setBorderBottom(BorderStyle.THIN); // 下边框
		style.setBorderLeft(BorderStyle.THIN);// 左边框
		style.setBorderTop(BorderStyle.THIN);// 上边框
		style.setBorderRight(BorderStyle.THIN);// 右边框
		// 创建第一个sheet（页），命名
		Sheet zengyuanSheet = wb.createSheet("增员");
		zengyuanSheet.setColumnHidden((short) 22, true);
		Row zyDescRow = zengyuanSheet.createRow((short) 0);
		zyDescRow.setHeight((short) (30 * 10));
		zyDescRow.setHeightInPoints(30);
		String[] zyDescArr = new String[] { "写明客户到帐所属财务（广州点米、深圳点米、上海外包等）", "客户到帐财务显示名称", "全名", "身份证、护照", "身份证号码，文本格式",
				"不填则默认汉族", "参保人手机号", "供应商是否可以直接联系员工（是、否）", "户籍城市", "本地城镇/本地农业/外埠城镇/外埠农业", "参保城市",
				"针对于需提供社保、医保号用来接续参保的城市", "数字或“最低基数”", "参保类型（深圳必填医疗档次）其他城市不填默认统一", "供应商名称（是否有指定供应商，以及参保地为深圳必须要填写，查看批注）",
				"YYYY-MM", "针对于需提供公积金账号用来接续参保的城市", "数字或“最低基数”", "公司+个人（%）", "YYYY-MM", "特殊情况说明", "产生异常的原因" };
		for (int zyDescIndex = 0; zyDescIndex < zyDescArr.length; zyDescIndex++) {
			Cell zyDescCell = zyDescRow.createCell(zyDescIndex);
			zyDescCell.setCellValue(zyDescArr[zyDescIndex]);
			zyDescCell.setCellStyle(style);
		}
		Row zyTitleRow = zengyuanSheet.createRow((short) 1);
		String[] titleArr = new String[] { "财务核算单位", "客户名称", "员工姓名", "证件类型", "证件号码", "民族", "手机", "是否外呼", "户籍城市", "户口性质",
				"参保城市", "社保/医保号", "社保基数", "参保类型", "供应商", "社保起缴年月", "公积金账号", "公积金基数", "公积金比例", "公积金起缴年月", "备注", "异常原因",
				"校验码" };
		for (int zyTitleIndex = 0; zyTitleIndex < titleArr.length; zyTitleIndex++) {
			Cell zyTitleCell = zyTitleRow.createCell(zyTitleIndex);
			zyTitleCell.setCellValue(titleArr[zyTitleIndex]);
			zyTitleCell.setCellStyle(style);
		}
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		style.setFont(font);
		for (int i = 0; i < zengyuanYichangList.size(); i++) {
			ZengyuanYichang zengyuanYichang = zengyuanYichangList.get(i);
			Row row = zengyuanSheet.createRow((short) i + 2);
			row.createCell(0).setCellValue(zengyuanYichang.getFinancialAccountingUnit());
			row.createCell(1).setCellValue(zengyuanYichang.getCustomerName());
			row.createCell(2).setCellValue(zengyuanYichang.getEmployeeName());
			row.createCell(3).setCellValue(zengyuanYichang.getCertificateType());
			row.createCell(4).setCellValue(zengyuanYichang.getCertificateNumber());
			row.createCell(5).setCellValue(zengyuanYichang.getNation());
			row.createCell(6).setCellValue(zengyuanYichang.getMobilePhone());
			row.createCell(7).setCellValue(zengyuanYichang.getIsExternalCall());
			row.createCell(8).setCellValue(zengyuanYichang.getHouseholdCity());
			row.createCell(9).setCellValue(zengyuanYichang.getHouseholdProperty());
			row.createCell(10).setCellValue(zengyuanYichang.getCity());
			row.createCell(11).setCellValue(zengyuanYichang.getSocialSecurityCardNumber());
			row.createCell(12).setCellValue(zengyuanYichang.getSocialSecurityBase());
			row.createCell(13).setCellValue(zengyuanYichang.getSocialSecurityType());
			row.createCell(14).setCellValue(zengyuanYichang.getSupplier());
			row.createCell(15).setCellValue(zengyuanYichang.getSocialSecurityBeginTime());
			row.createCell(16).setCellValue(zengyuanYichang.getAccumulationFundNumber());
			row.createCell(17).setCellValue(zengyuanYichang.getAccumulationFundCardinalNumber());
			row.createCell(18).setCellValue(zengyuanYichang.getAccumulationFundRatio());
			row.createCell(19).setCellValue(zengyuanYichang.getAccumulationFundBeginTime());
			row.createCell(20).setCellValue(zengyuanYichang.getRemark());
			row.createCell(21).setCellValue(zengyuanYichang.getYichangReason());
			row.createCell(22).setCellValue(zengyuanYichang.getCheckCode());
		}
		// 创建第二个sheet（页），命名
		Sheet jyExceptionSheet = wb.createSheet("减员");
		jyExceptionSheet.setColumnHidden((short) 12, true);
		Row jyDescRow = jyExceptionSheet.createRow((short) 0);
		jyDescRow.setHeight((short) (25 * 10));
		jyDescRow.setHeightInPoints(25);
		String[] jyDescArr = new String[] { "客户到帐财务显示名称", "全名", "身份证、护照", "文本格式", "城市名", "辞职、辞退、合同到期等", "YYYY-MM-DD",
				"YYYY-MM-DD（填写不产生费用月份）", "YYYY-MM（填写不产生费用月份）", "不清楚可为空", "特殊情况说明", "产生异常的原因" };
		for (int jyDescIndex = 0; jyDescIndex < jyDescArr.length; jyDescIndex++) {
			Cell jyDescCell = jyDescRow.createCell(jyDescIndex);
			jyDescCell.setCellValue(jyDescArr[jyDescIndex]);
			jyDescCell.setCellStyle(style);
		}
		Row jyTitleRow = jyExceptionSheet.createRow((short) 1);
		String[] jianyuanTitleArr = new String[] { "客户名称", "员工姓名", "证件类型", "证件号码", "参保城市", "离职原因", "离职日期", "社保停缴年月",
				"公积金停缴年月", "供应商", "备注", "异常原因", "校验码" };
		style = wb.createCellStyle();
		for (int jyIndex = 0; jyIndex < jianyuanTitleArr.length; jyIndex++) {
			Cell jyTitleCell = jyTitleRow.createCell(jyIndex);
			jyTitleCell.setCellValue(jianyuanTitleArr[jyIndex]);
			jyTitleCell.setCellStyle(style);
		}
		for (int j = 0; j < jinayuanYichangList.size(); j++) {
			JianyuanYichang jianyuanYichang = jinayuanYichangList.get(j);
			Row row = jyExceptionSheet.createRow((short) j + 2);
			row.createCell(0).setCellValue(jianyuanYichang.getClientName());
			row.createCell(1).setCellValue(jianyuanYichang.getEmployeeName());
			row.createCell(2).setCellValue(jianyuanYichang.getCertificateType());
			row.createCell(3).setCellValue(jianyuanYichang.getCertificateNumber());
			row.createCell(4).setCellValue(jianyuanYichang.getCity());
			row.createCell(5).setCellValue(jianyuanYichang.getDimissionReason());
			row.createCell(6).setCellValue(jianyuanYichang.getDimissionDate());
			row.createCell(7).setCellValue(jianyuanYichang.getSocialSecurityStopDate());
			row.createCell(8).setCellValue(jianyuanYichang.getAccumulationFundStopDate());
			row.createCell(9).setCellValue(jianyuanYichang.getSupplier());
			row.createCell(10).setCellValue(jianyuanYichang.getRemark());
			row.createCell(11).setCellValue(jianyuanYichang.getYichangReason());
			row.createCell(12).setCellValue(jianyuanYichang.getCheckCode());
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String realPath = request.getSession().getServletContext().getRealPath("/");
		String filePath = realPath + "download" + File.separator + "excel" + File.separator
				+ UUID.randomUUID().toString() + ".xlsx";
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(filePath);
			wb.write(fileOut);
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}

	/**
	 * @param id
	 * @return
	 */
	public ResultJson deleteException(Integer type, Integer id) {
		if (null == type || null == id) {
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		}
		int result = 0;
		if (1 == type) {
			result = zengyuanYichangMapper.deleteByPrimaryKey(id);
		} else if (2 == type) {
			result = jianyuanYichangMapper.deleteByPrimaryKey(id);
		}
		return ResultUtil.success(RestEnum.SUCCESS, result);
	}

	/**
	 * @param id
	 * @return
	 */
	public ResultJson batchDeleteException(Integer type, String ids) {
		if (null == type || StringUtils.isEmpty(ids)) {
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		}
		int result = 0;
		ids = ids.replace("，", ",");
		String[] str = ids.split(",");
		if (1 == type) {
			for (int i = 0; i < str.length; i++) {
				int id = Integer.parseInt(str[i]);
				result += zengyuanYichangMapper.deleteByPrimaryKey(id);
			}
		} else if (2 == type) {
			for (int i = 0; i < str.length; i++) {
				int id = Integer.parseInt(str[i]);
				result += jianyuanYichangMapper.deleteByPrimaryKey(id);
			}
		}
		return ResultUtil.success(RestEnum.SUCCESS, result);
	}

	/**
	 * @param type
	 * @return
	 */
	@PostMapping("/importDate")
	public ResultJson importDate(String yearMonth,Integer type, Integer userId) {
		if (StringUtils.isEmpty(yearMonth) || null == type) {
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		}
		List<Map<String, Object>> map = null;
		if (1 == type) {
			map = zengyuanYichangMapper.allImportDateTime(yearMonth,userId);
		} else if (2 == type) {
			map = jianyuanYichangMapper.allImportDateTime(yearMonth,userId);
		}
		return ResultUtil.success(RestEnum.SUCCESS, map);
	}
}