package com.dianmi.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.common.OwnHomeUtils;
import com.dianmi.mapper.CostDgMapper;
import com.dianmi.model.CostDg;
import com.dianmi.model.Jianyuan;
import com.dianmi.model.Zaice;
import com.dianmi.model.Zengyuan;
import com.dianmi.model.ZhangdanMingxi;
import com.dianmi.model.accumulation.DGAccumulation;
import com.dianmi.model.owndetailshow.DGDetailShow;
import com.dianmi.model.ownhome.DGPerson;
import com.dianmi.model.ownhome.SZLDDGShow;
import com.dianmi.model.po.ZaiceCustomerMsg;
import com.dianmi.service.CostDgService;
import com.dianmi.utils.MathArithmetic;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.UploadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.dianmi.utils.poi.ReadExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;

@Service
@SuppressWarnings("all")
public class CostDgServiceImpl extends CommonService implements CostDgService {

	/**
	 * @param file
	 * @param userId
	 * @param yearMonth
	 * @return 导入社保费用
	 */
	public ResultJson importCost(MultipartFile file, Integer userId, Integer supplierId, String yearMonth) {
		if (file == null || file.isEmpty())
			return ResultUtil.error(RestEnum.FILE_NOT_EXISTS);
		String fileName = file.getOriginalFilename();
		if (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))
			return ResultUtil.error(RestEnum.FILE_FORMATS_ERROR);
		if (null == userId || StringUtils.isEmpty(yearMonth))
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		String filePath = UploadFile.uploadFile(file);
		List<String[]> dataList = ReadExcel.readIrregularExcel2List(filePath).get(0);
		DeleteFile.DeleteFolder(filePath);
		if (dataList.isEmpty())
			return ResultUtil.error(RestEnum.FILE_DATA_IS_NULL);
		int result = 0;
		int startLine = getSocialSecurityStartLine(dataList);
		if (!isDongguanSocialSecurityTemplate(dataList.get(startLine)))
			return ResultUtil.error(RestEnum.FAILD, "请上传东莞社保模板");
		List<DGPerson> list = new ArrayList<>();
		for (int i = startLine + 1; i < dataList.size(); i++) {
			List<String> strList = Arrays.asList(dataList.get(i));
			String certificateNumber = strList.get(1).replaceAll("x", "X").trim(); // 身份证号码
			String name = strList.get(2); // 姓名
			// String month = StrList.get(3);//月份
			String securityType = strList.get(4); // 参保险种
			Double payRadix = Double.parseDouble(strList.get(5)); // 缴纳基数
			Double companyPay = Double.parseDouble(strList.get(6)); // 单位缴交
			Double personPay = Double.parseDouble(strList.get(7)); // 个人缴交
			Double companySupplementPay = Double.parseDouble(strList.get(8)); // 单位补缴
			Double personSupplementPay = Double.parseDouble(strList.get(9)); // 个人补缴
			Double supplementInterest = Double.parseDouble(strList.get(10)); // 补缴利息
			Double treatmentLateFees = Double.parseDouble(strList.get(11)); // 滞纳金
			Double total = Double.parseDouble(strList.get(12)); // 合计
			ZaiceCustomerMsg customerMsg = zaiceMsg(yearMonth, DONGGUAN, certificateNumber);
			list.add(new DGPerson(userId, customerMsg.getCustomerId(), supplierId, customerMsg.getCustomerName(),
					certificateNumber, name, yearMonth, securityType, payRadix, companyPay, personPay,
					companySupplementPay, personSupplementPay, supplementInterest, treatmentLateFees, total));
		}
		Map<String, CostDg> dgmap = getDGHomes(list);
		List<String> allCertificateNumberList = new ArrayList<String>();// 所有读取的身份证号码集合
		for (Entry<String, CostDg> dghomes : dgmap.entrySet()) {
			String key = dghomes.getKey();
			CostDg costDg = dgmap.get(key);
			String certificateNumber = costDg.getCertificateNumber();// 身份證號
			allCertificateNumberList.add(certificateNumber);
			// 根据月份、城市、身份证号码从在册表中获取在册信息
			ZaiceCustomerMsg customerMsg = zaiceMsg(yearMonth, DONGGUAN, costDg.getCertificateNumber());
			costDg.setCustomerId(customerMsg.getCustomerId());
			costDg.setSupplierId(supplierId);
			costDg.setCustomerName(customerMsg.getCustomerName());
			costDg.setAccumulationFundCompanyPay(0.0);
			costDg.setAccumulationFundPersonPay(0.0);
			costDg.setAccumulationFundTotal(0.0);
			// 判断费用是否已经导入
			if (isCostImport(yearMonth, certificateNumber)) {
				// 更新费用信息
				if (costDgMapper.updateCostDg(costDg) > 0)
					result += 1;
			} else {
				// 新增费用信息
				if (costDgMapper.insertSelective(costDg) > 0)
					result += 1;
			}
			// 更新账单明细表
			zhangdanService.updateZhangdanByZaiceId(getZhangdanMingxi(customerMsg.getZaiceId(), costDg));
			// 确定增员成功
			zengyuanService.updateAddStatus(certificateNumber, DONGGUAN, yearMonth);
			zaiceService.updateAddSocialStatus(yearMonth, DONGGUAN, certificateNumber);
		}
		// 供应商当月所有增员用户信息
		List<String> allZengyuanCertificateNumber = zengyuanService.getAllCertificateNumber(yearMonth, supplierId);
		// 增员失败的用户信息
		List<String> newZengyuanList = new ArrayList<String>(allZengyuanCertificateNumber);
		newZengyuanList.removeAll(allCertificateNumberList);
		updateToZengyuanFailed(newZengyuanList, yearMonth, DONGGUAN);
		List<String> allJianyuanCertificateNumber = jianyuanService.getAllCertificateNumber(yearMonth, supplierId);
		List<String> jianyuanFaildList = new ArrayList<String>(allJianyuanCertificateNumber);
		jianyuanFaildList.removeAll(newZengyuanList);
		updateToJianyuanFailed(jianyuanFaildList, yearMonth, DONGGUAN);
		List<String> jianyuanSuccessList = new ArrayList<String>(allJianyuanCertificateNumber);
		jianyuanSuccessList.removeAll(jianyuanFaildList);
		updateToJianyuanSuccess(jianyuanSuccessList, yearMonth, DONGGUAN);
		return ResultUtil.success(RestEnum.SUCCESS, "处理：" + result + "条");
	}

	/**
	 * @param strArr
	 * @return 判断是否是东莞社保缴费明细模板
	 */
	private boolean isDongguanSocialSecurityTemplate(String[] strArr) {
		if (strArr[0].contains("序号") && strArr[1].contains("身份证号码") && strArr[2].contains("姓名")
				&& strArr[3].contains("所属月份") && strArr[4].contains("参保险种") && strArr[5].contains("缴费基数")
				&& strArr[6].contains("单位缴交") && strArr[7].contains("个人缴交") && strArr[8].contains("单位补缴")
				&& strArr[9].contains("个人补缴") && strArr[10].contains("补交利息") && strArr[11].contains("滞纳金")
				&& strArr[12].contains("合计")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param list
	 * @return
	 */
	public int getSocialSecurityStartLine(List<String[]> list) {
		int startLine = 0;// 缴费数据起始行
		for (int i = 0; i < list.size(); i++) {
			String[] strArr = list.get(i);
			if (strArr.length > 4)
				if (!StringUtils.isEmpty(strArr[1]) && !StringUtils.isEmpty(strArr[2])
						&& !StringUtils.isEmpty(strArr[3]) && !StringUtils.isEmpty(strArr[4]))
					if (strArr[1].equals("身份证号码") && strArr[2].equals("姓名") && strArr[3].equals("所属月份")
							&& strArr[4].equals("参保险种"))
						return startLine = i;
		}
		return startLine;
	}

	/**
	 * @param yearMonth
	 * @param certificateNumber
	 * @return 賬單是否已經錄入費用表
	 */
	public boolean isCostImport(String yearMonth, String certificateNumber) {
		if (costDgMapper.isCostImport(yearMonth, certificateNumber).size() > 0)
			return true;
		else
			return false;
	}

	@Transactional
	public List readDGSocialInfo1(String filePath, Integer userId, Integer supplierId, String yearMonth)
			throws FileNotFoundException, IOException, EncryptedDocumentException, InvalidFormatException {
		String CITY = "东莞";
		Workbook book = WorkbookFactory.create(new FileInputStream(new File(filePath)));
		List<DGPerson> list = new ArrayList<DGPerson>();
		for (int y = 0; y < book.getNumberOfSheets();) {
			Sheet sheet = book.getSheetAt(y);
			if (sheet == null) {
				continue;
			}
			String[] strs = { "序号", "	身份证号码", "姓名", "所属月份", "参保险种", "缴费基数", "单位缴交", "个人缴交", "单位补缴", "个人补缴", "补交利息",
					"滞纳金", "合计" };
			Row rowsy = sheet.getRow(0);
			for (int i = 0; i < strs.length; i++) {
				rowsy.getCell(i).setCellType(CellType.STRING);
				if (!strs[i].equals(rowsy.getCell(i).getStringCellValue())) {
					break;
				}
			}
			y++;
			int rowNum = sheet.getLastRowNum();
			for (int i = 1; i <= rowNum; i++) {
				Row row = sheet.getRow(i);
				if (row == null) {
					continue;
				}

				for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
					row.getCell(j).setCellType(CellType.STRING);
					if (row.getCell(j) == null) {
						row.getCell(j).setCellValue("");
						continue;
					}
				}
				// Integer cdId = (int) row.getCell(0).getStringCellValue();
				String certificateNumber = row.getCell(1).getStringCellValue().trim(); // 身份证号码
				String name = row.getCell(2).getStringCellValue().trim(); // 姓名
				if (name == "" && certificateNumber == "") {
					break;
				}
				String reportingPeriod = row.getCell(3).getStringCellValue().trim(); // 所属月份
				String securityType = row.getCell(4).getStringCellValue().trim(); // 参保类型
				Double payRadix = row.getCell(5).getStringCellValue() == "" ? 0.00
						: Double.valueOf(row.getCell(5).getStringCellValue().trim()); // 缴纳基数
				Double companyPay = row.getCell(6).getStringCellValue() == "" ? 0.00
						: Double.valueOf(row.getCell(6).getStringCellValue().trim()); // 单位缴交
				Double personPay = row.getCell(7).getStringCellValue() == "" ? 0.00
						: Double.valueOf(row.getCell(7).getStringCellValue().trim()); // 个人缴交
				Double companySupplementPay = row.getCell(8).getStringCellValue() == "" ? 0.00
						: Double.valueOf(row.getCell(8).getStringCellValue().trim()); // 单位补缴
				Double personSupplementPay = row.getCell(9).getStringCellValue() == "" ? 0.00
						: Double.valueOf(row.getCell(9).getStringCellValue().trim()); // 个人补缴
				Double supplementInterest = row.getCell(10).getStringCellValue() == "" ? 0.00
						: Double.valueOf(row.getCell(10).getStringCellValue().trim()); // 补缴利息
				Double treatmentLateFees = row.getCell(11).getStringCellValue() == "" ? 0.00
						: Double.valueOf(row.getCell(11).getStringCellValue().trim()); // 滞纳金
				Double total = row.getCell(12).getStringCellValue() == "" ? 0.00
						: Double.valueOf(row.getCell(12).getStringCellValue().trim()); // 合计
				Map<String, Object> zaiceMsg = zaiceService.selectZaiceMsg(CITY, yearMonth, certificateNumber);
				int customerId = (int) zaiceMsg.get("customer_id");
				DGPerson dgPerson = new DGPerson();
				dgPerson.setUserId(userId);
				dgPerson.setCustomerId(customerId);
				dgPerson.setCertificateNumber(certificateNumber);
				dgPerson.setName(name);
				dgPerson.setReportingPeriod(yearMonth);
				dgPerson.setSecurityType(securityType);
				dgPerson.setPayRadix(payRadix);
				dgPerson.setCompanyPay(companyPay);
				dgPerson.setPersonPay(personPay);
				dgPerson.setCompanySupplementPay(companySupplementPay);
				dgPerson.setPensonSupplementPay(personSupplementPay);
				dgPerson.setSupplementInterest(supplementInterest);
				dgPerson.setPayLateFees(treatmentLateFees);
				dgPerson.setTotal(total);
				if (certificateNumber != "" && name != "") {
					list.add(dgPerson);
				}
			}

		}
		DeleteFile.DeleteFolder(filePath); // 上传成功删除Excel模板

		Map<String, CostDg> dgmap = getDGHomes(list);
		List<CostDg> dglist = new ArrayList<CostDg>();
		Integer count = 0;
		for (Entry<String, CostDg> dghomes : dgmap.entrySet()) {
			String key = dghomes.getKey();
			CostDg dghome = dgmap.get(key);

			dglist.add(dghome);
			costDgMapper.insert(dghome);
			Map<String, Object> zaiceMsg = zaiceService.selectZaiceMsg(CITY, dghome.getReportingPeriod(),
					dghome.getCertificateNumber());
			int zaiceId = (int) zaiceMsg.get("zc_id");
			ZhangdanMingxi supplierDetail = getZhangdanMingxi(zaiceId, dghome);
			zhangdanService.updateZhangdanByZaiceId(supplierDetail);
		}
		List<Zengyuan> zengyuanList = zengyuanService.selectZengyuanAll();
		List<Jianyuan> jianyuanList = jianyuanService.selectJianyuanAll();
		for (Zengyuan zengyuan : zengyuanList) {
			String certificateNumber = zengyuan.getCertificateNumber();
			String city = zengyuan.getCity();
			String month = zengyuan.getReportingPeriod();
			Integer supplierIds = zengyuan.getSupplierId();
			if (city == null || certificateNumber == null || month == null || supplierIds == null
					|| !"东莞".equals(city)) {
				continue;
			}
			if (getUpdateDGAddResult(costDgMapper, city, supplierIds, month, certificateNumber)) {
				count++;
				zengyuanService.updateAddStatus(certificateNumber, city, month);
				zaiceService.updateAddSocialStatus(city, month, certificateNumber);
			} else {
				zengyuanService.updateAddSocialStatus(certificateNumber, month, city);
			}
		}
		for (Jianyuan jianyuan : jianyuanList) {
			String certificateNumber = jianyuan.getCertificateNumber();
			String city = jianyuan.getCity();
			String month = jianyuan.getReportingPeriod();
			Integer supplierIds = zaiceService.getSupplierIdBySupplier(city, jianyuan.getSupplier(), certificateNumber);
			if (city == null || certificateNumber == null || month == null || supplierIds == null
					|| !"东莞".equals(city)) {
				continue;
			}
			if (getUpdateDGMinusResult(costDgMapper, city, supplierIds, month, certificateNumber)) {
				jianyuanService.updateMinusSocialStatus(certificateNumber, city, month);
				zaiceService.updateAddSocialStatus(city, month, certificateNumber);
			} else {
				jianyuanService.updateMinusStatus(certificateNumber, month, city);
			}
		}
		return dglist;
	}

	// 东莞
	private Boolean getUpdateDGAddResult(CostDgMapper costDgMapper, String city, Integer supplierId, String month,
			String certificateNumber) {
		Boolean flag = false;
		city = city.trim();
		certificateNumber = certificateNumber.trim();
		month = month.trim();
		String addEmployeeName = costDgMapper.selectEmployeeName(supplierId, month, certificateNumber);
		if (addEmployeeName != null && addEmployeeName != "") {
			flag = true;
		}
		return flag;
	}

	private Boolean getUpdateDGMinusResult(CostDgMapper costDgMapper, String city, Integer supplierId, String month,
			String certificateNumber) {
		Boolean flag = false;
		city = city.trim();
		certificateNumber = certificateNumber.trim();
		month = month.trim();
		String minusEmployeeName = costDgMapper.selectEmployeeName(supplierId, month, certificateNumber);
		if (minusEmployeeName == null) {
			flag = true;
		}
		return flag;
	}

	// 将东莞对象封装到map中
	private Map<String, CostDg> getDGHomes(List<DGPerson> list) {
		Map<String, CostDg> map = new HashMap<String, CostDg>();
		for (DGPerson dgPerson : list) {
			String keys = dgPerson.getCertificateNumber() + dgPerson.getName() + dgPerson.getReportingPeriod();
			if (map.containsKey(keys)) {
				settingDghomeInfo(map, dgPerson);
			} else {
				CostDg dgHome = setsDghomeInfo(dgPerson);
				map.put(keys, dgHome);
			}
		}
		return map;
	}

	/**
	 * 条件为true时设置DGHome对象
	 */
	private void settingDghomeInfo(Map<String, CostDg> map, DGPerson dgPerson) {
		setDghomeInfo(map, dgPerson);
	}

	/**
	 * 条件为false时设置DGHome对象
	 */
	private CostDg setsDghomeInfo(DGPerson dgPerson) {
		String keys = dgPerson.getCertificateNumber() + dgPerson.getName() + dgPerson.getReportingPeriod();
		CostDg dgHome = new CostDg();
		Map<String, CostDg> map = new HashMap<String, CostDg>();
		// dgHome.setCdId(dgPerson.getCdId);
		dgHome.setUserId(dgPerson.getUserId());
		dgHome.setCustomerId(dgPerson.getCustomerId());
		dgHome.setCustomerName(dgPerson.getCustomerName());
		dgHome.setCertificateNumber(dgPerson.getCertificateNumber());
		dgHome.setName(dgPerson.getName());
		dgHome.setReportingPeriod(dgPerson.getReportingPeriod());

		map.put(keys, dgHome);
		setDghomeInfo(map, dgPerson);
		return dgHome;
	}

	// 设置DGHome对象的属性
	private void setDghomeInfo(Map<String, CostDg> map, DGPerson dgPerson) {
		String keys = dgPerson.getCertificateNumber() + dgPerson.getName() + dgPerson.getReportingPeriod();
		String securityType = dgPerson.getSecurityType();
		Double dgTotal = dgPerson.getTotal() == null ? 0.00 : dgPerson.getTotal();
		Double mapTotal = map.get(keys).getSocialSecurityTotal() == null ? 0.00
				: map.get(keys).getSocialSecurityTotal();
		Double total = MathArithmetic.add(dgTotal, mapTotal);
		// Double companyFee = dgPerson.getCompanyPay();
		// Double CsupplementPay = dgPerson.getCompanySupplementPay();
		// Double supplementInterest = dgPerson.getSupplementInterest();
		// Double payLateFees = dgPerson.getPayLateFees();
		// Double personFee = dgPerson.getPersonPay();
		// Double PpensonSupplement = dgPerson.getPensonSupplementPay();
		map.get(keys).setCustomerId(dgPerson.getCustomerId());
		map.get(keys).setSupplierId(dgPerson.getSupplierId());
		map.get(keys).setCustomerName(dgPerson.getCustomerName());
		if (securityType.contains("失业保险")) {
			map.get(keys).setUnemploymentRadix(dgPerson.getPayRadix());
			map.get(keys).setUnemploymentCompanyPay(dgPerson.getCompanyPay());
			map.get(keys).setUnemploymentPersonPay(dgPerson.getPersonPay());
			map.get(keys).setUnemploymentCompanyPersonPay(dgPerson.getPensonSupplementPay());
			map.get(keys).setUnemploymentCompanySupplementPay(dgPerson.getCompanySupplementPay());
			map.get(keys).setUnemploymentSupplementInterest(dgPerson.getSupplementInterest());
			map.get(keys).setUnemploymentLateFees(dgPerson.getPayLateFees());
			map.get(keys).setSocialSecurityTotal(total);
		} else if (securityType.contains("养老保险")) {
			map.get(keys).setPensionRadix(dgPerson.getPayRadix());
			map.get(keys).setPensionCompanyPay(dgPerson.getCompanyPay());
			map.get(keys).setPensionPersonPay(dgPerson.getPersonPay());
			map.get(keys).setPensionCompanySupplementPay(dgPerson.getCompanySupplementPay());
			map.get(keys).setPensionCompanyPersonPay(dgPerson.getPensonSupplementPay());
			map.get(keys).setPensionSupplementInterest(dgPerson.getSupplementInterest());
			map.get(keys).setPensionLateFees(dgPerson.getPayLateFees());
			map.get(keys).setSocialSecurityTotal(total);
		} else if (securityType.contains("生育保险")) {
			map.get(keys).setProcreateRadix(dgPerson.getPayRadix());
			map.get(keys).setProcreateCompanyPay(dgPerson.getCompanyPay());
			map.get(keys).setProcreatePersonPay(dgPerson.getPersonPay());
			map.get(keys).setProcreateCompanySupplementPay(dgPerson.getCompanySupplementPay());
			map.get(keys).setProcreateCompanyPersonPay(dgPerson.getPensonSupplementPay());
			map.get(keys).setProcreateSupplementInterest(dgPerson.getSupplementInterest());
			map.get(keys).setProcreateLateFees(dgPerson.getPayLateFees());
			map.get(keys).setSocialSecurityTotal(total);
		} else if (securityType.contains("工伤保险")) {
			map.get(keys).setInjuryRadix(dgPerson.getPayRadix());
			map.get(keys).setInjuryCompanyPay(dgPerson.getCompanyPay());
			map.get(keys).setInjuryPersonPay(dgPerson.getPersonPay());
			map.get(keys).setInjuryCompanySupplementPay(dgPerson.getCompanySupplementPay());
			map.get(keys).setInjuryCompanyPersonPay(dgPerson.getPensonSupplementPay());
			map.get(keys).setInjurySupplementInterest(dgPerson.getSupplementInterest());
			map.get(keys).setInjuryLateFees(dgPerson.getPayLateFees());
			map.get(keys).setSocialSecurityTotal(total);
		} else if (securityType.contains("门诊基本医疗保险")) {
			map.get(keys).setMedicalTreatmentRadix(dgPerson.getPayRadix());
			map.get(keys).setMedicalTreatmentCompanyPay(dgPerson.getCompanyPay());
			map.get(keys).setMedicalTreatmentPersonPay(dgPerson.getPersonPay());
			map.get(keys).setMedicalTreatmentCompanySupplementPay(dgPerson.getCompanySupplementPay());
			map.get(keys).setMedicalTreatmentCompanyPersonPay(dgPerson.getPensonSupplementPay());
			map.get(keys).setMedicalTreatmentSupplementInterest(dgPerson.getSupplementInterest());
			map.get(keys).setMedicalTreatmentLateFees(dgPerson.getPayLateFees());
			map.get(keys).setSocialSecurityTotal(total);
		} else if (securityType.contains("住院基本医疗保险")) {
			map.get(keys).setSeriousIllnessTreatmentRadix(dgPerson.getPayRadix());
			map.get(keys).setSeriousIllnessTreatmentCompanyPay(dgPerson.getCompanyPay());
			map.get(keys).setSeriousIllnessTreatmentPersonPay(dgPerson.getPersonPay());
			map.get(keys).setSeriousIllnessTreatmentCompanySupplementPay(dgPerson.getCompanySupplementPay());
			map.get(keys).setSeriousIllnessTreatmentCompanyPersonPay(dgPerson.getPensonSupplementPay());
			map.get(keys).setSeriousIllnessTreatmentSupplementInterest(dgPerson.getSupplementInterest());
			map.get(keys).setSeriousIllnessTreatmentLateFees(dgPerson.getPayLateFees());
			map.get(keys).setSocialSecurityTotal(total);
		}
	}

	// 获取分页对象
	private List<SZLDDGShow> getSGShow(List<CostDg> list) {
		List<SZLDDGShow> showList = new ArrayList<SZLDDGShow>();
		for (CostDg dgHome : list) {
			SZLDDGShow shows = new SZLDDGShow();
			Map<String, String> strs = customerService.findCustomerInfo(dgHome.getCustomerId());
			shows.setReportingPeriod((dgHome.getReportingPeriod() == null ? "" : dgHome.getReportingPeriod()));
			String customerName = null;
			String dept = null;
			if (strs == null) {
				customerName = "数据为空！";
				dept = "数据为空！";
			} else {
				customerName = strs.get("customerName");
				dept = strs.get("deptName");
			}
			shows.setCustomerName(customerName);
			shows.setDept(dept);
			Double Ppension = dgHome.getPensionPersonPay();
			Double PpensionSupplement = dgHome.getPensionCompanyPersonPay();
			Double Punemployment = dgHome.getUnemploymentPersonPay();
			Double PunemploymentSupplement = dgHome.getUnemploymentCompanyPersonPay();
			Double Pinjury = dgHome.getInjuryPersonPay();
			Double PinjurySupplement = dgHome.getInjuryCompanyPersonPay();
			Double Pprocreate = dgHome.getProcreatePersonPay();
			Double PprocreateSupplement = dgHome.getProcreateCompanyPersonPay();
			Double PmedicalTreatment = dgHome.getMedicalTreatmentPersonPay();
			Double PmedicalTreatmentSupplement = dgHome.getMedicalTreatmentCompanyPersonPay();
			Double PseriousIllness = dgHome.getSeriousIllnessTreatmentPersonPay();
			Double PseriousIllnessSupplement = dgHome.getSeriousIllnessTreatmentCompanyPersonPay();

			Double Cpension = dgHome.getPensionCompanyPay();
			Double CpensionSupplement = dgHome.getPensionCompanySupplementPay();
			Double CpensionSupplementInterest = dgHome.getPensionSupplementInterest();
			Double CpensionLateFees = dgHome.getPensionLateFees();

			Double Cunemployment = dgHome.getUnemploymentCompanyPay();
			Double CunemploymentSupplement = dgHome.getUnemploymentCompanySupplementPay();
			Double CunemploymentSupplementInterest = dgHome.getUnemploymentSupplementInterest();
			Double CunemploymentLateFees = dgHome.getUnemploymentLateFees();

			Double Cinjury = dgHome.getInjuryCompanyPay();
			Double CinjurySupplement = dgHome.getInjuryCompanySupplementPay();
			Double CinjurySupplementInterest = dgHome.getInjurySupplementInterest();
			Double CinjuryLateFees = dgHome.getInjuryLateFees();

			Double Cprocreate = dgHome.getProcreateCompanyPay();
			Double CprocreateSupplement = dgHome.getProcreateCompanySupplementPay();
			Double CprocreateSupplementInterest = dgHome.getProcreateSupplementInterest();
			Double CprocreateLateFees = dgHome.getProcreateLateFees();

			Double Cmedical = dgHome.getMedicalTreatmentCompanyPay();
			Double CmedicalSupplement = dgHome.getMedicalTreatmentCompanySupplementPay();
			Double CmedicalSupplementInterest = dgHome.getMedicalTreatmentSupplementInterest();
			Double CmedicalLateFees = dgHome.getMedicalTreatmentLateFees();

			Double CseriousIllness = dgHome.getSeriousIllnessTreatmentCompanyPay();
			Double CseriousIllnessSupplement = dgHome.getSeriousIllnessTreatmentCompanySupplementPay();
			Double CseriousIllnessSupplementInterest = dgHome.getSeriousIllnessTreatmentSupplementInterest();
			Double CseriousIllnessLateFees = dgHome.getSeriousIllnessTreatmentLateFees();

			Double aa = OwnHomeUtils.add(Ppension, PpensionSupplement);
			Double bb = OwnHomeUtils.add(Cpension, CpensionSupplement);
			Double cc = OwnHomeUtils.add(CpensionSupplementInterest, CpensionLateFees);
			Double pensionFee = OwnHomeUtils.add(OwnHomeUtils.add(aa, bb), cc);
			shows.setPensionSocial(pensionFee);

			Double aaa = OwnHomeUtils.add(PmedicalTreatment, PmedicalTreatmentSupplement);
			Double bbb = OwnHomeUtils.add(Cmedical, CmedicalSupplement);
			Double ccc = OwnHomeUtils.add(CmedicalSupplementInterest, CmedicalLateFees);
			Double medicalFee = OwnHomeUtils.add(OwnHomeUtils.add(aaa, bbb), ccc);
			shows.setMedicaltreatmentSocial(medicalFee);

			Double aaaa = OwnHomeUtils.add(PseriousIllness, PseriousIllnessSupplement);
			Double bbbb = OwnHomeUtils.add(CseriousIllness, CseriousIllnessSupplement);
			Double cccc = OwnHomeUtils.add(CseriousIllnessSupplementInterest, CseriousIllnessLateFees);
			Double seriousIllnessFee = OwnHomeUtils.add(OwnHomeUtils.add(aaaa, bbbb), cccc);
			shows.setSeriousIllnessSocial(seriousIllnessFee);

			Double a = OwnHomeUtils.add(Pinjury, PinjurySupplement);
			Double b = OwnHomeUtils.add(Cinjury, CinjurySupplement);
			Double c = OwnHomeUtils.add(CinjurySupplementInterest, CinjuryLateFees);
			Double injuryFee = OwnHomeUtils.add(OwnHomeUtils.add(a, b), c);
			shows.setInjurySocial(injuryFee);

			Double d = OwnHomeUtils.add(Punemployment, PunemploymentSupplement);
			Double e = OwnHomeUtils.add(Cunemployment, CunemploymentSupplement);
			Double f = OwnHomeUtils.add(CunemploymentSupplementInterest, CunemploymentLateFees);
			Double unemploymentFee = OwnHomeUtils.add(OwnHomeUtils.add(d, e), f);
			shows.setUnemploymentSocial(unemploymentFee);

			Double h = OwnHomeUtils.add(Pprocreate, PprocreateSupplement);
			Double k = OwnHomeUtils.add(Cprocreate, CprocreateSupplement);
			Double t = OwnHomeUtils.add(CprocreateSupplementInterest, CprocreateLateFees);
			Double procreateFee = OwnHomeUtils.add(OwnHomeUtils.add(h, k), t);
			shows.setProcreateSocial(procreateFee);

			Double disabilityBenefit = dgHome.getDisabilityBenefit();
			Double serviceFee = dgHome.getServiceCharge();
			Double Caccumulation = dgHome.getAccumulationFundCompanyPay();
			Double Paccumulation = dgHome.getAccumulationFundPersonPay();
			shows.setDisabilityBenefitFee(disabilityBenefit);
			shows.setCompanyAccumulation(Caccumulation);
			shows.setPersonAccumulation(Paccumulation);
			Double accumulationTotalFee = OwnHomeUtils.add(Caccumulation, Paccumulation);
			shows.setAccumulationTotalPay(accumulationTotalFee);
			shows.setServiceFee(serviceFee);
			showList.add(shows);
		}
		return showList;
	}

	/**
	 * 根据月份,供应商id查询所有的数据 东莞页面展示
	 */
	public List selectDGhomeAll(Integer supplierId, String month, Integer currPage, Integer pageSize) {
		List<CostDg> list = costDgMapper.selectCostDgAll(supplierId, month, (currPage - 1), pageSize);
		if (list == null) {
			return new ArrayList();
		}
		PageHelper.startPage(currPage, pageSize);
		List<SZLDDGShow> showList = getSGShow(list);

		PageInfo<SZLDDGShow> pageList = new PageInfo<SZLDDGShow>(showList);
		return pageList.getList();
	}

	// 东莞详情展示
	public List selectDGInfoAll(Integer supplierId, String yearMonth, Integer currPage, Integer pageSize) {
		List<CostDg> list = costDgMapper.selectCostDgAll(supplierId, yearMonth, (currPage - 1), pageSize);
		if (list == null) {
			return new ArrayList();
		}
		PageHelper.startPage(currPage, pageSize);
		List<DGDetailShow> dgDetailList = getDGShow(list);
		if (dgDetailList == null) {
			return new ArrayList();
		}
		PageInfo<DGDetailShow> pageList = new PageInfo<DGDetailShow>(dgDetailList);
		return pageList.getList();
	}

	// 东莞详情对象
	private List<DGDetailShow> getDGShow(List<CostDg> list) {
		String city = "东莞";
		List<DGDetailShow> dgDetailList = new ArrayList<DGDetailShow>();
		for (CostDg dghome : list) {
			Zaice zaice = zaiceService.findAccumulationInfos(city, dghome.getReportingPeriod(),
					dghome.getCertificateNumber());
			if (zaice == null) {
				zaice = new Zaice();
			}
			DGDetailShow show = new DGDetailShow();
			show.setName(dghome.getName());
			show.setCertificateNumber(dghome.getCertificateNumber());

			show.setPensionRadix(dghome.getPensionRadix());
			show.setPensionCompanyPay(dghome.getPensionCompanyPay());
			show.setPensionPersonPay(dghome.getPensionPersonPay());
			show.setPensionCompanySupplementPay(dghome.getPensionCompanySupplementPay());
			show.setPensionCompanyPersonPay(dghome.getPensionCompanyPersonPay());
			show.setPensionSupplementInterest(dghome.getPensionSupplementInterest());
			show.setPensionLateFees(dghome.getPensionLateFees());

			Double Cpension = dghome.getPensionCompanyPay();
			Double CpensionSupplement = dghome.getPensionCompanySupplementPay();
			Double CpensionSupplementInterest = dghome.getPensionSupplementInterest();
			Double CpensionLateFees = dghome.getPensionLateFees();
			Double Ppension = dghome.getPensionPersonPay();
			Double PpensionSupplement = dghome.getPensionCompanyPersonPay();
			Double aa = OwnHomeUtils.add(Ppension, PpensionSupplement);
			Double bb = OwnHomeUtils.add(Cpension, CpensionSupplement);
			Double cc = OwnHomeUtils.add(CpensionSupplementInterest, CpensionLateFees);
			Double pensionSocialTotal = OwnHomeUtils.add(OwnHomeUtils.add(aa, bb), cc);
			show.setPensionSocialTotal(pensionSocialTotal);

			show.setUnemploymentRadix(dghome.getUnemploymentRadix());
			show.setUnemploymentCompanyPay(dghome.getUnemploymentCompanyPay());
			show.setUnemploymentPersonPay(dghome.getUnemploymentPersonPay());
			show.setUnemploymentCompanySupplementPay(dghome.getUnemploymentCompanySupplementPay());
			show.setUnemploymentCompanyPersonPay(dghome.getUnemploymentCompanyPersonPay());
			show.setUnemploymentSupplementInterest(dghome.getUnemploymentSupplementInterest());
			show.setUnemploymentLateFees(dghome.getUnemploymentLateFees());

			Double Cunemployment = dghome.getUnemploymentCompanyPay();
			Double CunemploymentSupplement = dghome.getUnemploymentCompanySupplementPay();
			Double CunemploymentSupplementInterest = dghome.getUnemploymentSupplementInterest();
			Double CunemploymentLateFees = dghome.getUnemploymentLateFees();
			Double Punemployment = dghome.getUnemploymentPersonPay();
			Double PunemploymentSupplement = dghome.getUnemploymentCompanyPersonPay();
			Double d = OwnHomeUtils.add(Punemployment, PunemploymentSupplement);
			Double e = OwnHomeUtils.add(Cunemployment, CunemploymentSupplement);
			Double f = OwnHomeUtils.add(CunemploymentSupplementInterest, CunemploymentLateFees);
			Double unemploymentTotal = OwnHomeUtils.add(OwnHomeUtils.add(d, e), f);
			show.setUnemploymentSocialTotal(unemploymentTotal);

			show.setInjuryRadix(dghome.getInjuryRadix());
			show.setInjuryCompanyPay(dghome.getInjuryCompanyPay());
			show.setInjuryPersonPay(dghome.getInjuryPersonPay());
			show.setInjuryCompanySupplementPay(dghome.getInjuryCompanySupplementPay());
			show.setInjuryCompanyPersonPay(dghome.getInjuryCompanyPersonPay());
			show.setInjurySupplementInterest(dghome.getInjurySupplementInterest());
			show.setInjuryLateFees(dghome.getInjuryLateFees());

			Double Pinjury = dghome.getInjuryPersonPay();
			Double PinjurySupplement = dghome.getInjuryCompanyPersonPay();
			Double CinjurySupplementInterest = dghome.getInjurySupplementInterest();
			Double CinjuryLateFees = dghome.getInjuryLateFees();
			Double CinjurySupplement = dghome.getInjuryCompanySupplementPay();
			Double Cinjury = dghome.getInjuryCompanyPay();
			Double a = OwnHomeUtils.add(Pinjury, PinjurySupplement);
			Double b = OwnHomeUtils.add(Cinjury, CinjurySupplement);
			Double c = OwnHomeUtils.add(CinjurySupplementInterest, CinjuryLateFees);
			Double injuryTotal = OwnHomeUtils.add(OwnHomeUtils.add(a, b), c);
			show.setInjurySocialTotal(injuryTotal);

			show.setProcreateRadix(dghome.getProcreateRadix());
			show.setProcreateCompanyPay(dghome.getProcreateCompanyPay());
			show.setProcreatePersonPay(dghome.getProcreatePersonPay());
			show.setProcreateCompanySupplementPay(dghome.getProcreateCompanySupplementPay());
			show.setProcreateCompanyPersonPay(dghome.getProcreateCompanyPersonPay());
			show.setProcreateSupplementInterest(dghome.getProcreateSupplementInterest());
			show.setProcreateLateFees(dghome.getProcreateLateFees());

			Double Pprocreate = dghome.getProcreatePersonPay();
			Double PprocreateSupplement = dghome.getProcreateCompanyPersonPay();
			Double Cprocreate = dghome.getProcreateCompanyPay();
			Double CprocreateSupplement = dghome.getProcreateCompanySupplementPay();
			Double CprocreateSupplementInterest = dghome.getProcreateSupplementInterest();
			Double CprocreateLateFees = dghome.getProcreateLateFees();
			Double h = OwnHomeUtils.add(Pprocreate, PprocreateSupplement);
			Double k = OwnHomeUtils.add(Cprocreate, CprocreateSupplement);
			Double t = OwnHomeUtils.add(CprocreateSupplementInterest, CprocreateLateFees);
			Double procreateTotal = OwnHomeUtils.add(OwnHomeUtils.add(h, k), t);
			show.setProcreateSocialTotal(procreateTotal);

			show.setSeriousIllnessTreatmentRadix(dghome.getSeriousIllnessTreatmentRadix());
			show.setSeriousIllnessTreatmentCompanyPay(dghome.getSeriousIllnessTreatmentCompanyPay());
			show.setSeriousIllnessTreatmentPersonPay(dghome.getSeriousIllnessTreatmentPersonPay());
			show.setSeriousIllnessTreatmentCompanySupplementPay(
					dghome.getSeriousIllnessTreatmentCompanySupplementPay());
			show.setSeriousIllnessTreatmentCompanyPersonPay(dghome.getSeriousIllnessTreatmentCompanyPersonPay());
			show.setSeriousIllnessTreatmentSupplementInterest(dghome.getSeriousIllnessTreatmentSupplementInterest());
			show.setSeriousIllnessTreatmentLateFees(dghome.getSeriousIllnessTreatmentLateFees());

			Double PseriousIllness = dghome.getSeriousIllnessTreatmentPersonPay();
			Double PseriousIllnessSupplement = dghome.getSeriousIllnessTreatmentCompanyPersonPay();
			Double CseriousIllness = dghome.getSeriousIllnessTreatmentCompanyPay();
			Double CseriousIllnessSupplement = dghome.getSeriousIllnessTreatmentCompanySupplementPay();
			Double CseriousIllnessSupplementInterest = dghome.getSeriousIllnessTreatmentSupplementInterest();
			Double CseriousIllnessLateFees = dghome.getSeriousIllnessTreatmentLateFees();
			Double aaaa = OwnHomeUtils.add(PseriousIllness, PseriousIllnessSupplement);
			Double bbbb = OwnHomeUtils.add(CseriousIllness, CseriousIllnessSupplement);
			Double cccc = OwnHomeUtils.add(CseriousIllnessSupplementInterest, CseriousIllnessLateFees);
			Double seriousIllnessTotal = OwnHomeUtils.add(OwnHomeUtils.add(aaaa, bbbb), cccc);
			show.setSeriousIllnessSocialTotal(seriousIllnessTotal);

			show.setMedicalTreatmentRadix(dghome.getMedicalTreatmentRadix());
			show.setMedicalTreatmentCompanyPay(dghome.getMedicalTreatmentCompanyPay());
			show.setMedicalTreatmentPersonPay(dghome.getMedicalTreatmentPersonPay());
			show.setMedicalTreatmentCompanySupplementPay(dghome.getMedicalTreatmentCompanySupplementPay());
			show.setMedicalTreatmentCompanyPersonPay(dghome.getMedicalTreatmentCompanyPersonPay());
			show.setMedicalTreatmentSupplementInterest(dghome.getMedicalTreatmentSupplementInterest());
			show.setMedicalTreatmentLateFees(dghome.getMedicalTreatmentLateFees());

			Double Pmedical = dghome.getMedicalTreatmentPersonPay();
			Double PmedicalSupplement = dghome.getMedicalTreatmentCompanyPersonPay();
			Double Cmedical = dghome.getMedicalTreatmentCompanyPay();
			Double CmedicalSupplement = dghome.getMedicalTreatmentCompanySupplementPay();
			Double CmedicalSupplementInterest = dghome.getMedicalTreatmentSupplementInterest();
			Double CmedicalLateFees = dghome.getMedicalTreatmentLateFees();
			Double aaa = OwnHomeUtils.add(Pmedical, PmedicalSupplement);
			Double bbb = OwnHomeUtils.add(Cmedical, CmedicalSupplement);
			Double ccc = OwnHomeUtils.add(CmedicalSupplementInterest, CmedicalLateFees);
			Double medicalTreatmentTotal = OwnHomeUtils.add(OwnHomeUtils.add(aaa, bbb), ccc);
			show.setMedicalTreatmentSocialTotal(medicalTreatmentTotal);

			Double disabilityBenefit = dghome.getDisabilityBenefit();
			Double serviceFee = dghome.getServiceCharge();
			Double Paccumulation = dghome.getAccumulationFundPersonPay();
			Double Caccumulation = dghome.getAccumulationFundCompanyPay();

			String accumulationRadix = zaice.getAccumulationFundCardinalNumber();
			if (accumulationRadix == null) {
				accumulationRadix = "0";
			}
			Double radix = (Double.valueOf(accumulationRadix)) == null ? 0 : (Double.valueOf(accumulationRadix.trim()));
			show.setAccumulationRadix(radix);
			show.setAccumulationUnit(Caccumulation);
			show.setAccumulationPerson(Paccumulation);
			Double accumulationTotal = OwnHomeUtils.add(Paccumulation, Caccumulation);
			show.setAccumulationTotalFee(accumulationTotal);
			show.setServiceFee(serviceFee);
			show.setDisabilityBenefit(disabilityBenefit);
			dgDetailList.add(show);
		}
		return dgDetailList;
	}

	@Transactional
	public ResultJson importGongjijin(MultipartFile file, Integer userId, Integer supplierId, String yearMonth) {
		if (file == null || file.isEmpty())
			return ResultUtil.error(RestEnum.FILE_NOT_EXISTS);
		String fileName = file.getOriginalFilename();
		if (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))
			return ResultUtil.error(RestEnum.FILE_FORMATS_ERROR);
		if (null == userId || StringUtils.isEmpty(yearMonth))
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		String filePath = UploadFile.uploadFile(file);
		List<String[]> list = ReadExcel.readIrregularExcel2List(filePath).get(0);
		DeleteFile.DeleteFolder(filePath);
		int result = 0;
		if (list.isEmpty())
			return ResultUtil.error(RestEnum.FILE_DATA_IS_NULL);
		int startLine = getAccumulationFundStartLine(list);
		if (!isDongguanAccumulationFundTemplate(list.get(startLine)))
			return ResultUtil.error(RestEnum.FAILD, "请上传东莞公积金模板");
		for (int j = startLine + 1; j < list.size(); j++) {
			String[] strArr = list.get(j);
			String name = strArr[1];//姓名
		    String certificateType = strArr[2];//证件类型
			String certificateNumber = strArr[3].replaceAll("x", "X").trim();// 身份证号码
			double accumulationFundRadix = Double.parseDouble(strArr[4]);// 缴存基数
			double accumulationFundOldRadix = Double.parseDouble(strArr[5]);// 旧基数
			String accumulationFundOldRatio = strArr[6];// 旧缴费比例
			String accumulationFundPersonRatio = strArr[7];// 个人缴费比例
			String accumulationFundCompanyRatio = strArr[8];// 公司缴费比例
			double accumulationFundPersonPay = Double.parseDouble(strArr[9]);// 个人缴存费用
			double accumulationFundCompanyPay = Double.parseDouble(strArr[10]);// 公司缴存费用
			double accumulationFundTotal = Double.parseDouble(strArr[11]);// 合计
			String accumulationFundAccount = strArr[12];// 个人账号
			String company = strArr[13];// 公司
			String manager = strArr[14];// 负责人
			// 根据月份、城市、身份证号码从在册表中获取在册信息
			ZaiceCustomerMsg zaiceMsg = zaiceMsg(yearMonth, DONGGUAN, certificateNumber);
			CostDg costDg = new CostDg( userId,  zaiceMsg.getCustomerId(),  supplierId,  zaiceMsg.getCustomerName(),  yearMonth,
					 name,  certificateNumber,  accumulationFundRadix,  accumulationFundOldRadix,
					 accumulationFundOldRatio,  accumulationFundPersonRatio,  accumulationFundCompanyRatio,
					 accumulationFundCompanyPay,  accumulationFundPersonPay,  accumulationFundTotal,
					 accumulationFundAccount,  company,  manager);
			if (isCostImport(yearMonth, certificateNumber)) {
				// 更新东莞费用表中公积金信息
				costDgMapper.updateAccumulationFund(costDg);
				result += 1;
			} else {
				// 更新东莞费用表中公积金信息
				costDgMapper.insertSelective(costDg);
				result += 1;
			}
			// 更新账单表中公积金信息
			zhangdanService.updateGongjijin(accumulationFundCompanyPay, accumulationFundPersonPay,
					zaiceMsg.getZaiceId());
		}
		return ResultUtil.success(RestEnum.SUCCESS, "处理：" + result + "条");
	}

	/**
	 * @param strArr
	 * @return
	 */
	public boolean isDongguanAccumulationFundTemplate(String[] strArr) {
		if (strArr[0].contains("序号") && strArr[1].contains("姓名") && strArr[2].contains("证件类型")
				&& strArr[3].contains("身份证号") && strArr[4].contains("缴存基数") && strArr[5].contains("旧基数")
				&& strArr[6].contains("旧缴费比例") && strArr[7].contains("个人") && strArr[8].contains("单位")
				&& strArr[9].contains("单位") && strArr[10].contains("个人") && strArr[11].contains("合计")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param list
	 * @return 返回東莞公積金數據起始行号
	 */
	private int getAccumulationFundStartLine(List<String[]> list) {
		int startLine = 0;
		for (int i = 0; i < list.size(); i++) {
			String[] strArr = list.get(i);
			if (strArr.length > 4)
				if (!StringUtil.isEmpty(strArr[0]) && !StringUtil.isEmpty(strArr[1]) && !StringUtil.isEmpty(strArr[2])
						&& !StringUtil.isEmpty(strArr[3]) && !StringUtils.isEmpty(strArr[4]))
					if ("序号".equals(strArr[0]) && "姓名".equals(strArr[1]) && "证件类型".equals(strArr[2])
							&& "身份证号".equals(strArr[3]) && "缴存基数".equals(strArr[4]))
						return startLine = i;
		}
		return startLine;
	}

	/**
	 * @param zaiceId
	 * @param costDg
	 * @return 供应商账单明细
	 */
	private ZhangdanMingxi getZhangdanMingxi(int zaiceId, CostDg costDg) {
		ZhangdanMingxi zdmingxi = new ZhangdanMingxi();
		zdmingxi.setZaiceId(zaiceId);
		Double Cpension = costDg.getPensionCompanyPay();
		Double CpensionSupplement = costDg.getPensionCompanySupplementPay();
		Double CpensionSupplementInterest = costDg.getPensionSupplementInterest();
		Double CpensionLateFees = costDg.getPensionLateFees();
		Double Ppension = costDg.getPensionPersonPay();
		Double PpensionSupplement = costDg.getPensionCompanyPersonPay();
		Double c_pensionFee = MathArithmetic.add(MathArithmetic.add(Cpension, CpensionSupplement),
				MathArithmetic.add(CpensionSupplementInterest, CpensionLateFees));
		zdmingxi.setShijiYanglaoGongsi(c_pensionFee);
		Double p_pensionFee = MathArithmetic.add(Ppension, PpensionSupplement);
		zdmingxi.setShijiYanglaoGeren(p_pensionFee);

		Double Pmedical = costDg.getMedicalTreatmentPersonPay();
		Double PmedicalSupplement = costDg.getMedicalTreatmentCompanyPersonPay();
		Double Cmedical = costDg.getMedicalTreatmentCompanyPay();
		Double CmedicalSupplement = costDg.getMedicalTreatmentCompanySupplementPay();
		Double CmedicalSupplementInterest = costDg.getMedicalTreatmentSupplementInterest();
		Double CmedicalLateFees = costDg.getMedicalTreatmentLateFees();
		Double c_medicalFee = MathArithmetic.add(MathArithmetic.add(Cmedical, CmedicalSupplement),
				MathArithmetic.add(CmedicalSupplementInterest, CmedicalLateFees));
		Double p_medicalFee = MathArithmetic.add(Pmedical, PmedicalSupplement);
		zdmingxi.setShijiJibenYiliaoGongsi(c_medicalFee);
		zdmingxi.setShijiJibenYiliaoGeren(p_medicalFee);

		Double PseriousIllness = costDg.getSeriousIllnessTreatmentPersonPay();
		Double PseriousIllnessSupplement = costDg.getSeriousIllnessTreatmentCompanyPersonPay();
		Double CseriousIllness = costDg.getSeriousIllnessTreatmentCompanyPay();
		Double CseriousIllnessSupplement = costDg.getSeriousIllnessTreatmentCompanySupplementPay();
		Double CseriousIllnessSupplementInterest = costDg.getSeriousIllnessTreatmentSupplementInterest();
		Double CseriousIllnessLateFees = costDg.getSeriousIllnessTreatmentLateFees();
		Double c_seriousIllness = MathArithmetic.add(OwnHomeUtils.add(CseriousIllness, CseriousIllnessSupplement),
				MathArithmetic.add(CseriousIllnessSupplementInterest, CseriousIllnessLateFees));
		Double p_seriousIllness = MathArithmetic.add(PseriousIllness, PseriousIllnessSupplement);
		zdmingxi.setShijiDabingYiliaoGongsi(c_seriousIllness);
		zdmingxi.setShijiDabingYiliaoGeren(p_seriousIllness);

		Double Cunemployment = costDg.getUnemploymentCompanyPay();
		Double CunemploymentSupplement = costDg.getUnemploymentCompanySupplementPay();
		Double CunemploymentSupplementInterest = costDg.getUnemploymentSupplementInterest();
		Double CunemploymentLateFees = costDg.getUnemploymentLateFees();
		Double Punemployment = costDg.getUnemploymentPersonPay();
		Double PunemploymentSupplement = costDg.getUnemploymentCompanyPersonPay();
		Double c_unemployment = MathArithmetic.add(MathArithmetic.add(Cunemployment, CunemploymentSupplement),
				MathArithmetic.add(CunemploymentSupplementInterest, CunemploymentLateFees));
		Double p_unemployment = MathArithmetic.add(Punemployment, PunemploymentSupplement);
		zdmingxi.setShijiShiyeGongsi(c_unemployment);
		zdmingxi.setShijiShiyeGeren(p_unemployment);

		Double CinjurySupplementInterest = costDg.getInjurySupplementInterest();
		Double CinjuryLateFees = costDg.getInjuryLateFees();
		Double CinjurySupplement = costDg.getInjuryCompanySupplementPay();
		Double Cinjury = costDg.getInjuryCompanyPay();
		Double Cprocreate = costDg.getProcreateCompanyPay();
		Double CprocreateSupplement = costDg.getProcreateCompanySupplementPay();
		Double CprocreateSupplementInterest = costDg.getProcreateSupplementInterest();
		Double CprocreateLateFees = costDg.getProcreateLateFees();
		Double c_injury = MathArithmetic.add(OwnHomeUtils.add(CinjurySupplementInterest, CinjuryLateFees),
				MathArithmetic.add(CinjurySupplement, Cinjury));
		Double c_procreate = MathArithmetic.add(OwnHomeUtils.add(Cprocreate, CprocreateSupplement),
				MathArithmetic.add(CprocreateSupplementInterest, CprocreateLateFees));
		zdmingxi.setShijiGongshangGongsi(c_injury);
		zdmingxi.setShijiShengyuGongsi(c_procreate);

		Double disabilityBenefit = costDg.getDisabilityBenefit();
		Double serviceFee = costDg.getServiceCharge();
		Double Paccumulation = costDg.getAccumulationFundPersonPay();
		Double Caccumulation = costDg.getAccumulationFundCompanyPay();
		zdmingxi.setShijiGongjijinGongsi(Caccumulation);
		zdmingxi.setShijiGongjijinGeren(Paccumulation);
		zdmingxi.setShijiQitaGongsi(0.00);
		zdmingxi.setShijiQitaGongsi(0.00);
		zdmingxi.setShijiCanbaojinGongsi(disabilityBenefit);
		zdmingxi.setShijiFuwufei(serviceFee);
		return zdmingxi;
	}

	// 导出东莞公积金
	@SuppressWarnings("deprecation")
	public String writeDGAccumulationFee(Integer supplierId, String yearMonth) throws ParseException, IOException {
		String city = "东莞";
		String supplier = zaiceService.selectSupplierBySupplierId(city, supplierId);
		if (supplier == null) {
			supplier = "数据为空！";
		}
		Workbook wb = new HSSFWorkbook();
		HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
		String[] strs = { "序号", "姓名", "证件类型", "身份证号", "缴存基数", "旧基数", "旧缴费比例", "个人", "单位", "单位", "个人", "合计", "个人账号",
				"公司", "负责人" };
		HSSFSheet sheet = (HSSFSheet) wb.createSheet(supplierId + "号供应商" + yearMonth + "月(东莞)公积金明细");
		style.setFillBackgroundColor(IndexedColors.AQUA.getIndex()); // 填满
		style.setFillForegroundColor(IndexedColors.ORANGE.getIndex()); // 填满前
		// 设置默认行高,列宽
		sheet.setDefaultRowHeightInPoints(20);
		sheet.setDefaultColumnWidth(20);
		// 创建字体设置字体为宋体
		HSSFFont font = (HSSFFont) wb.createFont();
		font.setFontName("宋体");
		// 设置字体高度
		font.setFontHeightInPoints((short) 12);
		CreationHelper createHelper = wb.getCreationHelper();
		style.setFont(font);
		// 设置自动换行
		style.setWrapText(true);
		// 设置单元格内容垂直对其方式为居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
		style.setAlignment(HorizontalAlignment.CENTER);// 水平 居中格式

		HSSFRow rows = sheet.createRow((short) 0);
		rows.setHeightInPoints(30);
		for (int y = 0; y < strs.length; y++) {
			HSSFCellStyle styles = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
			styles.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
			sheet.autoSizeColumn(y); // 自动调整宽度
			HSSFCell cells = rows.createCell(y, CellType.STRING);
			cells.setCellStyle(styles);
			HSSFFont fonts = (HSSFFont) wb.createFont();
			fonts.setColor(HSSFColor.BLUE_GREY.index);// HSSFColor.VIOLET.index
														// //字体颜色
			// fonts.setFontName("宋体");
			fonts.setBold(true);
			styles.setFont(fonts); // 字体增粗
			fonts.setFontHeightInPoints((short) 18);
			rows.getCell(0).setCellValue(yearMonth + "月(东莞)-" + supplier + "公积金明细");
		}
		// 合并单元格
		CellRangeAddress cra = new CellRangeAddress(0, (short) 0, 0, (short) (rows.getLastCellNum() - 1)); // 起始行,
																											// 终止行,
																											// 起始列,
																											// 终止列
		sheet.addMergedRegion(cra);

		HSSFRow rowss = sheet.createRow((int) 1);
		rowss.setHeightInPoints(20);
		for (int i = 0; i < strs.length; i++) {
			HSSFCellStyle stylet = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
			stylet.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
			stylet.setAlignment(HorizontalAlignment.CENTER); // 水平 居中格式
			sheet.autoSizeColumn(i); // 自动调整宽度
			HSSFFont fontt = (HSSFFont) wb.createFont();
			fontt.setBold(true);
			stylet.setFont(fontt);
			HSSFCell cell = rowss.createCell(i, CellType.STRING);
			sheet.setColumnWidth(i, (short) (15 * 256));
			cell.setCellStyle(stylet);
			cell.setCellValue(strs[i]);
		}

		List<CostDg> dgList = costDgMapper.selectCostDgAllBySupplierId(supplierId, yearMonth);
		List<DGAccumulation> dgAccumulations = new ArrayList<DGAccumulation>();
		if (dgList == null) {
			return null;
		}
		Integer supplierIds = null;
		String accumulationNumber = null;
		String accumulationRadix1 = null;
		String accumulationRatio1 = null;
		String oldRadix = null;
		String oldRatio = null;
		for (CostDg dgHome : dgList) {
			String month = dgHome.getReportingPeriod();
			String certificateNum = dgHome.getCertificateNumber();
			if (supplierIds == null || month == null || month == "" || certificateNum == null || certificateNum == "") {
				break;
			}
			Zaice zaice = zaiceService.findAccumulationInfos(city, month, certificateNum);
			if (zaice == null) {
				accumulationNumber = "账号为空!请检查数据";
				accumulationRadix1 = "0";
				accumulationRatio1 = "0%";
			} else {
				accumulationNumber = zaice.getAccumulationFundNumber();
				accumulationRadix1 = zaice.getAccumulationFundCardinalNumber();
				accumulationRatio1 = zaice.getAccumulationFundRatio();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Calendar calendar = Calendar.getInstance();

			DGAccumulation dgAccumulation = new DGAccumulation();
			dgAccumulation.setName(dgHome.getName());
			String certificateType = zaiceService.findCertificateType(dgHome.getName(), certificateNum, supplierIds);
			if (certificateType == null) {
				certificateType = "数据异常！";
			}
			dgAccumulation.setCertificateType(certificateType);
			dgAccumulation.setCertificateNum(certificateNum);
			Double payRadix = Double.valueOf((accumulationRadix1.trim()));
			dgAccumulation.setPayRadix(payRadix);

			Date date = (Date) sdf.parse(yearMonth);
			calendar.setTime(date);
			calendar.add(Calendar.DATE, -1);
			date = (Date) calendar.getTime();
			String oldMonth = sdf.format(date);
			Zaice zaices = zaiceService.findAccumulationInfos(city, month, certificateNum);
			if (zaices == null) {
				oldRadix = "0";
				oldRatio = "0%";
			} else {
				oldRadix = zaices.getAccumulationFundCardinalNumber();
				oldRatio = zaices.getAccumulationFundRatio();
			}
			int index = accumulationRatio1.indexOf("%");
			int indexs = accumulationRatio1.indexOf("+");
			String c_ratio = accumulationRatio1.substring(0, index);
			String p_ratio = accumulationRatio1.substring(indexs + 1, (accumulationRatio1.trim().length()) - 1);

			int o_index = oldRatio.indexOf("%");
			int o_indexs = oldRatio.indexOf("+");
			String o_c_ratio = oldRatio.substring(0, o_index);
			String o_p_ratio = oldRatio.substring(o_indexs + 1, (oldRatio.trim().length()) - 1);

			dgAccumulation.setOldPayRadix(Double.valueOf(oldRadix.trim()));
			Double o_payRatio = Double.valueOf(o_c_ratio.trim());
			dgAccumulation.setOldPayRatio(o_payRatio / 100.00);

			Double c_payRatio = Double.valueOf(c_ratio.trim());
			Double p_payRatio = Double.valueOf(p_ratio.trim());
			dgAccumulation.setPersonRatio(p_payRatio / 100.00);
			dgAccumulation.setCompanyRatio(c_payRatio / 100.00);
			Double c_payMoney = OwnHomeUtils.mul(payRadix, (c_payRatio / 100.00));
			Double p_payMoney = OwnHomeUtils.mul(payRadix, (p_payRatio / 100.00));
			dgAccumulation.setCompanyFee(c_payMoney);
			dgAccumulation.setPersonFee(p_payMoney);
			dgAccumulation.setTotalFee(OwnHomeUtils.add(c_payMoney, p_payMoney));
			dgAccumulation.setPersonNumber(accumulationNumber);
			String customerNmae = customerService.findCustomerName(dgHome.getCustomerId());
			if (customerNmae == null) {
				customerNmae = "********";
			}
			dgAccumulation.setCustomer(customerNmae);
			String userName = userService.findUserName(dgHome.getUserId());
			dgAccumulation.setManagerName(userName);

			if (dgHome.getName() != null && certificateNum != null) {
				dgAccumulations.add(dgAccumulation);
			}
		}

		for (int rowNum = 2; rowNum <= dgAccumulations.size() + 1; rowNum++) {
			HSSFRow row = sheet.createRow(rowNum);
			row.setHeightInPoints(20);
			HSSFCell cell = row.createCell(0, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(String.valueOf(rowNum - 1));
			cell = row.createCell(1, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(dgAccumulations.get(rowNum - 2).getName());
			cell = row.createCell(2, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(dgAccumulations.get(rowNum - 2).getCertificateType());
			cell = row.createCell(3, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(dgAccumulations.get(rowNum - 2).getCertificateNum());
			cell = row.createCell(4, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(dgAccumulations.get(rowNum - 2).getPayRadix());
			cell = row.createCell(5, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(dgAccumulations.get(rowNum - 2).getOldPayRadix());

			cell = rows.createCell(6, CellType.STRING);
			cell.setCellStyle(style);
			int oldRatio2 = (int) ((dgAccumulations.get(rowNum - 2).getOldPayRatio()) * 100);
			cell.setCellValue((String.valueOf(oldRatio2)) + "%");
			cell = row.createCell(7, CellType.STRING);
			cell.setCellStyle(style);
			int personRatio = (int) ((dgAccumulations.get(rowNum - 2).getPersonRatio()) * 100);
			cell.setCellValue(String.valueOf(personRatio) + "%");
			cell = row.createCell(8, CellType.STRING);
			cell.setCellStyle(style);
			int unitRatio = (int) ((dgAccumulations.get(rowNum - 2).getCompanyRatio()) * 100);
			cell.setCellValue(String.valueOf(unitRatio) + "%");

			cell = row.createCell(9, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(dgAccumulations.get(rowNum - 2).getCompanyFee());
			cell = row.createCell(10, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(dgAccumulations.get(rowNum - 2).getPersonFee());
			cell = row.createCell(11, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(dgAccumulations.get(rowNum - 2).getTotalFee());
			cell = row.createCell(12, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(dgAccumulations.get(rowNum - 2).getPersonNumber());
			cell = row.createCell(13, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(dgAccumulations.get(rowNum - 2).getCustomer());
			cell = row.createCell(14, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(dgAccumulations.get(rowNum - 2).getManagerName());

			for (int i = 0; i < 15; i++) {
				sheet.setColumnWidth(i, (short) (16 * 256));
				if (i == 12 || i == 3 || i == 13) {
					sheet.autoSizeColumn(i);
				}
			}
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String excelPath = request.getSession().getServletContext().getRealPath("/") + "download" + File.separator
				+ "excel" + File.separator + supplierId + File.separator;
		String filePath1 = excelPath + "编号" + supplierId + "的供应商" + yearMonth + "月公积金明细.xls";
		File file1 = new File(excelPath);
		if (!file1.exists() && !file1.isDirectory()) {
			file1.mkdirs();
		}
		File files1 = new File(filePath1);
		if (!files1.exists() && !files1.isFile()) {
			files1.createNewFile();
		}
		FileOutputStream out1 = null;
		try {
			out1 = new FileOutputStream(files1);
			wb.write(out1);
			out1.flush();
			return filePath1;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (out1 != null) {
				out1.close();
			}
			out1 = null;
		}
	}

	// 导出东莞费用
	@SuppressWarnings("resource")
	public String writeDGInfoAll(Integer supplierId, String yearMonth) throws IOException {
		String city = "东莞";
		String supplier = zaiceService.selectSupplierBySupplierId(city, supplierId);
		Workbook wb = new HSSFWorkbook();
		HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
		HSSFSheet sheet = (HSSFSheet) wb.createSheet(supplierId + "号供应商" + yearMonth + "月(东莞)费用明细");
		// 设置单元格内容垂直对其方式为居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
		style.setAlignment(HorizontalAlignment.CENTER);// 水平 // 创建一个居中格式

		String[] strs = { "姓名", "身份证号码", "社会基本养老保险", "失业保险", "工伤保险", "生育保险", "住院基本医疗保险", "门诊基本医疗保险", "住房公积金", "残保金",
				"服务费", "单位合计", "个人合计", "合计" };
		String[] strArrs = { "基数", "单位", "个人", "单位补", "个人补", "补利息", "滞纳金", "合计", "缴费基数", "单位", "个人", "单位补", "个人补",
				"补利息", "滞纳金", "合计", "基数", "单位", "个人", "单位补", "个人补", "补利息", "滞纳金", "合计", "基数", "单位", "个人", "单位补", "个人补",
				"补利息", "滞纳金", "合计", "基数", "单位", "个人", "单位补", "个人补", "补利息", "滞纳金", "合计", "基数", "单位", "个人", "单位补", "个人补",
				"补利息", "滞纳金", "合计", "基数", "公司", "个人", "合计" };
		// 设置默认行高,列宽
		// sheet.setDefaultRowHeight((short)(20 * 256));
		sheet.setDefaultRowHeightInPoints(20);
		sheet.setDefaultColumnWidth(15);
		// 创建字体设置字体为宋体
		HSSFFont font = (HSSFFont) wb.createFont();
		font.setFontName("宋体");
		// 设置字体高度
		font.setFontHeightInPoints((short) 12);
		CreationHelper createHelper = wb.getCreationHelper();
		style.setFont(font);
		// 设置自动换行
		style.setWrapText(true);

		HSSFRow rows = sheet.createRow((short) 0);
		rows.setHeightInPoints(30);
		for (int y = 0; y < strArrs.length + 7; y++) {
			HSSFCellStyle styles = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
			styles.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
			sheet.autoSizeColumn(y); // 自动调整宽度
			Cell cells = rows.createCell(y, CellType.STRING);
			cells.setCellStyle(styles);
			HSSFFont fonts = (HSSFFont) wb.createFont();
			fonts.setColor(HSSFColor.BLUE_GREY.index); // HSSFColor.VIOLET.index
														// //字体颜色
			fonts.setBold(true);
			styles.setFont(fonts); // 字体增粗
			fonts.setFontHeightInPoints((short) 18);
			rows.getCell(0).setCellValue(yearMonth + "月(东莞)-" + supplier + "费用明细");
		}
		// 合并单元格
		CellRangeAddress cra = new CellRangeAddress(0, (short) 0, 0, (short) (rows.getLastCellNum() - 1)); // 起始行,
																											// 终止行,
																											// 起始列,
																											// 终止列
		sheet.addMergedRegion(cra);

		HSSFRow row = sheet.createRow((short) 1);
		row.setHeightInPoints(20);
		HSSFRow row2 = sheet.createRow((short) 2);
		row2.setHeightInPoints(20);
		for (int i = 0; i < (strArrs.length) + 7; i++) {
			HSSFCellStyle stylet = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
			stylet.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
			stylet.setAlignment(HorizontalAlignment.CENTER); // 水平 居中格式
			HSSFFont fontt = (HSSFFont) wb.createFont();
			fontt.setBold(true);
			stylet.setFont(fontt);

			HSSFCell ce = row.createCell(i);
			sheet.setColumnWidth(i, (short) (13 * 256));
			ce.setCellStyle(stylet);
			if (i < 3) {
				ce.setCellValue(strs[i]); // 表格的第二行第一列显示的数据
			} else if (i == 10) {
				ce.setCellValue(strs[3]);
			} else if (i == 18) {
				ce.setCellValue(strs[4]);
			} else if (i == 26) {
				ce.setCellValue(strs[5]);
			} else if (i == 34) {
				ce.setCellValue(strs[6]);
			} else if (i == 42) {
				ce.setCellValue(strs[7]);
			} else if (i == 50) {
				ce.setCellValue(strs[8]);
			} else if (i == 54) {
				ce.setCellValue(strs[9]);
			} else if (i == 55) {
				ce.setCellValue(strs[10]);
			} else if (i == 56) {
				ce.setCellValue(strs[11]);
			} else if (i == 57) {
				ce.setCellValue(strs[12]);
			} else if (i == 58) {
				ce.setCellValue(strs[13]);
			} else {
				ce.setCellValue("");
			}
			HSSFCell ces = null;
			if (i >= 2 && i < (strArrs.length) + 2) {
				ces = row2.createCell(i);
				sheet.setColumnWidth(i, (short) (13 * 256));
				ces.setCellStyle(stylet);
				ces.setCellValue(strArrs[i - 2]);
			} else {
				ces = row2.createCell(i);
				sheet.setColumnWidth(i, (short) (13 * 256));
				ces.setCellStyle(stylet);
				ces.setCellValue("");
			}
			if (i == 1) {
				sheet.autoSizeColumn(i); // 自动调整宽度
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 0, (short) 0));// 设置单元格合并
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 1, (short) 1));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 2, (short) 9));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 10, (short) 17));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 18, (short) 25));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 26, (short) 33));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 34, (short) 41));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 42, (short) 49));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 50, (short) 53));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 54, (short) 54));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 55, (short) 55));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 56, (short) 56));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 57, (short) 57));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 58, (short) 58));

		List<CostDg> list = costDgMapper.selectCostDgAllBySupplierId(supplierId, yearMonth);
		List<DGDetailShow> detailList = getDGShow(list);
		if (detailList == null || list == null) {
			return null;
		}
		for (int i = 3; i < (detailList.size() + 3); i++) {
			List ll = new ArrayList();
			ll.add(detailList.get(i - 3).getName());
			ll.add(detailList.get(i - 3).getCertificateNumber());

			ll.add(detailList.get(i - 3).getPensionRadix());
			ll.add(detailList.get(i - 3).getPensionCompanyPay());
			ll.add(detailList.get(i - 3).getPensionPersonPay());
			ll.add(detailList.get(i - 3).getPensionCompanySupplementPay());
			ll.add(detailList.get(i - 3).getPensionCompanyPersonPay());
			ll.add(detailList.get(i - 3).getPensionSupplementInterest());
			ll.add(detailList.get(i - 3).getPensionLateFees());
			ll.add(detailList.get(i - 3).getPensionSocialTotal());

			ll.add(detailList.get(i - 3).getUnemploymentRadix());
			ll.add(detailList.get(i - 3).getUnemploymentCompanyPay());
			ll.add(detailList.get(i - 3).getUnemploymentPersonPay());
			ll.add(detailList.get(i - 3).getUnemploymentCompanySupplementPay());
			ll.add(detailList.get(i - 3).getUnemploymentCompanyPersonPay());
			ll.add(detailList.get(i - 3).getUnemploymentSupplementInterest());
			ll.add(detailList.get(i - 3).getUnemploymentLateFees());
			ll.add(detailList.get(i - 3).getUnemploymentSocialTotal());

			ll.add(detailList.get(i - 3).getInjuryRadix());
			ll.add(detailList.get(i - 3).getInjuryCompanyPay());
			ll.add(detailList.get(i - 3).getInjuryPersonPay());
			ll.add(detailList.get(i - 3).getInjuryCompanySupplementPay());
			ll.add(detailList.get(i - 3).getInjuryCompanyPersonPay());
			ll.add(detailList.get(i - 3).getInjurySupplementInterest());
			ll.add(detailList.get(i - 3).getInjuryLateFees());
			ll.add(detailList.get(i - 3).getInjurySocialTotal());

			ll.add(detailList.get(i - 3).getProcreateRadix());
			ll.add(detailList.get(i - 3).getProcreateCompanyPay());
			ll.add(detailList.get(i - 3).getProcreatePersonPay());
			ll.add(detailList.get(i - 3).getProcreateCompanySupplementPay());
			ll.add(detailList.get(i - 3).getProcreateCompanyPersonPay());
			ll.add(detailList.get(i - 3).getProcreateSupplementInterest());
			ll.add(detailList.get(i - 3).getProcreateLateFees());
			ll.add(detailList.get(i - 3).getProcreateSocialTotal());

			ll.add(detailList.get(i - 3).getSeriousIllnessTreatmentRadix());
			ll.add(detailList.get(i - 3).getSeriousIllnessTreatmentCompanyPay());
			ll.add(detailList.get(i - 3).getSeriousIllnessTreatmentPersonPay());
			ll.add(detailList.get(i - 3).getSeriousIllnessTreatmentCompanySupplementPay());
			ll.add(detailList.get(i - 3).getSeriousIllnessTreatmentCompanyPersonPay());
			ll.add(detailList.get(i - 3).getSeriousIllnessTreatmentSupplementInterest());
			ll.add(detailList.get(i - 3).getSeriousIllnessTreatmentLateFees());
			ll.add(detailList.get(i - 3).getSeriousIllnessSocialTotal());

			ll.add(detailList.get(i - 3).getMedicalTreatmentRadix());
			ll.add(detailList.get(i - 3).getMedicalTreatmentCompanyPay());
			ll.add(detailList.get(i - 3).getMedicalTreatmentPersonPay());
			ll.add(detailList.get(i - 3).getMedicalTreatmentCompanySupplementPay());
			ll.add(detailList.get(i - 3).getMedicalTreatmentCompanyPersonPay());
			ll.add(detailList.get(i - 3).getMedicalTreatmentSupplementInterest());
			ll.add(detailList.get(i - 3).getMedicalTreatmentLateFees());
			ll.add(detailList.get(i - 3).getMedicalTreatmentSocialTotal());

			ll.add(detailList.get(i - 3).getAccumulationRadix());
			ll.add(detailList.get(i - 3).getAccumulationUnit());
			ll.add(detailList.get(i - 3).getAccumulationPerson());
			ll.add(detailList.get(i - 3).getAccumulationTotalFee());

			ll.add(detailList.get(i - 3).getDisabilityBenefit());
			ll.add(detailList.get(i - 3).getServiceFee());
			ll.add(detailList.get(i - 3).getCompanyTotal());
			ll.add(detailList.get(i - 3).getPersonTotal());
			ll.add(detailList.get(i - 3).getTotal());

			HSSFRow rowLine = sheet.createRow((short) i);
			rowLine.setHeightInPoints(20);
			sheet.setDefaultColumnWidth(13);
			for (int x = 0; x < (strArrs.length + 7); x++) {
				HSSFCell cc = null;
				if (x >= 0 && x < 2) {
					cc = rowLine.createCell(x, CellType.STRING);
					sheet.setColumnWidth(x, (short) (13 * 256));
					cc.setCellStyle(style);
					cc.setCellValue(ll.get(x) + "".trim());
				} else {
					cc = rowLine.createCell(x, CellType.NUMERIC);
					sheet.setColumnWidth(x, (short) (13 * 256));
					cc.setCellStyle(style);
					Object obj = ll.get(x);
					if (obj == null) {
						obj = 0.00;
					}
					Double value = (Double) obj;
					cc.setCellValue(value);
				}
				if (x == 1) {
					sheet.autoSizeColumn(x); // 自动调整宽度
				}
			}
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String excelPath = request.getSession().getServletContext().getRealPath("/") + "download" + File.separator
				+ "excel" + File.separator + supplierId + File.separator;
		String filePath1 = excelPath + "编号" + supplierId + "的供应商" + yearMonth + "月社保费用明细.xls";
		File file1 = new File(excelPath);
		if (!file1.exists() && !file1.isDirectory()) {
			file1.mkdirs();
		}
		File files1 = new File(filePath1);
		if (!files1.exists() && !files1.isFile()) {
			files1.createNewFile();
		}
		FileOutputStream out1 = null;
		try {
			out1 = new FileOutputStream(files1);
			wb.write(out1);
			out1.flush();
			return filePath1;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (out1 != null) {
				out1.close();
			}
			out1 = null;
		}
	}

	// 批量删除东莞自有户信息
	public String deleteDGInfoAll(Integer supplierId, String yearMonth) {
		int a = -1;
		List<CostDg> dgList = costDgMapper.selectCostDgAllBySupplierId(supplierId, yearMonth);
		a = costDgMapper.deleteCostDgDetails(supplierId, yearMonth);
		if (a > 0 && a == (dgList.size())) {
			return "删除成功！";
		}
		return null;
	}
}