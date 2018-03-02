package com.dianmi.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import com.dianmi.mapper.CostGzMapper;
import com.dianmi.model.CostGz;
import com.dianmi.model.Jianyuan;
import com.dianmi.model.Zaice;
import com.dianmi.model.Zengyuan;
import com.dianmi.model.ZhangdanMingxi;
import com.dianmi.model.accumulation.GZAccumulation;
import com.dianmi.model.accumulation.GZExportAccumulation;
import com.dianmi.model.owndetailshow.GZDetailShow;
import com.dianmi.model.ownhome.SZLDDGShow;
import com.dianmi.model.po.ZaiceCustomerMsg;
import com.dianmi.service.CostGzService;
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

@SuppressWarnings("all")
@Service
public class CostGzServiceImpl extends CommonService implements CostGzService {

	// 导入广州社保费用数据
	@Transactional
	@Override
	public ResultJson importCost(MultipartFile file, Integer userId, Integer supplierId, String yearMonth) {
		System.out.println("userId:" + userId);
		if (file == null || file.isEmpty())
			return ResultUtil.error(RestEnum.FILE_NOT_EXISTS);
		String fileName = file.getOriginalFilename();
		if (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))
			return ResultUtil.error(RestEnum.FILE_FORMATS_ERROR);
		if (null == userId || StringUtils.isEmpty(yearMonth))
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		String filePath = UploadFile.uploadFile(file);
		List<String[]> list = ReadExcel.readIrregularExcel2List(filePath).get(0);
		DeleteFile.DeleteFolder(filePath);// 上传成功删除Excel模板
		if (list.isEmpty())
			return ResultUtil.error(RestEnum.FILE_DATA_IS_NULL);
		int startLine = getSocialSecurityStartLine(list);// 缴费数据起始行
		if (!isGuangzhouSocialSecurityTemplate(list.get(startLine)))
			return ResultUtil.error(RestEnum.FAILD, "请上传广州社保模板");
		String paymentMonth = list.get(startLine - 1)[5];// 缴费月份
		int result = 0;
		List<String> allCertificateNumberList = new ArrayList<String>();// 所有读取的身份证号码集合
		for (int j = startLine + 2; j < list.size(); j++) {
			List<String> strList = Arrays.asList(list.get(j));
			String name = strList.get(0); // 客户姓名
			String certificateNumber = strList.get(1).replaceAll("x", "X").trim(); // 客户身份证号
			String certificateName = strList.get(2); // 证件名称
			String socialSecurityNumber = strList.get(3); // 社保/医保号
			Double pensionRadix = Double.valueOf(strList.get(4)); // 默认0.00，养老缴纳基数
			Double pensionCompanyPay = Double.valueOf(strList.get(5));// 养老单位缴费
			Double pensionPersonPay = Double.valueOf(strList.get(6));// 养老个人缴费
			Double injuryCompanyRadix = Double.valueOf(strList.get(7)); // 默认0.00，工伤保险企业缴费基数
			Double injuryCompanyPay = Double.valueOf(strList.get(8));// 工伤单位缴费
			Double injuryPersonPay = Double.valueOf(strList.get(9));// 工伤个人缴费
			Double unemploymentRadix = Double.valueOf(strList.get(10)); // 失业保险缴纳基数
			Double unemploymentCompanyPay = Double.valueOf(strList.get(11));// 失业单位缴费
			Double unemploymentPersonPay = Double.valueOf(strList.get(12));// 失业个人缴费
			Double medicalTreatmentRadix = Double.valueOf(strList.get(13)); // 社会医疗缴纳基数
			Double medicalTreatmentCompanyPay = Double.valueOf(strList.get(14));// 社会医疗单位缴费
			Double medicalTreatmentPersonPay = Double.valueOf(strList.get(15));// 社会医疗个人缴费
			Double seriousIllnessRedix = Double.valueOf(strList.get(16)); // 重大疾病医疗缴费基数
			Double seriousIllnessCompanyPay = Double.valueOf(strList.get(17));// 重大疾病医疗单位缴费
			Double seriousIllnessPersonPay = Double.valueOf(strList.get(18));// 重大疾病医疗个人缴费
			Double procreateCompanyRadix = Double.valueOf(strList.get(19)); // 默认0.00，生育保险企业缴费基数
			Double procreateCompanyPay = Double.valueOf(strList.get(20));// 生育单位缴费
			Double procreatePersonPay = Double.valueOf(strList.get(21));// 生育个人缴费
			Double companyTotal = Double.valueOf(strList.get(22)); // 默认0.00，公司部分合计
			Double personTotal = Double.valueOf(strList.get(23)); // 默认0.00，个人部分合计
			Double total = Double.valueOf(strList.get(24)); // 默认0.00，合计应缴金额
			allCertificateNumberList.add(certificateNumber);
			ZaiceCustomerMsg customerMsg = zaiceMsg(yearMonth, GUANGZHOU, certificateNumber);
			CostGz costGz = new CostGz(userId, customerMsg.getCustomerId(), supplierId, customerMsg.getCustomerName(),
					yearMonth, name, certificateNumber, certificateName, socialSecurityNumber, paymentMonth,
					pensionRadix, pensionCompanyPay, pensionPersonPay, unemploymentRadix, unemploymentCompanyPay,
					unemploymentPersonPay, injuryCompanyRadix, injuryCompanyPay, injuryPersonPay, procreateCompanyRadix,
					procreateCompanyPay, procreatePersonPay, medicalTreatmentRadix, medicalTreatmentCompanyPay,
					medicalTreatmentPersonPay, seriousIllnessRedix, seriousIllnessCompanyPay, seriousIllnessPersonPay,
					0.0, companyTotal, personTotal, total, 0.0, null, 0.0, null, 0.0, 0.0, null, null, null);
			// 判断费用是否已经导入
			if (isCostImport(yearMonth, certificateNumber)) {
				// 更新广州费用信息
				costGzMapper.updateCostGz(costGz);
				result += 1;
			} else {
				// 新增广州费用信息
				costGzMapper.insertSelective(costGz);
				result += 1;
			}
			// 账单明细信息更新
			zhangdanService.updateZhangdanByZaiceId(getZhangdanMingxi(customerMsg.getZaiceId(), costGz));
			// 确定增员成功
			zengyuanService.updateAddStatus(certificateNumber, GUANGZHOU, yearMonth);
			zaiceService.updateAddSocialStatus(yearMonth, GUANGZHOU, certificateNumber);
		}
		List<String> allCertificateNumber = zengyuanService.getAllCertificateNumber(yearMonth, supplierId);// 当前月份下按供应商分类的所有身份证号码集合
		List<String> newZengyuanList = new ArrayList<String>(allCertificateNumber);
		newZengyuanList.removeAll(allCertificateNumberList);
		updateToZengyuanFailed(newZengyuanList, yearMonth, GUANGZHOU);
		List<String> allJianyuanCertificateNumber = jianyuanService.getAllCertificateNumber(yearMonth, supplierId);
		List<String> jianyuanFaildList = new ArrayList<String>(allJianyuanCertificateNumber);
		jianyuanFaildList.removeAll(newZengyuanList);
		updateToJianyuanFailed(jianyuanFaildList, yearMonth, GUANGZHOU);
		// 减员成功的用户信息
		List<String> jianyuanSuccessList = new ArrayList<String>(allJianyuanCertificateNumber);
		jianyuanSuccessList.removeAll(jianyuanFaildList);
		updateToJianyuanSuccess(jianyuanSuccessList, yearMonth, GUANGZHOU);
		return ResultUtil.success(RestEnum.SUCCESS, "处理：" + result + "条");
	}

	/**
	 * @param strArr
	 * @return
	 */
	private boolean isGuangzhouSocialSecurityTemplate(String[] strArr) {
		if (strArr[0].contains("姓名") && strArr[1].contains("身份证明号码") && strArr[2].contains("证件名称")
				&& strArr[3].contains("个人社保号")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param list
	 * @return 获取社保起始行
	 */
	public int getSocialSecurityStartLine(List<String[]> list) {
		int startLine = 0;// 缴费数据起始行
		for (int i = 0; i < list.size(); i++) {
			String[] strArr = list.get(i);
			if (strArr.length > 5)
				if (!StringUtils.isEmpty(strArr[0]) && !StringUtils.isEmpty(strArr[1])
						&& !StringUtils.isEmpty(strArr[2]) && !StringUtils.isEmpty(strArr[3])
						&& !StringUtils.isEmpty(strArr[4]))
					if (("姓名").equals(strArr[0]) && ("身份证明号码").equals(strArr[1]) && ("证件名称").equals(strArr[2])
							&& ("个人社保号").equals(strArr[3]) && ("基本养老保险").equals(strArr[4]))
						return startLine = i;
		}
		return startLine;
	}

	/**
	 * @param yearMonth
	 * @param certificateNumber
	 * @return
	 */
	public boolean isCostImport(String yearMonth, String certificateNumber) {
		if (costGzMapper.isCostImport(yearMonth, certificateNumber).size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * @param file
	 * @param userId
	 * @param yearMonth
	 *            导入公积金费用
	 */
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
		if (list.isEmpty())
			return ResultUtil.error(RestEnum.FILE_DATA_IS_NULL);
		int accumulationFundStartLine = getAccumulationFundStartLine(list);
		if (!isGuangzhouAccumulationFundTemplate(list.get(accumulationFundStartLine)))
			return ResultUtil.error(RestEnum.FAILD, "请上传广州公积金模板");
		int result = 0;
		for (int i = accumulationFundStartLine + 1; i < list.size(); i++) {
			String[] strArr = list.get(i);
			String name = strArr[1];
			String certificateNumber = strArr[2];// 身份證號碼
			double accumulationFundRadix = Double.parseDouble(strArr[3]);// 缴存基数
			String accumulationFundCompanyRatio = strArr[4];// 公司缴存比例
			double accumulationFundCompanyPay = Double.parseDouble(strArr[5]);// 公司缴费
			String accumulationFundPersonRatio = strArr[6];// 个人缴存比例
			double accumulationFundPersonPay = Double.parseDouble(strArr[7]);// 个人缴费
			double accumulationFundTotal = MathArithmetic.add(accumulationFundCompanyPay, accumulationFundPersonPay);// 月缴存额合计
			String systemPersonNumber = strArr[8];
			String company = strArr[9];// 公司
			String manager = strArr[10];// 负责人
			ZaiceCustomerMsg customerMsg = zaiceMsg(yearMonth, GUANGZHOU, certificateNumber);
			CostGz costGz = new CostGz(userId, customerMsg.getCustomerId(), supplierId, customerMsg.getCustomerName(),
					yearMonth, name, certificateNumber, accumulationFundRadix, accumulationFundCompanyRatio,
					accumulationFundCompanyPay, accumulationFundPersonRatio, accumulationFundPersonPay,
					accumulationFundTotal, systemPersonNumber, company, manager);
			if (isCostImport(yearMonth, certificateNumber)) {
				costGzMapper.updateAccumulationFund(costGz);
				result += 1;
			} else {
				costGzMapper.insertSelective(costGz);
				result += 1;
			}
			// 更新账单表中公积金信息
			zhangdanService.updateGongjijin(accumulationFundCompanyPay, accumulationFundPersonPay,
					customerMsg.getZaiceId());
		}
		return ResultUtil.success(RestEnum.SUCCESS, "处理：" + result + "条");
	}

	/**
	 * @param strArr
	 * @return 判断上传的模板是否是公积金模板
	 */
	public boolean isGuangzhouAccumulationFundTemplate(String[] strArr) {
		if (strArr[0].contains("序号") && strArr[1].contains("姓名") && strArr[3].contains("缴存基数")
				&& strArr[4].contains("公司缴存比例") && strArr[5].contains("公司月缴存额") && strArr[6].contains("个人缴存比例")
				&& strArr[7].contains("个人月缴存额") && strArr[8].contains("月缴存额合计")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param list
	 * @return 获取公积金数据起始行
	 */
	public int getAccumulationFundStartLine(List<String[]> list) {
		int startLine = 0;
		for (int i = 0; i < list.size(); i++) {
			String[] strArr = list.get(i);
			if (strArr.length > 4)
				if (!StringUtil.isEmpty(strArr[0]) && !StringUtil.isEmpty(strArr[1]) && !StringUtil.isEmpty(strArr[2])
						&& !StringUtil.isEmpty(strArr[3]) && !StringUtils.isEmpty(strArr[4]))
					if ("序号".equals(strArr[0]) && "姓名".equals(strArr[1]) && "缴存基数".equals(strArr[3])
							&& "公司缴存比例".equals(strArr[4]))
						return startLine = i;
		}
		return startLine;
	}

	/**
	 * 读取广州自有户扣费数据
	 */
	@Transactional
	public List<CostGz> readGZSocialInfo(String filePath, Integer userId, Integer supplierId, String yearMonth) {
		String CITY = "广州";
		List<CostGz> gzHomeList = new ArrayList<CostGz>();
		Workbook book = null;
		try {
			book = WorkbookFactory.create(new FileInputStream(new File(filePath)));
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Sheet sheet = book.getSheetAt(0);
		int rowNum = sheet.getLastRowNum();
		String str = OwnHomeUtils.getSpecificRow(sheet);
		String strs[] = str.split(",");
		Integer line = Integer.valueOf(strs[0]) + 2;
		for (int i = line; i <= rowNum; i++) {
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
			String uname = row.getCell(0).getStringCellValue().trim(); // 姓名
			String certificateNumber = row.getCell(1).getStringCellValue().trim(); // 客户身份证号
			if (uname == "" && certificateNumber == "") {
				break;
			}
			String certificateName = row.getCell(2).getStringCellValue().trim(); // 证件名称
			String socialSecurityNumber = row.getCell(3).getStringCellValue().trim(); // 社保/医保号
			Double pensionRadix = row.getCell(4).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(4).getStringCellValue().trim()); // 默认0.00，养老缴纳基数
			Double pensionCompanyPay = row.getCell(5).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(5).getStringCellValue().trim()); // 默认0.00，养老公司缴费
			Double pensionPersonPay = row.getCell(6).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(6).getStringCellValue().trim());
			Double injuryCompanyRadix = row.getCell(7).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(7).getStringCellValue().trim()); // 默认0.00，工伤保险企业缴费基数
			Double injuryCompanyPay = row.getCell(8).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(8).getStringCellValue().trim());
			Double injuryPersonPay = row.getCell(9).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(9).getStringCellValue().trim());
			Double unemploymentRadix = row.getCell(10).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(10).getStringCellValue().trim()); // 默认0.00，失业保险缴纳基数
			Double unemploymentCompanyPay = row.getCell(11).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(11).getStringCellValue().trim());
			Double unemploymentPersonPay = row.getCell(12).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(12).getStringCellValue().trim());
			Double medicalTreatmentRadix = row.getCell(13).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(13).getStringCellValue().trim()); // 默认0.00，医疗缴费基数
			Double medicalTreatmentCompanyPay = row.getCell(14).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(14).getStringCellValue().trim());
			Double medicalTreatmentPersonPay = row.getCell(15).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(15).getStringCellValue().trim());
			Double seriousIllnessRedix = row.getCell(16).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(16).getStringCellValue().trim()); // 大病缴费基数
			Double seriousIllnessCompanyPay = row.getCell(17).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(17).getStringCellValue().trim());
			Double seriousIllnessPersonPay = row.getCell(18).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(18).getStringCellValue().trim());
			Double procreateCompanyRadix = row.getCell(19).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(19).getStringCellValue().trim()); // 默认0.00，生育保险企业缴费基数
			Double procreateCompanyPay = row.getCell(20).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(20).getStringCellValue().trim());
			Double procreatePersonPay = row.getCell(21).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(21).getStringCellValue().trim());
			Double companyTotal = row.getCell(22).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(22).getStringCellValue().trim()); // 默认0.00，公司部分合计
			Double personTotal = row.getCell(23).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(23).getStringCellValue().trim()); // 默认0.00，个人部分合计
			Double total = row.getCell(24).getStringCellValue() == "" ? 0.00
					: Double.valueOf(row.getCell(24).getStringCellValue().trim()); // 应缴金额

			Map<String, Object> customerMsg = zaiceService.selectZaiceMsg(CITY, yearMonth, certificateNumber);
			int customerId = (int) customerMsg.get("customer_id");
			CostGz gzhome = new CostGz();
			gzhome.setUserId(userId);
			gzhome.setCustomerId(customerId);
			gzhome.setReportingPeriod(yearMonth);
			gzhome.setName(uname);
			gzhome.setPaymentMonth(strs[3]);
			gzhome.setCertificateNumber(certificateNumber);
			gzhome.setCertificateName(certificateName);
			gzhome.setSocialSecurityNumber(socialSecurityNumber);
			gzhome.setPensionRadix(pensionRadix);
			gzhome.setPensionCompanyPay(pensionCompanyPay);
			gzhome.setPensionPersonPay(pensionPersonPay);
			gzhome.setInjuryCompanyRadix(injuryCompanyRadix);
			gzhome.setInjuryCompanyPay(injuryCompanyPay);
			gzhome.setInjuryPersonPay(injuryPersonPay);
			gzhome.setUnemploymentRadix(unemploymentRadix);
			gzhome.setUnemploymentCompanyPay(unemploymentCompanyPay);
			gzhome.setUnemploymentPersonPay(unemploymentPersonPay);
			gzhome.setMedicalTreatmentRadix(medicalTreatmentRadix);
			gzhome.setMedicalTreatmentCompanyPay(medicalTreatmentCompanyPay);
			gzhome.setMedicalTreatmentPersonPay(medicalTreatmentPersonPay);
			gzhome.setSeriousIllnessRadix(seriousIllnessRedix);
			gzhome.setSeriousIllnessCompanyPay(seriousIllnessCompanyPay);
			gzhome.setSeriousIllnessPersonPay(seriousIllnessPersonPay);
			gzhome.setProcreateCompanyRadix(procreateCompanyRadix);
			gzhome.setProcreateCompanyPay(procreateCompanyPay);
			gzhome.setProcreatePersonPay(procreatePersonPay);
			gzhome.setCompanyTotal(companyTotal);
			gzhome.setPersonTotal(personTotal);
			gzhome.setSocialSecurityTotal(total);
			if (uname != "" && certificateNumber != "") {
				gzHomeList.add(gzhome);
				costGzMapper.insert(gzhome);

				// ZhangdanMingxi supplierDetail = getSupplierDetail(gzhome);
				Map<String, Object> zaiceMsg = zaiceService.selectZaiceMsg(CITY, gzhome.getReportingPeriod(),
						gzhome.getCertificateNumber());
				int zaiceId = (int) zaiceMsg.get("zc_id");
				// supplierDetail.setZaiceId(zaiceId);
				// zhangdanService.updateZhangdanByZaiceId(supplierDetail);
			}
		}
		DeleteFile.DeleteFolder(filePath); // 上传成功删除Excel模板

		Integer count = 0;
		List<Zengyuan> zengyuanList = zengyuanService.selectZengyuanAll();
		List<Jianyuan> jianyuanList = jianyuanService.selectJianyuanAll();
		for (Zengyuan zengyuan : zengyuanList) {
			String certificateNumber = zengyuan.getCertificateNumber();
			String city = zengyuan.getCity();
			String month = zengyuan.getReportingPeriod();
			Integer supplierIds = zengyuan.getSupplierId();
			if (city == null || certificateNumber == null || month == null || supplierIds == null
					|| !"广州".equals(city)) {
				continue;
			}
			if (getUpdateGZAddResult(costGzMapper, city, supplierIds, month, certificateNumber)) {
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
					|| !"广州".equals(city)) {
				continue;
			}
			if (getUpdateGZMinusResult(costGzMapper, city, supplierIds, month, certificateNumber)) {
				jianyuanService.updateMinusSocialStatus(certificateNumber, city, month);
				zaiceService.updateAddSocialStatus(city, month, certificateNumber);
			} else {
				jianyuanService.updateMinusStatus(certificateNumber, month, city);
			}
		}
		return gzHomeList;
	}

	// 广州
	private Boolean getUpdateGZAddResult(CostGzMapper costGzMapper, String city, Integer supplierId, String month,
			String certificateNumber) {
		Boolean flag = false;
		city = city.trim();
		certificateNumber = certificateNumber.trim();
		month = month.trim();
		String addEmployeeName = costGzMapper.selectEmployeeName(supplierId, month, certificateNumber);
		if (addEmployeeName != null && addEmployeeName != "") {
			flag = true;
		}
		return flag;
	}

	private Boolean getUpdateGZMinusResult(CostGzMapper costGzMapper, String city, Integer supplierId, String month,
			String certificateNumber) {
		Boolean flag = false;
		city = city.trim();
		certificateNumber = certificateNumber.trim();
		month = month.trim();
		String minusEmployeeName = costGzMapper.selectEmployeeName(supplierId, month, certificateNumber);
		if (minusEmployeeName == null) {
			flag = true;
		}
		return flag;
	}

	// 分页对象
	private List<SZLDDGShow> getSGShow(List<CostGz> list) {
		List<SZLDDGShow> showList = new ArrayList<SZLDDGShow>();
		for (CostGz gzHome : list) {
			SZLDDGShow shows = new SZLDDGShow();
			Map<String, String> strs = customerService.findCustomerInfo(gzHome.getCustomerId());
			shows.setReportingPeriod((gzHome.getReportingPeriod() == null ? "" : gzHome.getReportingPeriod()));
			String customerName = null;
			String dept = null;
			if (strs == null) {
				customerName = "数据异常！";
				dept = "数据异常！";
			} else {
				customerName = strs.get("customerName");
				dept = strs.get("deptName");
			}
			shows.setCustomerName(customerName);
			shows.setDept(dept);

			Double Ppension = gzHome.getPensionPersonPay();
			Double Punemployment = gzHome.getUnemploymentPersonPay();
			Double Pinjury = gzHome.getInjuryPersonPay();
			Double Pprocreate = gzHome.getProcreatePersonPay();
			Double PmedicalTreatment = gzHome.getMedicalTreatmentPersonPay();
			Double PseriousIllness = gzHome.getSeriousIllnessPersonPay();
			// Double personFee =
			// (Ppension+Punemployment+Pinjury)+(Pprocreate+PmedicalTreatment+PseriousIllness);
			Double personFee = gzHome.getPersonTotal();

			Double Cpension = gzHome.getPensionCompanyPay();
			Double Cunemployment = gzHome.getUnemploymentCompanyPay();
			Double Cinjury = gzHome.getInjuryCompanyPay();
			Double Cprocreate = gzHome.getProcreateCompanyPay();
			Double Cmedical = gzHome.getMedicalTreatmentCompanyPay();
			Double CseriousIllness = gzHome.getSeriousIllnessCompanyPay();
			// Double companyFee = (Cpension+Cunemployment+Cinjury)+
			// (Cprocreate+Cmedical+CseriousIllness);
			Double companyFee = gzHome.getCompanyTotal();

			shows.setPersonSocialTotal(personFee);
			shows.setCompanySocialTotal(companyFee);
			Double socialTotal = OwnHomeUtils.add(personFee, companyFee);
			shows.setSocialTotalPay(socialTotal);

			Double Caccumulation = gzHome.getAccumulationFundCompanyPay();
			Double Paccumulation = gzHome.getAccumulationFundPersonPay();
			Double serviceFee = gzHome.getServiceCharge();
			shows.setCompanyAccumulation(Caccumulation);
			shows.setPersonAccumulation(Paccumulation);

			Double accumulationTotalFee = OwnHomeUtils.add(Caccumulation, Paccumulation);
			shows.setAccumulationTotalPay(accumulationTotalFee);
			Double pensionFee = OwnHomeUtils.add(Ppension, Cpension);
			shows.setPensionSocial(pensionFee);
			Double medicalFee = OwnHomeUtils.add(PmedicalTreatment, Cmedical);
			shows.setMedicaltreatmentSocial(medicalFee);
			Double seriousIllnessFee = OwnHomeUtils.add(PseriousIllness, CseriousIllness);
			shows.setSeriousIllnessSocial(seriousIllnessFee);
			Double injuryFee = OwnHomeUtils.add(Pinjury, Cinjury);
			shows.setInjurySocial(injuryFee);
			Double unemploymentFee = OwnHomeUtils.add(Punemployment, Cunemployment);
			shows.setUnemploymentSocial(unemploymentFee);
			Double procreateFee = OwnHomeUtils.add(Pprocreate, Cprocreate);
			shows.setProcreateSocial(procreateFee);

			shows.setServiceFee(serviceFee);
			Double Total = OwnHomeUtils.add(serviceFee, OwnHomeUtils.add(socialTotal, accumulationTotalFee));
			shows.setTotalFee(Total);
			showList.add(shows);
		}
		return showList;
	}

	// 广州分页
	public List selectGZhomeAll(Integer supplierId, String month, Integer currPage, Integer pageSize) {
		List<CostGz> list = costGzMapper.selectGZhomeAll(supplierId, month, (currPage - 1), pageSize);
		if (list == null) {
			return new ArrayList();
		}
		PageHelper.startPage(currPage, pageSize);
		List<SZLDDGShow> showList = getSGShow(list);

		PageInfo<SZLDDGShow> pageList = new PageInfo<SZLDDGShow>(showList);
		// pageList.getTotal();
		return pageList.getList();
	}

	// 广州详情展示
	public List selectGZInfoAll(Integer supplierId, String yearMonth, Integer currPage, Integer pageSize) {
		List<CostGz> list = costGzMapper.selectGZhomeAll(supplierId, yearMonth, (currPage - 1), pageSize);
		if (list == null) {
			return new ArrayList();
		}
		PageHelper.startPage(currPage, pageSize);
		List<GZDetailShow> gzDetailList = getGZShows(list);
		if (gzDetailList == null) {
			return new ArrayList();
		}
		PageInfo<GZDetailShow> pageList = new PageInfo<GZDetailShow>(gzDetailList);
		return pageList.getList();
	}

	// 详情对象
	private List<GZDetailShow> getGZShows(List<CostGz> list) {
		String city = "广州";
		List<GZDetailShow> gzDetailList = new ArrayList<GZDetailShow>();
		String accumulationRadix = null;
		for (CostGz gzHome : list) {
			Zaice zaice = zaiceService.findAccumulationInfos(city, gzHome.getReportingPeriod(),
					gzHome.getCertificateNumber());
			if (zaice == null) {
				accumulationRadix = "0";
			} else {
				accumulationRadix = zaice.getAccumulationFundCardinalNumber();
			}
			GZDetailShow show = new GZDetailShow();
			show.setName(gzHome.getName());
			show.setCertificateNumber(gzHome.getCertificateName());
			show.setSocialSecurityNumber(gzHome.getSocialSecurityNumber());

			show.setPensionPersonPay(gzHome.getPensionPersonPay());
			show.setPensionCompanyPay(gzHome.getPensionCompanyPay());
			show.setPensionRadix(gzHome.getPensionRadix());

			show.setUnemploymentPersonPay(gzHome.getUnemploymentPersonPay());
			show.setUnemploymentCompanyPay(gzHome.getUnemploymentCompanyPay());
			show.setUnemploymentRadix(gzHome.getUnemploymentRadix());

			show.setInjuryPersonPay(gzHome.getInjuryPersonPay());
			show.setInjuryCompanyPay(gzHome.getInjuryCompanyPay());
			show.setInjuryCompanyRadix(gzHome.getInjuryCompanyRadix());

			show.setProcreatePersonPay(gzHome.getProcreatePersonPay());
			show.setProcreateCompanyPay(gzHome.getProcreateCompanyPay());
			show.setProcreateCompanyRadix(gzHome.getProcreateCompanyRadix());

			show.setMedicalTreatmentPersonPay(gzHome.getMedicalTreatmentPersonPay());
			show.setMedicalTreatmentCompanyPay(gzHome.getMedicalTreatmentCompanyPay());
			show.setMedicalTreatmentRadix(gzHome.getMedicalTreatmentRadix());

			show.setSeriousIllnessPersonPay(gzHome.getSeriousIllnessPersonPay());
			show.setSeriousIllnessCompanyPay(gzHome.getSeriousIllnessCompanyPay());
			show.setSeriousIllnessRedix(gzHome.getSeriousIllnessRadix());

			Double Ppension = gzHome.getPensionPersonPay();
			Double Punemployment = gzHome.getUnemploymentPersonPay();
			Double Pinjury = gzHome.getInjuryPersonPay();
			Double Pprocreate = gzHome.getProcreatePersonPay();
			Double PmedicalTreatment = gzHome.getMedicalTreatmentPersonPay();
			Double PseriousIllness = gzHome.getSeriousIllnessPersonPay();
			Double Paccumulation = gzHome.getAccumulationFundPersonPay();
			/*
			 * Double personTotal = (Ppension+Punemployment+Pinjury)+
			 * (Pprocreate+PmedicalTreatment)+(PseriousIllness+Paccumulation);
			 */
			Double personTotal = gzHome.getPersonTotal();

			Double Cpension = gzHome.getPensionCompanyPay();
			Double Cunemployment = gzHome.getUnemploymentCompanyPay();
			Double Cinjury = gzHome.getInjuryCompanyPay();
			Double Cprocreate = gzHome.getProcreateCompanyPay();
			Double Cmedical = gzHome.getMedicalTreatmentCompanyPay();
			Double CseriousIllness = gzHome.getSeriousIllnessCompanyPay();
			Double Caccumulation = gzHome.getAccumulationFundCompanyPay();
			Double serviceFee = gzHome.getServiceCharge();
			/*
			 * Double companyTotal = (Cpension+Cunemployment+Cinjury)+
			 * (Cprocreate+Cmedical)+(CseriousIllness+Caccumulation);
			 */
			Double companyTotal = gzHome.getCompanyTotal();

			Double radix = (Double.valueOf(accumulationRadix)) == null ? 0.00
					: Double.valueOf(accumulationRadix.trim());
			show.setAccumulationRadix(radix);
			show.setAccumulationCompanyTotal(Caccumulation);
			show.setAccumulationPersonTotal(Paccumulation);
			Double accumulationTotal = OwnHomeUtils.add(Paccumulation, Caccumulation);
			show.setAccumulationTotalFee(accumulationTotal);

			show.setCompanyTotal(personTotal);
			show.setPersonTotal(companyTotal);
			Double total = OwnHomeUtils.add(OwnHomeUtils.add(personTotal, companyTotal), serviceFee);
			show.setServiceFee(serviceFee);
			show.setTotal(total);
			gzDetailList.add(show);
		}
		return gzDetailList;
	}

	// 读取广州公积金
	@Transactional
	public List<GZAccumulation> readGZAccumulationFee(String filePath, Integer userId, Integer supplierId,
			String yearMonth, String city)
			throws EncryptedDocumentException, InvalidFormatException, FileNotFoundException, IOException {
		List<GZAccumulation> list = new ArrayList<GZAccumulation>();
		Workbook book = WorkbookFactory.create(new FileInputStream(new File(filePath)));
		Sheet sheet = book.getSheetAt(0);
		int num = getAccumulationCustomerLine(sheet);
		if (num < 0) {
			return new ArrayList();
		}
		int line = getAccumulationLine(sheet, num);
		if (line < 0) {
			return new ArrayList();
		}
		for (int i = line + 1; i < num; i++) {
			Row rowLine = sheet.getRow(i);
			if (rowLine == null) {
				continue;
			}
			for (int j = 0; j < rowLine.getLastCellNum(); j++) {
				if (rowLine.getCell(j) == null || rowLine.getCell(j).getCellTypeEnum().equals(CellType.BLANK)) {
					rowLine.getCell(j).setCellValue("");
					continue;
				}
				rowLine.getCell(j).setCellType(CellType.STRING);
			}
			String unitNumber = rowLine.getCell(0).getStringCellValue() == "" ? ""
					: rowLine.getCell(0).getStringCellValue().trim();
			String unitName = rowLine.getCell(1).getStringCellValue() == "" ? ""
					: rowLine.getCell(1).getStringCellValue().trim();
			String fundsSource = rowLine.getCell(2).getStringCellValue() == "" ? ""
					: rowLine.getCell(2).getStringCellValue().trim();
			String personNum = rowLine.getCell(3).getStringCellValue() == "" ? ""
					: rowLine.getCell(3).getStringCellValue().trim();
			String p_AccumulationNum = rowLine.getCell(4).getStringCellValue() == "" ? ""
					: rowLine.getCell(4).getStringCellValue().trim();
			String name = rowLine.getCell(5).getStringCellValue() == "" ? ""
					: rowLine.getCell(5).getStringCellValue().trim();
			String certificateType = rowLine.getCell(6).getStringCellValue() == "" ? ""
					: rowLine.getCell(6).getStringCellValue().trim();
			String certificateNum = rowLine.getCell(7).getStringCellValue() == "" ? ""
					: rowLine.getCell(7).getStringCellValue().trim();
			if (name == "" && certificateNum == "" && unitName == "") {
				break;
			}
			Double personPayRadix = rowLine.getCell(8).getStringCellValue() == "" ? 0.00
					: Double.valueOf(rowLine.getCell(8).getStringCellValue().trim());
			Double personFee = rowLine.getCell(9).getStringCellValue() == "" ? 0.00
					: Double.valueOf(rowLine.getCell(9).getStringCellValue().trim());
			Double unitFee = rowLine.getCell(10).getStringCellValue() == "" ? 0.00
					: Double.valueOf(rowLine.getCell(10).getStringCellValue().trim());
			Double accumulationBalance = rowLine.getCell(11).getStringCellValue() == "" ? 0.00
					: Double.valueOf(rowLine.getCell(11).getStringCellValue().trim());
			String payStatus = rowLine.getCell(12).getStringCellValue() == "" ? ""
					: rowLine.getCell(12).getStringCellValue().trim();

			GZAccumulation gzAccumulation = new GZAccumulation();
			gzAccumulation.setUnitNumber(unitNumber);
			gzAccumulation.setUnitName(unitName);
			gzAccumulation.setFundsSource(fundsSource);
			gzAccumulation.setPersonNum(personNum);
			gzAccumulation.setPersonAccumulationNum(p_AccumulationNum);
			gzAccumulation.setName(name);
			gzAccumulation.setCertificateType(certificateType);
			gzAccumulation.setCertificateNum(certificateNum);
			gzAccumulation.setPersonPayRadix(personPayRadix);
			gzAccumulation.setPersonFee(personFee);
			gzAccumulation.setUnitFee(unitFee);
			gzAccumulation.setAccumulationBalance(accumulationBalance);
			gzAccumulation.setPayStatus(payStatus);

			if (name != "" && name != null && certificateNum != "" && certificateNum != null) {
				list.add(gzAccumulation);
				costGzMapper.updateAccumulation(p_AccumulationNum, unitFee, personFee, supplierId, yearMonth,
						certificateNum, userId);
			}
		}
		return list;
	}

	// 判断公积金表头
	private Integer getAccumulationLine(Sheet sheet, Integer num) {
		int line = 0;
		for (int i = 1; i < num; i++) {
			Row rowLine = sheet.getRow(i);
			if (rowLine == null) {
				continue;
			}
			for (int j = 0; j < rowLine.getLastCellNum(); j++) {
				if (rowLine.getCell(j) == null || rowLine.getCell(j).getCellTypeEnum().equals(CellType.BLANK)) {
					rowLine.getCell(j).setCellValue("");
					continue;
				}
				rowLine.getCell(j).setCellType(CellType.STRING);
			}

			String[] strs = { "单位登记号", "单位名称", "资金来源", "个人编号", "个人公积金账号", "姓名", "证件类型", "证件号", "个人缴存基数", "个人缴存额",
					"单位缴存额", "公积金余额", "缴存状态" };
			Boolean[] flags = new Boolean[strs.length];
			for (int j = 0; j < strs.length; j++) {
				String cellValue = rowLine.getCell(j).getStringCellValue();
				if (strs[j].equals(cellValue)) {
					flags[j] = true;
				} else {
					flags[j] = false;
				}
			}
			if (flags != null && !(Arrays.toString(flags).contains("false"))) {
				line = rowLine.getRowNum();
				return line;
			} else {
				continue;
			}
		}
		return -1;
	}

	// 判断客服公积金模板表头
	private Integer getAccumulationCustomerLine(Sheet sheet) {
		int line = 0;
		for (int i = 1; i < sheet.getLastRowNum(); i++) {
			Row rowLine = sheet.getRow(i);
			if (rowLine == null) {
				continue;
			}
			for (int j = 0; j < rowLine.getLastCellNum(); j++) {
				if (rowLine.getCell(j) == null || rowLine.getCell(j).getCellTypeEnum().equals(CellType.BLANK)) {
					rowLine.getCell(j).setCellValue("");
					continue;
				}
				rowLine.getCell(j).setCellType(CellType.STRING);
			}
			String[] strs = { "序号", "姓名", "身份证号码", "缴存基数", "公司缴存比例", "公司月缴存额", "个人缴存比例", "个人月缴存额", "月缴存额合计", "系统中个人编号",
					"公司", "负责人" };
			Boolean[] flags = new Boolean[strs.length];
			for (int j = 0; j < strs.length; j++) {
				String cellValue = rowLine.getCell(j).getStringCellValue();
				if (strs[j].equals(cellValue)) {
					flags[j] = true;
				} else {
					flags[j] = false;
				}
			}
			if (flags != null && !(Arrays.toString(flags).contains("false"))) {
				line = rowLine.getRowNum();
				return line;
			} else {
				continue;
			}
		}
		return -1;
	}

	// 获取账单明细对象
	private ZhangdanMingxi getZhangdanMingxi(int zaiceId, CostGz gzHome) {
		ZhangdanMingxi zdmingxi = new ZhangdanMingxi();
		zdmingxi.setZaiceId(zaiceId);
		Double Ppension = gzHome.getPensionPersonPay();
		Double Punemployment = gzHome.getUnemploymentPersonPay();
		Double Pinjury = gzHome.getInjuryPersonPay();
		Double Pprocreate = gzHome.getProcreatePersonPay();
		Double PmedicalTreatment = gzHome.getMedicalTreatmentPersonPay();
		Double PseriousIllness = gzHome.getSeriousIllnessPersonPay();

		Double Cpension = gzHome.getPensionCompanyPay();
		Double Cunemployment = gzHome.getUnemploymentCompanyPay();
		Double Cinjury = gzHome.getInjuryCompanyPay();
		Double Cprocreate = gzHome.getProcreateCompanyPay();
		Double Cmedical = gzHome.getMedicalTreatmentCompanyPay();
		Double CseriousIllness = gzHome.getSeriousIllnessCompanyPay();

		zdmingxi.setShijiYanglaoGongsi(Cpension);
		zdmingxi.setShijiYanglaoGeren(Ppension);
		zdmingxi.setShijiJibenYiliaoGongsi(Cmedical);
		zdmingxi.setShijiJibenYiliaoGeren(PmedicalTreatment);
		zdmingxi.setShijiDabingYiliaoGongsi(CseriousIllness);
		zdmingxi.setShijiDabingYiliaoGeren(PseriousIllness);
		zdmingxi.setShijiShiyeGongsi(Cunemployment);
		zdmingxi.setShijiShiyeGeren(Punemployment);
		zdmingxi.setShijiGongshangGongsi(Cinjury);
		zdmingxi.setShijiShengyuGongsi(Cprocreate);

		Double Caccumulation = gzHome.getAccumulationFundCompanyPay();
		Double Paccumulation = gzHome.getAccumulationFundPersonPay();
		zdmingxi.setShijiGongjijinGongsi(Caccumulation);
		zdmingxi.setShijiGongjijinGeren(Paccumulation);
		zdmingxi.setShijiQitaGongsi(0.00);
		zdmingxi.setShijiQitaGeren(0.00);

		Double serviceFee = gzHome.getServiceCharge();
		zdmingxi.setShijiFuwufei(serviceFee);
		return zdmingxi;
	}

	// 广州公积金导出
	@SuppressWarnings("deprecation")
	public String writeGZAccumulationFee(Integer supplierId, String yearMonth) throws IOException {
		String city = "广州";
		String supplier = zaiceService.selectSupplierBySupplierId(city, supplierId);
		if (supplier == null) {
			supplier = "数据为空！";
		}
		Workbook wb = new HSSFWorkbook();
		HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
		String[] strs = { "序号", "姓名", "身份证号码", "缴存基数", "公司缴存比例", "公司月缴存额", "个人缴存比例", "个人月缴存额", "月缴存额合计", "系统中个人编号",
				"公司", "负责人" };
		HSSFSheet sheet = (HSSFSheet) wb.createSheet(supplierId + "号供应商" + yearMonth + "月(广州)公积金明细");

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

		HSSFRow rows = sheet.createRow((int) 0);
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
			rows.getCell(0).setCellValue(yearMonth + "月(广州)-" + supplier + "公积金明细");
		}
		// 合并单元格
		CellRangeAddress cra = new CellRangeAddress(0, (short) 0, 1, (short) (rows.getLastCellNum() - 1)); // 起始行,
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
			sheet.setColumnWidth(i, (short) (13 * 256));
			cell.setCellStyle(stylet);
			cell.setCellValue(strs[i]);
		}

		List<CostGz> gzList = costGzMapper.selectGZhomeAllBySupplierId(supplierId, yearMonth);
		List<GZExportAccumulation> gzAccumulations = new ArrayList<GZExportAccumulation>();
		if (gzList == null) {
			return null;
		}
		Integer supplierIds = null;
		String accumulationNumber = null;
		String accumulationRadix = null;
		String accumulationRatio = null;
		for (CostGz gzHome : gzList) {
			String month = gzHome.getReportingPeriod();
			String certificateNum = gzHome.getCertificateNumber();
			if (supplierIds == null || month == null || month == "" || certificateNum == null || certificateNum == "") {
				break;
			}
			Zaice zaice = zaiceService.findAccumulationInfos(city, month, certificateNum);
			if (zaice == null) {
				accumulationNumber = "账号为空!请检查数据";
				accumulationRadix = "0";
				accumulationRatio = "0%";
			} else {
				accumulationNumber = zaice.getAccumulationFundNumber();
				accumulationRadix = zaice.getAccumulationFundCardinalNumber();
				accumulationRatio = zaice.getAccumulationFundRatio();
			}
			GZExportAccumulation gzExportAccumulation = new GZExportAccumulation();

			gzExportAccumulation.setName(gzHome.getName());
			gzExportAccumulation.setCertificateNum(certificateNum);
			Double payRadix = Double.valueOf((accumulationRadix.trim()));
			gzExportAccumulation.setPayRadix(payRadix);

			int index = accumulationRatio.indexOf("%");
			int indexs = accumulationRatio.indexOf("+");
			String c_ratio = accumulationRatio.substring(0, index);
			String p_ratio = accumulationRatio.substring(indexs + 1, (accumulationRatio.trim().length()) - 1);
			Double c_payRatio = Double.valueOf((c_ratio.replace("%", "").trim()));
			Double p_payRatio = Double.valueOf((p_ratio.replace("%", "").trim()));
			gzExportAccumulation.setUnitRatio(c_payRatio / 100.00);
			Double c_payMoney = OwnHomeUtils.mul(payRadix, (c_payRatio / 100.00));
			Double p_payMoney = OwnHomeUtils.mul(payRadix, (p_payRatio / 100.00));
			gzExportAccumulation.setUnitMonthFee(c_payMoney);
			gzExportAccumulation.setPersonRatio(p_payRatio / 100.00);
			gzExportAccumulation.setPersonMonthFee(p_payMoney);
			gzExportAccumulation.setTotalFee(OwnHomeUtils.add(c_payMoney, p_payMoney));
			gzExportAccumulation.setPersonNumber(accumulationNumber);

			String customerNmae = customerService.findCustomerName(gzHome.getCustomerId());
			if (customerNmae == null) {
				customerNmae = "********";
			}
			gzExportAccumulation.setCompany(customerNmae);
			String userName = userService.findUserName(gzHome.getUserId());
			if (userName == null) {
				userName = "数据异常!";
			}
			gzExportAccumulation.setManagerName(userName);

			if (gzHome.getName() != null && certificateNum != null) {
				gzAccumulations.add(gzExportAccumulation);
			}
		}

		for (int rowNum = 2; rowNum <= gzAccumulations.size() + 1; rowNum++) {
			HSSFRow row = sheet.createRow(rowNum);
			row.setHeightInPoints(20);
			HSSFCell cell = row.createCell(0, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(String.valueOf(rowNum - 1));
			cell = row.createCell(1, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(gzAccumulations.get(rowNum - 2).getName());
			cell = row.createCell(2, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(gzAccumulations.get(rowNum - 2).getCertificateNum());
			cell = row.createCell(3, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(gzAccumulations.get(rowNum - 2).getPayRadix());
			cell = row.createCell(4, CellType.STRING);
			cell.setCellStyle(style);
			int unitRatio = (int) ((gzAccumulations.get(rowNum - 2).getUnitRatio()) * 100);
			cell.setCellValue(String.valueOf(unitRatio) + "%");
			cell = row.createCell(5, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(gzAccumulations.get(rowNum - 2).getUnitMonthFee());
			cell = row.createCell(6, CellType.STRING);
			cell.setCellStyle(style);
			int personRatio = (int) ((gzAccumulations.get(rowNum - 2).getPersonRatio()) * 100);
			cell.setCellValue(String.valueOf(personRatio) + "%");
			cell = row.createCell(7, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(gzAccumulations.get(rowNum - 2).getPersonMonthFee());
			cell = row.createCell(8, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(gzAccumulations.get(rowNum - 2).getTotalFee());
			cell = row.createCell(9, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(gzAccumulations.get(rowNum - 2).getPersonNumber());
			cell = row.createCell(10, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(gzAccumulations.get(rowNum - 2).getCompany());
			cell = row.createCell(11, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(gzAccumulations.get(rowNum - 2).getManagerName());
			for (int i = 0; i < 12; i++) {
				sheet.setColumnWidth(i, (short) (16 * 256));
				if (i == 2 || i == 10 || i == 9) {
					sheet.autoSizeColumn(i);
				}
			}
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String excelPath = request.getSession().getServletContext().getRealPath("/") + "download" + File.separator
				+ "excel" + File.separator + supplierId + File.separator;
		// String path =
		// "E:"+File.separator+"ownhomeInfo"+File.separator+supplierId+File.separator;
		// System.out.println("==============================================================写入成功");
		String filePath1 = excelPath + "编号" + supplierId + "的供应商" + yearMonth + "月公积金明细.xls";
		// String filePath2 =
		// path+"编号"+supplierId+"的供应商"+yearMonth+"月公积金明细.xls";
		File file1 = new File(excelPath);
		// File file2 = new File(path);
		if (!file1.exists() && !file1.isDirectory()) {
			file1.mkdirs();
		}
		/*
		 * if(!file2.exists() && !file2.isDirectory()){ file2.mkdirs(); }
		 */
		File files1 = new File(filePath1);
		// File files2 = new File(filePath2);
		if (!files1.exists() && !files1.isFile()) {
			files1.createNewFile();
		}
		/*
		 * if(!files2.exists() && !files2.isFile()){ files2.createNewFile(); }
		 */
		FileOutputStream out1 = null;
		// FileOutputStream out2=null;
		try {
			out1 = new FileOutputStream(files1);
			// out2 = new FileOutputStream(files2);
			wb.write(out1);
			// wb.write(out2);
			out1.flush();
			// out2.flush();
			return filePath1;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (out1 != null) {
				out1.close();
			}
			out1 = null;
			// out2=null;
		}
	}

	// 广州费用导出
	public String writeGZInfoAll(Integer supplierId, String yearMonth) throws IOException {
		String city = "广州";
		String supplier = zaiceService.selectSupplierBySupplierId(city, supplierId);
		Workbook wb = new HSSFWorkbook();
		HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
		HSSFSheet sheet = (HSSFSheet) wb.createSheet(supplierId + "号供应商" + yearMonth + "月(广州)费用明细");
		// 设置单元格内容垂直对其方式为居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
		style.setAlignment(HorizontalAlignment.CENTER);// 水平 // 创建一个居中格式

		String[] strs = { "姓名", "身份证号码", "个人社保号", "基本养老保险", "失业保险", "工伤保险", "生育保险", "职工社会医疗保险", "职工重大疾病医疗补助", "住房公积金",
				"残保金", "服务费", "单位", "个人", "总计" };
		String[] strArrs = { "个人", "单位", "基数", "个人", "单位", "基数", "个人", "单位", "基数", "个人", "单位", "基数", "个人", "单位", "基数",
				"个人", "单位", "基数", "基数", "公司", "个人", "合计" };
		// 设置默认行高,列宽
		// sheet.setDefaultRowHeight((short)(20 * 256));
		sheet.setDefaultRowHeightInPoints(20);
		sheet.setDefaultColumnWidth(13);
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
		for (int y = 0; y < strArrs.length + 8; y++) {
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
			rows.getCell(0).setCellValue(yearMonth + "月(广州)-" + supplier + "费用明细");
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
		for (int i = 0; i < (strArrs.length) + 8; i++) {
			HSSFCellStyle stylet = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
			stylet.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
			stylet.setAlignment(HorizontalAlignment.CENTER); // 水平 居中格式
			HSSFFont fontt = (HSSFFont) wb.createFont();
			fontt.setBold(true);
			stylet.setFont(fontt);

			HSSFCell ce = row.createCell(i);
			sheet.setColumnWidth(i, (short) (13 * 256));
			ce.setCellStyle(stylet);
			if (i < 4) {
				ce.setCellValue(strs[i]); // 表格的第二行第一列显示的数据
			} else if (i == 6) {
				ce.setCellValue(strs[4]);
			} else if (i == 9) {
				ce.setCellValue(strs[5]);
			} else if (i == 12) {
				ce.setCellValue(strs[6]);
			} else if (i == 15) {
				ce.setCellValue(strs[7]);
			} else if (i == 18) {
				ce.setCellValue(strs[8]);
			} else if (i == 21) {
				ce.setCellValue(strs[9]);
			} else if (i == 25) {
				ce.setCellValue(strs[10]);
			} else if (i == 26) {
				ce.setCellValue(strs[11]);
			} else if (i == 27) {
				ce.setCellValue(strs[12]);
			} else if (i == 28) {
				ce.setCellValue(strs[13]);
			} else if (i == 29) {
				ce.setCellValue(strs[14]);
			} else {
				ce.setCellValue("");
			}
			HSSFCell ces = null;
			if (i >= 3 && i < (strArrs.length) + 3) {
				ces = row2.createCell(i);
				sheet.setColumnWidth(i, (short) (13 * 256));
				ces.setCellStyle(stylet);
				ces.setCellValue(strArrs[i - 3]);
			} else {
				ces = row2.createCell(i);
				sheet.setColumnWidth(i, (short) (13 * 256));
				ces.setCellStyle(stylet);
				ces.setCellValue("");
			}
			if (i == 1 || i == 2) {
				sheet.autoSizeColumn(i); // 自动调整宽度
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 0, (short) 0));// 设置单元格合并
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 1, (short) 1));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 2, (short) 2));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 3, (short) 5));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 6, (short) 8));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 9, (short) 11));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 12, (short) 14));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 15, (short) 17));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 18, (short) 20));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 21, (short) 24));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 25, (short) 25));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 26, (short) 26));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 27, (short) 27));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 28, (short) 28));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 29, (short) 29));

		List<CostGz> list = costGzMapper.selectGZhomeAllBySupplierId(supplierId, yearMonth);
		List<GZDetailShow> detailList = getGZShows(list);
		if (detailList == null || list == null) {
			return null;
		}
		for (int i = 3; i < (detailList.size() + 3); i++) {
			List ll = new ArrayList();
			ll.add(detailList.get(i - 3).getName());
			ll.add(detailList.get(i - 3).getCertificateNumber());
			ll.add(detailList.get(i - 3).getSocialSecurityNumber());

			ll.add(detailList.get(i - 3).getPensionPersonPay());
			ll.add(detailList.get(i - 3).getPensionCompanyPay());
			ll.add(detailList.get(i - 3).getPensionRadix());

			ll.add(detailList.get(i - 3).getUnemploymentPersonPay());
			ll.add(detailList.get(i - 3).getUnemploymentCompanyPay());
			ll.add(detailList.get(i - 3).getUnemploymentRadix());

			ll.add(detailList.get(i - 3).getInjuryPersonPay());
			ll.add(detailList.get(i - 3).getInjuryCompanyPay());
			ll.add(detailList.get(i - 3).getInjuryCompanyRadix());

			ll.add(detailList.get(i - 3).getProcreatePersonPay());
			ll.add(detailList.get(i - 3).getProcreateCompanyPay());
			ll.add(detailList.get(i - 3).getProcreateCompanyRadix());

			ll.add(detailList.get(i - 3).getMedicalTreatmentPersonPay());
			ll.add(detailList.get(i - 3).getMedicalTreatmentCompanyPay());
			ll.add(detailList.get(i - 3).getMedicalTreatmentRadix());

			ll.add(detailList.get(i - 3).getSeriousIllnessPersonPay());
			ll.add(detailList.get(i - 3).getSeriousIllnessCompanyPay());
			ll.add(detailList.get(i - 3).getSeriousIllnessRedix());

			ll.add(detailList.get(i - 3).getAccumulationRadix());
			ll.add(detailList.get(i - 3).getAccumulationCompanyTotal());
			ll.add(detailList.get(i - 3).getAccumulationPersonTotal());
			ll.add(detailList.get(i - 3).getAccumulationTotalFee());

			ll.add(detailList.get(i - 3).getDisabilityBenefit());
			ll.add(detailList.get(i - 3).getServiceFee());
			ll.add(detailList.get(i - 3).getCompanyTotal());
			ll.add(detailList.get(i - 3).getPersonTotal());
			ll.add(detailList.get(i - 3).getTotal());

			HSSFRow rowLine = sheet.createRow((short) i);
			rowLine.setHeightInPoints(20);
			sheet.setDefaultColumnWidth(15);
			for (int x = 0; x < (strArrs.length + 8); x++) {
				HSSFCell cc = null;
				if (x >= 0 && x < 3) {
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
				if (x == 1 || i == 2) {
					sheet.autoSizeColumn(x); // 自动调整宽度
				}
			}

		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String excelPath = request.getSession().getServletContext().getRealPath("/") + "download" + File.separator
				+ "excel" + File.separator + supplierId + File.separator;
		// String path =
		// "E:"+File.separator+"ownhomeInfo"+File.separator+supplierId+File.separator;
		// System.out.println("==============================================================写入成功");
		String filePath1 = excelPath + "编号" + supplierId + "的供应商" + yearMonth + "月社保费用明细.xls";
		// String filePath2 =
		// path+"编号"+supplierId+"的供应商"+yearMonth+"月社保费用明细.xls";
		File file1 = new File(excelPath);
		// File file2 = new File(path);
		if (!file1.exists() && !file1.isDirectory()) {
			file1.mkdirs();
		}
		/*
		 * if(!file2.exists() && !file2.isDirectory()){ file2.mkdirs(); }
		 */
		File files1 = new File(filePath1);
		// File files2 = new File(filePath2);
		if (!files1.exists() && !files1.isFile()) {
			files1.createNewFile();
		}
		/*
		 * if(!files2.exists() && !files2.isFile()){ files2.createNewFile(); }
		 */
		FileOutputStream out1 = null;
		// FileOutputStream out2=null;
		try {
			out1 = new FileOutputStream(files1);
			// out2 = new FileOutputStream(files2);
			wb.write(out1);
			// wb.write(out2);
			out1.flush();
			/// out2.flush();
			return filePath1;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (out1 != null) {
				out1.close();
			}
			out1 = null;
			// out2=null;
		}
	}

	// 批量删除广州自有户信息
	public String deleteGZInfoAll(Integer supplierId, String yearMonth) {
		int a = -1;
		List<CostGz> gzList = costGzMapper.selectGZhomeAllBySupplierId(supplierId, yearMonth);
		a = costGzMapper.deleteGZhomeDetails(supplierId, yearMonth);
		if (a > 0 && a == (gzList.size())) {
			return "删除成功！";
		}
		return null;
	}

}
