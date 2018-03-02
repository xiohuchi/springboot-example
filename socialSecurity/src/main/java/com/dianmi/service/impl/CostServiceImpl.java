package com.dianmi.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.model.CostDg;
import com.dianmi.model.CostGz;
import com.dianmi.model.CostSz;
import com.dianmi.model.CostZh;
import com.dianmi.service.CostService;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.DownloadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;

/**
 * @Author:create by lzw
 * @Date:2017年12月5日 上午10:50:45
 * @Description:
 */
@Service
@SuppressWarnings("all")
public class CostServiceImpl extends CommonService implements CostService {

	/**
	 * @param yearMonth
	 * @return 月份费用
	 */
	public ResultJson cost(String yearMonth) {
		if (StringUtils.isEmpty(yearMonth))
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		List<Map<String, Object>> suppliers = supplierMapper.ziyouhuSupplier();
		List<Map<String, Object>> all = mergeList(suppliers, costList(yearMonth), "s_id");
		List<Map<String, Object>> zhangdanList = zhangdanList();
		List<Map<String, Object>> zhangdanAll = mergeList(all, zhangdanList, "s_id");
		return ResultUtil.success(RestEnum.SUCCESS, zhangdanAll);
	}

	/**
	 * @param yearMonth
	 * @return
	 */
	public List<Map<String, Object>> costList(String yearMonth) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> szCostList = costSzMapper.costGroupBySupplier(yearMonth);
		list.addAll(szCostList);
		List<Map<String, Object>> gzCostList = costGzMapper.costGroupBySupplier(yearMonth);
		list.addAll(gzCostList);
		List<Map<String, Object>> zhCostList = costZhMapper.costGroupBySupplier(yearMonth);
		list.addAll(zhCostList);
		List<Map<String, Object>> dgCostList = costDgMapper.costGroupBySupplier(yearMonth);
		list.addAll(dgCostList);
		return list;
	}

	/**
	 * @param principalList
	 * @param subordinateList
	 * @param middleman
	 * @return
	 */
	private List<Map<String, Object>> mergeList(List<Map<String, Object>> principalList,
			List<Map<String, Object>> subordinateList, String middleman) {
		List<Map<String, Object>> newList = new ArrayList<>();
		for (int i = 0; i < principalList.size(); i++) {
			Map<String, Object> principalMap = principalList.get(i);
			for (int j = 0; j < subordinateList.size(); j++) {
				Map<String, Object> subordinateMap = subordinateList.get(j);
				if (principalMap.get(middleman) == subordinateMap.get(middleman)) {
					principalMap.putAll(subordinateMap);
				}
			}
			newList.add(principalMap);
		}
		return newList;
	}

	/**
	 * @return
	 */
	public List<Map<String, Object>> zhangdanList() {
		List<Map<String, Object>> zhangdanList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list = zhangdanMapper.zhangdanBidui();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> zhangdanMap = new HashMap<String, Object>();
			Map<String, Object> map = list.get(i);
			double yuqiShebao = (double) map.get("yuqiShebaoHeji");
			double shijiShebao = (double) map.get("shijiShebaoHeji");
			double yuqiGongjijin = (double) map.get("yuqiGongjijinHeji");
			double shijiGongjijin = (double) map.get("shijiGongjijinHeji");
			double yuqiHeji = (double) map.get("yuqiHeji");
			double shijiHeji = (double) map.get("shijiHeji");
			zhangdanMap.put("s_id", map.get("supplier_id"));
            String reason = "";
			if (yuqiHeji == shijiHeji) {
				zhangdanMap.put("isMatch", 1);
			} else {
				if(yuqiShebao != shijiShebao){
					reason += "社保费用不匹配";
				}
				if(yuqiGongjijin != shijiGongjijin){
					reason += " 公积金费用不匹配";
				}
				zhangdanMap.put("isMatch", 0);
				zhangdanMap.put("reason", reason);
			}
			zhangdanList.add(zhangdanMap);
		}
		return zhangdanList;
	}

	/**
	 * @param yearMonth
	 * @param supplierId
	 *            导出社保和公积金费用
	 * @throws IOException
	 */
	public ResultJson exportCost(String yearMonth, Integer type, Integer supplierId) throws IOException {
		if (StringUtils.isEmpty(yearMonth) || null == type || null == supplierId) {
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		}
		// 深圳立德
		if (type == 1) {
			List<CostSz> costSzList = costSzMapper.selectCostBySupplier(yearMonth, supplierId);
			String filePath = createCostSzTemplate(costSzList);
			DownloadFile.downloadFile(filePath, "深圳立德费用明细" + yearMonth + ".xlsx");

			// 广州立德深圳分公司
		} else if (type == 2) {
			List<CostSz> costSzList = costSzMapper.selectCostBySupplier(yearMonth, supplierId);
			String filePath = createCostSzTemplate(costSzList);
			DownloadFile.downloadFile(filePath, "广州立德深圳分公司费用明细" + yearMonth + ".xlsx");
			DeleteFile.DeleteFolder(filePath);
			// 上海立德深圳分公司
		} else if (type == 3) {
			List<CostSz> costSzList = costSzMapper.selectCostBySupplier(yearMonth, supplierId);
			String filePath = createCostSzTemplate(costSzList);
			DownloadFile.downloadFile(filePath, "上海立德深圳分公司费用明细" + yearMonth + ".xlsx");
			DeleteFile.DeleteFolder(filePath);
			// 东莞
		} else if (type == 4) {
			List<CostDg> costDgList = costDgMapper.selectCostBySupplier(yearMonth, supplierId);
			String filePath = createCostDgTemplate(costDgList);
			DownloadFile.downloadFile(filePath, "东莞费用明细" + yearMonth + ".xlsx");
			DeleteFile.DeleteFolder(filePath);
			// 珠海
		} else if (type == 5) {
			List<CostZh> costZhList = costZhMapper.selectCostBySupplier(yearMonth, supplierId);
			String filePath = createCostZhTemplate(costZhList);
			DownloadFile.downloadFile(filePath, "珠海费用明细" + yearMonth + ".xlsx");
			DeleteFile.DeleteFolder(filePath);
			// 广州
		} else if (type == 6) {
			List<CostGz> costGzList = costGzMapper.selectCostBySupplier(yearMonth, supplierId);
			String filePath = createCostGzTemplate(costGzList);
			DownloadFile.downloadFile(filePath, "广州费用明细" + yearMonth + ".xlsx");
			DeleteFile.DeleteFolder(filePath);
		}
		return ResultUtil.success(RestEnum.SUCCESS);
	}

	/**
	 * @param list
	 * @return 创建东莞社保公积金费用模板
	 */
	public String createCostDgTemplate(List<CostDg> list) {
		// 创建excel工作簿
		SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(), 100);
		// 创建第一个sheet（页），命名
		Sheet sheet = wb.createSheet("东莞费用单");
		String[] title = new String[] { "客户名称", "员工姓名", "身份证号码", "养老基数", "公司", "个人", "公司补缴", "个人补缴", "补缴利息", "滞纳金",
				"失业基数", "公司", "个人", "公司补缴", "个人补缴", "补缴利息", "滞纳金", "工伤基数", "公司", "个人", "公司补缴", "个人补缴", "补缴利息", "滞纳金",
				"生育基数", "公司", "个人", "公司补缴", "个人补缴", "补缴利息", "滞纳金", "医疗基数", "公司", "个人", "公司补缴", "个人补缴", "补缴利息", "滞纳金",
				"大病医疗基数", "公司", "个人", "公司补缴", "个人补缴", "补缴利息", "滞纳金", "社保合计", "公积金基数", "旧基数", "旧缴费比例", "个人比例", "公司比例",
				"公司缴费", "个人缴费", "合计", "公积金账号", "公司", "负责人" };
		Row titleRow = sheet.createRow((short) 0);
		// 创建样式
		CellStyle style = wb.createCellStyle();
		// 设置背景色
		style.setFillForegroundColor(IndexedColors.TAN.getIndex());
		// 设置背景图案
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		for (int i = 0; i < title.length; i++) {
			Cell cell = titleRow.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}
		// 设置字体大小
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		style.setFont(font);
		for (int j = 0; j < list.size(); j++) {
			CostDg costDg = list.get(j);
			Row row = sheet.createRow((short) j + 1);
			row.createCell(0).setCellValue(costDg.getCustomerName());// 客户名称
			row.createCell(1).setCellValue(costDg.getName());// 员工姓名
			row.createCell(2).setCellValue(costDg.getCertificateNumber());// 身份证号码

			row.createCell(3).setCellValue(costDg.getPensionRadix());// 养老基数
			row.createCell(4).setCellValue(costDg.getPensionCompanyPay());// 养老公司缴费
			row.createCell(5).setCellValue(costDg.getPensionPersonPay());// 养老个人缴费
			row.createCell(6).setCellValue(costDg.getPensionCompanySupplementPay());// 养老公司补缴
			row.createCell(7).setCellValue(costDg.getPensionCompanyPersonPay());// 养老个人补缴
			row.createCell(8).setCellValue(costDg.getPensionCompanyPay());// 养老补缴利息
			row.createCell(9).setCellValue(costDg.getPensionLateFees());// 养老滞纳金

			row.createCell(10).setCellValue(costDg.getUnemploymentRadix());// 失业
			row.createCell(11).setCellValue(costDg.getUnemploymentCompanyPay());
			row.createCell(12).setCellValue(costDg.getUnemploymentPersonPay());
			row.createCell(13).setCellValue(costDg.getUnemploymentCompanySupplementPay());
			row.createCell(14).setCellValue(costDg.getUnemploymentCompanyPersonPay());
			row.createCell(15).setCellValue(costDg.getUnemploymentSupplementInterest());
			row.createCell(16).setCellValue(costDg.getUnemploymentLateFees());

			row.createCell(17).setCellValue(costDg.getInjuryRadix());// 工伤
			row.createCell(18).setCellValue(costDg.getInjuryCompanyPay());
			row.createCell(19).setCellValue(costDg.getInjuryPersonPay());
			row.createCell(20).setCellValue(costDg.getInjuryCompanySupplementPay());
			row.createCell(21).setCellValue(costDg.getInjuryCompanyPersonPay());
			row.createCell(22).setCellValue(costDg.getInjurySupplementInterest());
			row.createCell(23).setCellValue(costDg.getInjuryLateFees());

			row.createCell(24).setCellValue(costDg.getProcreateRadix());// 生育
			row.createCell(25).setCellValue(costDg.getProcreateCompanyPay());
			row.createCell(26).setCellValue(costDg.getProcreatePersonPay());
			row.createCell(27).setCellValue(costDg.getProcreateCompanySupplementPay());
			row.createCell(28).setCellValue(costDg.getProcreateCompanyPersonPay());
			row.createCell(29).setCellValue(costDg.getProcreateSupplementInterest());
			row.createCell(30).setCellValue(costDg.getProcreateLateFees());

			row.createCell(31).setCellValue(costDg.getMedicalTreatmentRadix());// 基本医疗
			row.createCell(32).setCellValue(costDg.getMedicalTreatmentCompanyPay());
			row.createCell(33).setCellValue(costDg.getMedicalTreatmentPersonPay());
			row.createCell(34).setCellValue(costDg.getMedicalTreatmentCompanySupplementPay());
			row.createCell(35).setCellValue(costDg.getMedicalTreatmentCompanyPersonPay());
			row.createCell(36).setCellValue(costDg.getMedicalTreatmentSupplementInterest());
			row.createCell(37).setCellValue(costDg.getMedicalTreatmentLateFees());

			row.createCell(38).setCellValue(costDg.getSeriousIllnessTreatmentRadix());// 大病医疗
			row.createCell(39).setCellValue(costDg.getSeriousIllnessTreatmentCompanyPay());
			row.createCell(40).setCellValue(costDg.getSeriousIllnessTreatmentPersonPay());
			row.createCell(41).setCellValue(costDg.getSeriousIllnessTreatmentCompanySupplementPay());
			row.createCell(42).setCellValue(costDg.getSeriousIllnessTreatmentCompanyPersonPay());
			row.createCell(43).setCellValue(costDg.getSeriousIllnessTreatmentSupplementInterest());
			row.createCell(44).setCellValue(costDg.getSeriousIllnessTreatmentLateFees());

			row.createCell(45).setCellValue(costDg.getSocialSecurityTotal());

			row.createCell(46).setCellValue(costDg.getAccumulationFundRadix());// 公积金缴存基数
			row.createCell(47).setCellValue(costDg.getAccumulationFundOldRadix());// 公积金旧基数
			row.createCell(48).setCellValue(costDg.getAccumulationFundOldRatio());// 旧缴费比例
			row.createCell(49).setCellValue(costDg.getAccumulationFundPersonRatio());// 个人比例
			row.createCell(50).setCellValue(costDg.getAccumulationFundCompanyRatio());// 公司比例
			row.createCell(51).setCellValue(costDg.getAccumulationFundCompanyPay());// 公积金公司缴费
			row.createCell(52).setCellValue(costDg.getAccumulationFundPersonPay());// 公积金个人缴费
			row.createCell(53).setCellValue(costDg.getAccumulationFundTotal());// 公积金合计
			row.createCell(54).setCellValue(costDg.getAccumulationFundAccount());// 公积金账号

			row.createCell(55).setCellValue(costDg.getCompany());// 公司
			row.createCell(56).setCellValue(costDg.getManager());// 负责人
		}
		String filePath = realPath() + "download" + File.separator + "excel" + File.separator
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
	 * @param list
	 * @return 创建广州社保公积金费用模板
	 */
	public String createCostGzTemplate(List<CostGz> list) {
		// 创建excel工作簿
		SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(), 100);
		// 创建第一个sheet（页），命名
		Sheet sheet = wb.createSheet("广州费用单");
		String[] title = new String[] { "客户名称", "姓名", "身份证号码", "证件名称", "社保医保号", "缴费月份", "养老缴纳基数", "企业缴费", "个人缴费",
				"失业缴纳基数", "企业", "个人", "工伤缴费基数", "企业", "个人", "生育缴费基数", "企业", "个人", "医疗缴费基数", "企业", "个人", "大病医疗缴费基数",
				"企业", "个人", "社保公司部分合计", "社保个人部分合计", "社保合计", "公积金基数", "公司缴存比例", "公司缴费", "个人缴存比例", "个人缴费", "公积金月缴存额",
				"系统中个人编号", "公司", "负责人" };
		Row titleRow = sheet.createRow((short) 0);
		// 创建样式
		CellStyle style = wb.createCellStyle();
		// 设置背景色
		style.setFillForegroundColor(IndexedColors.TAN.getIndex());
		// 设置背景图案
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		for (int i = 0; i < title.length; i++) {
			Cell cell = titleRow.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}
		// 设置字体大小
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		style.setFont(font);
		for (int j = 0; j < list.size(); j++) {
			CostGz costGz = list.get(j);
			Row row = sheet.createRow((short) j + 1);

			row.createCell(0).setCellValue(costGz.getCustomerName());
			row.createCell(1).setCellValue(costGz.getName());
			row.createCell(2).setCellValue(costGz.getCertificateNumber());
			row.createCell(3).setCellValue(costGz.getCertificateName());
			row.createCell(4).setCellValue(costGz.getSocialSecurityNumber());
			row.createCell(5).setCellValue(costGz.getPaymentMonth());

			row.createCell(6).setCellValue(costGz.getPensionRadix());
			row.createCell(7).setCellValue(costGz.getPensionCompanyPay());
			row.createCell(8).setCellValue(costGz.getPensionPersonPay());

			row.createCell(9).setCellValue(costGz.getUnemploymentRadix());
			row.createCell(10).setCellValue(costGz.getUnemploymentCompanyPay());
			row.createCell(11).setCellValue(costGz.getUnemploymentPersonPay());

			row.createCell(12).setCellValue(costGz.getInjuryCompanyRadix());
			row.createCell(13).setCellValue(costGz.getInjuryCompanyPay());
			row.createCell(14).setCellValue(costGz.getInjuryPersonPay());

			row.createCell(15).setCellValue(costGz.getProcreateCompanyRadix());
			row.createCell(16).setCellValue(costGz.getProcreateCompanyPay());
			row.createCell(17).setCellValue(costGz.getProcreatePersonPay());

			row.createCell(18).setCellValue(costGz.getMedicalTreatmentRadix());
			row.createCell(19).setCellValue(costGz.getMedicalTreatmentCompanyPay());
			row.createCell(20).setCellValue(costGz.getMedicalTreatmentPersonPay());

			row.createCell(21).setCellValue(costGz.getSeriousIllnessRadix());
			row.createCell(22).setCellValue(costGz.getSeriousIllnessCompanyPay());
			row.createCell(23).setCellValue(costGz.getSeriousIllnessPersonPay());

			row.createCell(24).setCellValue(costGz.getCompanyTotal());
			row.createCell(25).setCellValue(costGz.getPersonTotal());
			row.createCell(26).setCellValue(costGz.getSocialSecurityTotal());

			row.createCell(27).setCellValue(costGz.getAccumulationFundRadix());
			row.createCell(28).setCellValue(costGz.getAccumulationFundCompanyRatio());
			row.createCell(29).setCellValue(costGz.getAccumulationFundCompanyPay());
			row.createCell(30).setCellValue(costGz.getAccumulationFundPersonRatio());
			row.createCell(31).setCellValue(costGz.getAccumulationFundPersonPay());
			row.createCell(32).setCellValue(costGz.getAccumulationFundTotal());

			row.createCell(33).setCellValue(costGz.getSystemPersonNumber());
			row.createCell(34).setCellValue(costGz.getCompany());
			row.createCell(35).setCellValue(costGz.getManager());

		}
		String filePath = realPath() + "download" + File.separator + "excel" + File.separator
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
	 * @param list
	 * @return 创建深圳社保公积金费用模板
	 */
	public String createCostSzTemplate(List<CostSz> list) {
		// 创建excel工作簿
		SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(), 100);
		// 创建第一个sheet（页），命名
		Sheet sheet = wb.createSheet("深圳费用单");
		String[] title = new String[] { "客户名称", "缴费月份", "个人编号", "姓名", "客户身份证号", "养老保险缴纳基数", "企业", "个人", "失业保险缴纳基数",
				"企业", "个人", "工伤保险企业缴费基数", "企业", "生育保险企业缴费基数", "企业", "医疗缴费基数", "企业", "个人", "社保公司部分", "社保个人部分", "社保合计",
				"单位编号", "公积金账号", "公积金基数", "公司比例", "个人比例", "缴费总计", "缴费月份", "公积金客户名称" };
		Row titleRow = sheet.createRow((short) 0);
		// 创建样式
		CellStyle style = wb.createCellStyle();
		// 设置背景色
		style.setFillForegroundColor(IndexedColors.TAN.getIndex());
		// 设置背景图案
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		for (int i = 0; i < title.length; i++) {
			Cell cell = titleRow.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}
		// 设置字体大小
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		style.setFont(font);
		for (int j = 0; j < list.size(); j++) {
			CostSz costSz = list.get(j);
			Row row = sheet.createRow((short) j + 1);
			row.createCell(0).setCellValue(costSz.getCustomerName());
			row.createCell(1).setCellValue(costSz.getPaymentMonth());
			row.createCell(2).setCellValue(costSz.getPersonNumber());
			row.createCell(3).setCellValue(costSz.getName());
			row.createCell(4).setCellValue(costSz.getCertificateNumber());

			row.createCell(5).setCellValue(costSz.getPensionRadix());
			row.createCell(6).setCellValue(costSz.getPensionCompanyPay());
			row.createCell(7).setCellValue(costSz.getPensionPersonPay());

			row.createCell(8).setCellValue(costSz.getUnemploymentRadix());
			row.createCell(9).setCellValue(costSz.getUnemploymentPersonPay());
			row.createCell(10).setCellValue(costSz.getUnemploymentCompanyPay());

			row.createCell(11).setCellValue(costSz.getInjuryCompanyRadix());
			row.createCell(12).setCellValue(costSz.getInjuryCompanyPay());

			row.createCell(13).setCellValue(costSz.getProcreateCompanyRadix());
			row.createCell(14).setCellValue(costSz.getProcreateCompanyPay());

			row.createCell(15).setCellValue(costSz.getMedicalTreatmentRadix());
			row.createCell(16).setCellValue(costSz.getMedicalTreatmentCompanyPay());
			row.createCell(17).setCellValue(costSz.getMedicalTreatmentPersonPay());

			row.createCell(18).setCellValue(costSz.getCompanyTotal());
			row.createCell(19).setCellValue(costSz.getPersonTotal());
			row.createCell(20).setCellValue(costSz.getSocialSecurityTotal());
			row.createCell(21).setCellValue(costSz.getCompanyNum());

			row.createCell(22).setCellValue(costSz.getAccumulationFundAccount());
			row.createCell(23).setCellValue(costSz.getAccumulationFundRadix());
			row.createCell(24).setCellValue(costSz.getAccumulationFundCompanyRatio());
			row.createCell(25).setCellValue(costSz.getAccumulationFundPersonRatio());
			row.createCell(26).setCellValue(costSz.getAccumulationFundTotal());
			row.createCell(27).setCellValue(costSz.getAccumulationFundPaymentMonth());
		}
		String filePath = realPath() + "download" + File.separator + "excel" + File.separator
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
	 * @param list
	 * @return 创建广州社保公积金费用模板
	 */
	public String createCostZhTemplate(List<CostZh> list) {
		// 创建excel工作簿
		SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(), 100);
		// 创建第一个sheet（页），命名
		Sheet sheet = wb.createSheet("珠海费用单");
		String[] title = new String[] { "客户名称", "客户姓名", "客户身份证号", "证件名称", "社保/医保号", "缴费月份", "养老保险缴纳基数", "公司", "个人",
				"失业保险缴纳基数", "公司", "个人", "工伤保险缴纳基数", "公司", "个人", "生育保险缴纳基数", "公司", "个人", "医疗保险缴纳基数", "公司", "个人",
				"大病医疗保险缴纳基数", "公司", "个人", "社保公司部分合计", "社保个人部分合计", "社保合计", "公积金基数", "缴费比例", "公司", "个人", "合计", "工资始扣月",
				"始参月份", "公积金账号", "公司", "负责人" };
		Row titleRow = sheet.createRow((short) 0);
		// 创建样式
		CellStyle style = wb.createCellStyle();
		// 设置背景色
		style.setFillForegroundColor(IndexedColors.TAN.getIndex());
		// 设置背景图案
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		for (int i = 0; i < title.length; i++) {
			Cell cell = titleRow.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}
		// 设置字体大小
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		style.setFont(font);
		for (int j = 0; j < list.size(); j++) {
			CostZh costZh = list.get(j);
			Row row = sheet.createRow((short) j + 1);
			row.createCell(0).setCellValue(costZh.getCustomerName());
			row.createCell(1).setCellValue(costZh.getName());
			row.createCell(2).setCellValue(costZh.getCertificateNumber());
			row.createCell(3).setCellValue(costZh.getCertificateName());
			row.createCell(4).setCellValue(costZh.getSocialSecurityNumber());
			row.createCell(5).setCellValue(costZh.getPaymentMonth());

			row.createCell(6).setCellValue(costZh.getPensionRadix());
			row.createCell(7).setCellValue(costZh.getPensionCompanyPay());
			row.createCell(8).setCellValue(costZh.getPensionPersonPay());

			row.createCell(9).setCellValue(costZh.getUnemploymentRadix());
			row.createCell(10).setCellValue(costZh.getUnemploymentCompanyPay());
			row.createCell(11).setCellValue(costZh.getUnemploymentPersonPay());

			row.createCell(12).setCellValue(costZh.getInjuryCompanyRadix());
			row.createCell(13).setCellValue(costZh.getInjuryCompanyPay());
			row.createCell(14).setCellValue(costZh.getInjuryPersonPay());

			row.createCell(15).setCellValue(costZh.getProcreateCompanyRadix());
			row.createCell(16).setCellValue(costZh.getProcreateCompanyPay());
			row.createCell(17).setCellValue(costZh.getProcreatePersonPay());

			row.createCell(18).setCellValue(costZh.getMedicalTreatmentRadix());
			row.createCell(19).setCellValue(costZh.getMedicalTreatmentCompanyPay());
			row.createCell(20).setCellValue(costZh.getMedicalTreatmentPersonPay());

			row.createCell(21).setCellValue(costZh.getSeriousIllnessRedix());
			row.createCell(22).setCellValue(costZh.getSeriousIllnessCompanyPay());
			row.createCell(23).setCellValue(costZh.getSeriousIllnessPersonPay());

			row.createCell(24).setCellValue(costZh.getCompanyTotal());
			row.createCell(25).setCellValue(costZh.getPersonTotal());
			row.createCell(26).setCellValue(costZh.getSocialSecurityTotal());

			row.createCell(27).setCellValue(costZh.getAccumulationFundRadix());
			row.createCell(28).setCellValue(costZh.getAccumulationFundRatio());
			row.createCell(29).setCellValue(costZh.getAccumulationFundCompanyPay());
			row.createCell(30).setCellValue(costZh.getAccumulationFundPersonPay());
			row.createCell(31).setCellValue(costZh.getAccumulationFundTotal());
			row.createCell(32).setCellValue(costZh.getSalaryBeginPaymentMonth());
			row.createCell(33).setCellValue(costZh.getBeginInjoyMonth());
			row.createCell(34).setCellValue(costZh.getAccumulationFundAccount());

			row.createCell(35).setCellValue(costZh.getCompany());
			row.createCell(36).setCellValue(costZh.getManager());

		}
		String filePath = realPath() + "download" + File.separator + "excel" + File.separator
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
}