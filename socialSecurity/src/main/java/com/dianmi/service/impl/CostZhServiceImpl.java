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
import com.dianmi.mapper.CostZhMapper;
import com.dianmi.model.CostZh;
import com.dianmi.model.Zaice;
import com.dianmi.model.ZhangdanMingxi;
import com.dianmi.model.accumulation.ZHAccumulation;
import com.dianmi.model.accumulation.ZHExportAccumulation;
import com.dianmi.model.owndetailshow.ZHDetailShow;
import com.dianmi.model.ownhome.ZHShow;
import com.dianmi.model.po.ZaiceCustomerMsg;
import com.dianmi.service.CostZhService;
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
public class CostZhServiceImpl extends CommonService implements CostZhService {

	// 导入珠海社保费用
	@Transactional
	public ResultJson importCost(MultipartFile file, Integer userId, Integer supplierId, String yearMonth) {
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
		int startLine = getSocialSecurityStartLine(list);
		if (!isZhuhaiSocialSecurityTemplate(list.get(startLine)))
			return ResultUtil.error(RestEnum.FAILD, "请上传珠海社保模板");
		String paymentMonth = list.get(startLine - 1)[5];
		int result = 0;
		List<String> allCertificateNumberList = new ArrayList<String>();// 所有读取的身份证号码集合
		for (int i = startLine + 2; i < list.size() - 1; i++) {
			List<String> strList = Arrays.asList(list.get(i));
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
			Double unemploymentRadix1 = Double.valueOf(strList.get(10)); // 默认0.00，农民失业保险缴纳基数
			Double unemploymentCompanyPay1 = Double.valueOf(strList.get(11));// 失业单位缴费
			Double unemploymentPersonPay1 = Double.valueOf(strList.get(12));// 失业个人缴费
			Double unemploymentRadix2 = Double.valueOf(strList.get(13)); // 默认0.00，城镇失业保险缴纳基数
			Double unemploymentCompanyPay2 = Double.valueOf(strList.get(14));// 失业单位缴费
			Double unemploymentPersonPay2 = Double.valueOf(strList.get(15));// 失业个人缴费
			Double medicalTreatmentRadix = Double.valueOf(strList.get(16)); // 默认0.00，医疗缴费基数
			Double medicalTreatmentCompanyPay = Double.valueOf(strList.get(17));// 医疗单位缴费
			Double medicalTreatmentPersonPay = Double.valueOf(strList.get(18));// 医疗个人缴费
			Double seriousIllnessRedix = Double.valueOf(strList.get(19)); // 大病缴费基数
			Double seriousIllnessCompanyPay = Double.valueOf(strList.get(20));// 大病单位缴费
			Double seriousIllnessPersonPay = Double.valueOf(strList.get(21));// 大病个人缴费
			Double procreateCompanyRadix = Double.valueOf(strList.get(22)); // 默认0.00，生育保险企业缴费基数
			Double procreateCompanyPay = Double.valueOf(strList.get(23));// 生育单位缴费
			Double procreatePersonPay = Double.valueOf(strList.get(24));// 生育个人缴费
			Double companyTotal = Double.valueOf(strList.get(25)); // 默认0.00，公司部分合计
			Double personTotal = Double.valueOf(strList.get(26)); // 默认0.00，个人部分合计
			Double total = Double.valueOf(strList.get(27)); // 默认0.00，合计应缴金额
			allCertificateNumberList.add(certificateNumber);
			ZaiceCustomerMsg customerMsg = zaiceMsg(yearMonth, ZHUHAI, certificateNumber);
			CostZh costZh = new CostZh();
			costZh.setUserId(userId);
			costZh.setCustomerId(customerMsg.getCustomerId());
			costZh.setSupplierId(supplierId);
			costZh.setCustomerName(customerMsg.getCustomerName());
			costZh.setReportingPeriod(yearMonth);
			costZh.setName(name);
			costZh.setPaymentMonth(paymentMonth);
			costZh.setCertificateNumber(certificateNumber);
			costZh.setCertificateName(certificateName);
			costZh.setSocialSecurityNumber(socialSecurityNumber);
			costZh.setPensionRadix(pensionRadix);
			costZh.setPensionCompanyPay(pensionCompanyPay);
			costZh.setPensionPersonPay(pensionPersonPay);
			costZh.setInjuryCompanyRadix(injuryCompanyRadix);
			costZh.setInjuryCompanyPay(injuryCompanyPay);
			costZh.setInjuryPersonPay(injuryPersonPay);
			// 农民工失业保险基数为0
			if (unemploymentRadix1 != 0) {
				costZh.setUnemploymentRadix(unemploymentRadix1);
				costZh.setUnemploymentCompanyPay(unemploymentCompanyPay1);
				costZh.setUnemploymentPersonPay(unemploymentPersonPay1);
				// 城镇工失业保险基数为0
			} else if (unemploymentRadix2 != 0) {
				costZh.setUnemploymentRadix(unemploymentRadix2);
				costZh.setUnemploymentCompanyPay(unemploymentCompanyPay2);
				costZh.setUnemploymentPersonPay(unemploymentPersonPay2);
			} else {
				costZh.setUnemploymentRadix(0.0);
				costZh.setUnemploymentCompanyPay(0.0);
				costZh.setUnemploymentPersonPay(0.0);
			}
			costZh.setMedicalTreatmentRadix(medicalTreatmentRadix);
			costZh.setMedicalTreatmentCompanyPay(medicalTreatmentCompanyPay);
			costZh.setMedicalTreatmentPersonPay(medicalTreatmentPersonPay);
			costZh.setSeriousIllnessRedix(seriousIllnessRedix);
			costZh.setSeriousIllnessCompanyPay(seriousIllnessCompanyPay);
			costZh.setSeriousIllnessPersonPay(seriousIllnessPersonPay);
			costZh.setProcreateCompanyRadix(procreateCompanyRadix);
			costZh.setProcreateCompanyPay(procreateCompanyPay);
			costZh.setProcreatePersonPay(procreatePersonPay);
			costZh.setCompanyTotal(companyTotal);
			costZh.setPersonTotal(personTotal);
			costZh.setSocialSecurityTotal(total);
			costZh.setAccumulationFundRadix(0.0);
			costZh.setAccumulationFundRatio(null);
			costZh.setAccumulationFundCompanyPay(0.0);
			costZh.setAccumulationFundPersonPay(0.0);
			costZh.setAccumulationFundTotal(0.0);
			costZh.setSalaryBeginPaymentMonth(null);
			costZh.setBeginInjoyMonth(null);
			costZh.setAccumulationFundAccount(null);
			costZh.setManager(null);
			costZh.setManager(null);
			// 判断费用是否已经导入
			if (isCostImport(yearMonth, certificateNumber)) {
				// 更新珠海费用信息
				costZhMapper.updateCostZh(costZh);
				result += 1;
			} else {
				// 新增珠海费用信息
				costZhMapper.insertSelective(costZh);
				result += 1;
			}
			// 账单明细信息更新
			zhangdanService.updateZhangdanByZaiceId(getSupplierDetail(customerMsg.getZaiceId(), costZh));
			// 确定增员成功
			jianyuanService.updateMinusSocialStatus(certificateNumber, ZHUHAI, yearMonth);
			zaiceService.updateAddSocialStatus(yearMonth, ZHUHAI, certificateNumber);
		}
		List<String> allCertificateNumber = zengyuanService.getAllCertificateNumber(yearMonth, supplierId);// 当前月份下按供应商分类的所有身份证号码集合
		List<String> newZengyuanList = new ArrayList<String>(allCertificateNumber);
		newZengyuanList.removeAll(allCertificateNumberList);
		updateToZengyuanFailed(newZengyuanList, yearMonth, ZHUHAI);
		List<String> allJianyuanCertificateNumber = jianyuanService.getAllCertificateNumber(yearMonth, supplierId);
		List<String> jianyuanFaildList = new ArrayList<String>(allJianyuanCertificateNumber);
		jianyuanFaildList.removeAll(newZengyuanList);
		updateToJianyuanFailed(jianyuanFaildList, yearMonth, ZHUHAI);
		// 减员成功的用户信息
		List<String> jianyuanSuccessList = new ArrayList<String>(allJianyuanCertificateNumber);
		jianyuanSuccessList.removeAll(jianyuanFaildList);
		updateToJianyuanSuccess(jianyuanSuccessList, yearMonth, ZHUHAI);
		return ResultUtil.success(RestEnum.SUCCESS, "处理：" + result + "条");
	}
	
	/**
	 * @param strArr
	 * @return 判断是否是珠海社保模板
	 */
	public boolean isZhuhaiSocialSecurityTemplate(String[] strArr) {
		if (strArr[0].contains("姓名") && strArr[1].contains("身份证明号码") && strArr[2].contains("证件名称")
				&& strArr[3].contains("个人社保号")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param list
	 * @return 返回社保数据起始行号
	 */
	private int getSocialSecurityStartLine(List<String[]> list) {
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
		if (costZhMapper.isCostImport(yearMonth, certificateNumber).size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * 導入公積金
	 */
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
		DeleteFile.DeleteFolder(filePath);// 上传成功删除Excel模板
		if (list.isEmpty())
			return ResultUtil.error(RestEnum.FILE_DATA_IS_NULL);
		int startLine = getAccumulationFundStartLine(list);
		if (!isZhuhaiAccumulationFundTemplate(list.get(startLine)))
			return ResultUtil.error(RestEnum.FAILD, "请上传珠海公积金模板");
		int result = 0;
		for (int j = startLine + 1; j < list.size(); j++) {
			String[] strArr = list.get(j);
			String name = strArr[1];
			String certificateNumber = strArr[2].replaceAll("x", "X");// 身份证号码
			double accumulationFundRadix = Double.parseDouble(strArr[3]);// 购买基数
			String accumulationFundRatio = strArr[4];
			double accumulationFundCompanyPay = Double.parseDouble(strArr[5]);
			double accumulationFundPersonPay = Double.parseDouble(strArr[6]);
			double accumulationFundTotal = Double.parseDouble(strArr[7]);
			String salaryBeginPaymentMonth = strArr[8];
			String beginInjoyMonth = strArr[9];
			String accumulationFundAccount = strArr[10];
			String company = strArr[11];
			String manager = strArr[12];
			ZaiceCustomerMsg customerMsg = zaiceMsg(yearMonth, ZHUHAI, certificateNumber);
			CostZh costZh = new CostZh( userId,  customerMsg.getCustomerId(),  supplierId,  customerMsg.getCustomerName(),  yearMonth,
					 name,  certificateNumber,  accumulationFundRadix,  accumulationFundRatio,
					 accumulationFundCompanyPay,  accumulationFundPersonPay,  accumulationFundTotal,
					 salaryBeginPaymentMonth,  beginInjoyMonth,  accumulationFundAccount,  company,
					 manager);
			if(isCostImport(yearMonth,certificateNumber)){
				costZhMapper.updateAccumulationFund(costZh);
				result += 1;
			}else{
				costZhMapper.insertSelective(costZh);
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
	 * @return
	 */
	public boolean isZhuhaiAccumulationFundTemplate(String[] strArr) {
		if (strArr[0].contains("序号") && strArr[1].contains("姓名") && strArr[2].contains("身份证号码")
				&& strArr[3].contains("购买基数") && strArr[4].contains("比例") && strArr[5].contains("公司金额")
				&& strArr[6].contains("个人金额") && strArr[7].contains("合计") && strArr[8].contains("工资始扣月")
				&& strArr[9].contains("始参月份") && strArr[10].contains("帐号") && strArr[11].contains("公司")
				&& strArr[12].contains("负责人")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param list
	 * @return 返回公积金缴费起始行号
	 */
	private int getAccumulationFundStartLine(List<String[]> list) {
		int startLine = 0;
		for (int i = 0; i < list.size(); i++) {
			String[] strArr = list.get(i);
			if (strArr.length > 4)
				if (!StringUtil.isEmpty(strArr[0]) && !StringUtil.isEmpty(strArr[1]) && !StringUtil.isEmpty(strArr[2])
						&& !StringUtil.isEmpty(strArr[3]) && !StringUtils.isEmpty(strArr[4]))
					if ("序号".equals(strArr[0]) && "姓名".equals(strArr[1]) && "身份证号码".equals(strArr[2])
							&& "购买基数".equals(strArr[3]) && "比例".equals(strArr[4]))
						return startLine = i;
		}
		return startLine;
	}

	// 珠海
	private Boolean getUpdateZHAddResult(CostZhMapper costZhMapper, String city, Integer supplierId, String month,
			String certificateNumber) {
		Boolean flag = false;
		city = city.trim();
		certificateNumber = certificateNumber.trim();
		month = month.trim();
		String addEmployeeName = costZhMapper.selectEmployeeName(supplierId, month, certificateNumber);
		if (addEmployeeName != null && addEmployeeName != "") {
			flag = true;
		}
		return flag;
	}

	private Boolean getUpdateZHMinusResult(CostZhMapper costZhMapper, String city, Integer supplierId, String month,
			String certificateNumber) {
		Boolean flag = false;
		city = city.trim();
		certificateNumber = certificateNumber.trim();
		month = month.trim();
		String minusEmployeeName = costZhMapper.selectEmployeeName(supplierId, month, certificateNumber);
		if (minusEmployeeName == null) {
			flag = true;
		}
		return flag;
	}

	// 分页对象
	private List<ZHShow> getZHShowInfo(List<CostZh> list) {
		String city = "珠海";
		List<ZHShow> showList = new ArrayList<ZHShow>();
		for (CostZh zhHome : list) {
			ZHShow shows = new ZHShow();
			Map<String, String> strs = customerService.findCustomerInfo(zhHome.getCustomerId());
			shows.setReportingPeriod((zhHome.getReportingPeriod() == null ? "" : zhHome.getReportingPeriod()));
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

			Double Ppension = zhHome.getPensionPersonPay();
			Double Punemployment = zhHome.getUnemploymentPersonPay();
			Double Pinjury = zhHome.getInjuryPersonPay();
			Double Pprocreate = zhHome.getProcreatePersonPay();
			Double PmedicalTreatment = zhHome.getMedicalTreatmentPersonPay();
			Double PseriousIllness = zhHome.getSeriousIllnessPersonPay();
			// Double personFee =
			// (Ppension+Punemployment+Pinjury)+(Pprocreate+PmedicalTreatment+PseriousIllness);
			Double personFee = zhHome.getPersonTotal();

			Double Cpension = zhHome.getPensionCompanyPay();
			Double Cunemployment = zhHome.getUnemploymentCompanyPay();
			Double Cinjury = zhHome.getInjuryCompanyPay();
			Double Cprocreate = zhHome.getProcreateCompanyPay();
			Double Cmedical = zhHome.getMedicalTreatmentCompanyPay();
			Double CseriousIllness = zhHome.getSeriousIllnessCompanyPay();
			// Double companyFee =
			// (Cpension+Cunemployment+Cinjury)+(Cprocreate+Cmedical+CseriousIllness);
			Double companyFee = zhHome.getCompanyTotal();

			Double pensionFee = OwnHomeUtils.add(Ppension, Cpension);
			shows.setPensionSocial(pensionFee);
			Double injuryFee = OwnHomeUtils.add(Pinjury, Cinjury);
			shows.setInjurySocial(injuryFee);

			String houseType = zaiceService.selectHouseType(city, zhHome.getReportingPeriod(),
					zhHome.getCertificateName());
			if (houseType == null) {
				houseType = "数据异常！";
			}
			Double unemploymentFee = OwnHomeUtils.add(Punemployment, Cunemployment);
			if (houseType.contains("农村") || houseType.contains("农业")) {
				shows.setPeasantryUnemploymentSocial(unemploymentFee);
				shows.setCityUnemploymentSocial(0.00);
			} else if (houseType.contains("城镇")) {
				shows.setCityUnemploymentSocial(unemploymentFee);
				shows.setPeasantryUnemploymentSocial(0.00);
			}
			Double medicalFee = OwnHomeUtils.add(PmedicalTreatment, Cmedical);
			shows.setMedicaltreatmentSocial(medicalFee);
			Double seriousIllnessFee = OwnHomeUtils.add(PseriousIllness, CseriousIllness);
			shows.setSeriousIllnessSocial(seriousIllnessFee);
			Double procreateFee = OwnHomeUtils.add(Pprocreate, Cprocreate);
			shows.setProcreateSocial(procreateFee);

			shows.setPersonSocialTotal(personFee);
			shows.setCompanySocialTotal(companyFee);
			Double socialTotal = OwnHomeUtils.add(personFee, companyFee);
			shows.setSocialTotalPay(socialTotal);

			Double Caccumulation = zhHome.getAccumulationFundCompanyPay();
			Double Paccumulation = zhHome.getAccumulationFundPersonPay();
			Double serviceFee = zhHome.getServiceCharge();
			shows.setCompanyAccumulation(Caccumulation);
			shows.setPersonAccumulation(Paccumulation);
			shows.setServiceFee(serviceFee);
			Double accumulationTotalFee = OwnHomeUtils.add(Caccumulation, Paccumulation);
			shows.setAccumulationTotalPay(accumulationTotalFee);

			Double Total = OwnHomeUtils.add(OwnHomeUtils.add(socialTotal, accumulationTotalFee), serviceFee);
			shows.setTotalFee(Total);
			showList.add(shows);
		}
		return showList;
	}

	// 珠海分页
	public List selectZHhomeAll(Integer supplierId, String month, Integer currPage, Integer pageSize) {

		List<CostZh> list = costZhMapper.selectZHhomeAll(supplierId, month, (currPage - 1), pageSize);
		if (list == null) {
			return new ArrayList();
		}
		PageHelper.startPage(currPage, pageSize);
		List<ZHShow> showList = getZHShowInfo(list);
		PageInfo<ZHShow> pageList = new PageInfo<ZHShow>(showList);
		// pageList.getTotal();

		return pageList.getList();
	}

	// 珠海详情展示
	public List selectZHInfoAll(Integer supplierId, String yearMonth, Integer currPage, Integer pageSize) {

		List<CostZh> list = costZhMapper.selectZHhomeAll(supplierId, yearMonth, (currPage - 1), pageSize);
		if (list == null) {
			return new ArrayList();
		}
		PageHelper.startPage(currPage, pageSize);
		List<ZHDetailShow> zhDetailList = getZHShows(list);
		if (zhDetailList == null) {
			return new ArrayList();
		}

		PageInfo<ZHDetailShow> pageList = new PageInfo<ZHDetailShow>(zhDetailList);
		return pageList.getList();
	}

	// 详情对象
	private List<ZHDetailShow> getZHShows(List<CostZh> list) {
		String city = "珠海";
		List<ZHDetailShow> zhDetailList = new ArrayList<ZHDetailShow>();
		String accumulationRadix = null;
		for (CostZh zhHome : list) {
			Zaice zaice = zaiceService.findAccumulationInfos(city, zhHome.getReportingPeriod(),
					zhHome.getCertificateNumber());
			if (zaice == null) {
				accumulationRadix = "0";
			} else {
				accumulationRadix = zaice.getAccumulationFundCardinalNumber();
			}
			ZHDetailShow show = new ZHDetailShow();
			show.setName(zhHome.getName());
			show.setCertificateNumber(zhHome.getCertificateName());
			show.setSocialSecurityNumber(zhHome.getSocialSecurityNumber());

			show.setPensionPersonPay(zhHome.getPensionPersonPay());
			show.setPensionCompanyPay(zhHome.getPensionCompanyPay());
			show.setPensionRadix(zhHome.getPensionRadix());

			String houseType = zaiceService.selectHouseType(city, zhHome.getReportingPeriod(),
					zhHome.getCertificateName());
			if (houseType == null) {
				houseType = "数据异常！";
			}
			if (houseType.contains("农村") || houseType.contains("农业")) {
				show.setFarmersUnemploymentPersonPay(zhHome.getUnemploymentPersonPay());
				show.setFarmersUnemploymentCompanyPay(zhHome.getUnemploymentCompanyPay());
				show.setFarmersUnemploymentRadix(zhHome.getUnemploymentRadix());
			} else if (houseType.contains("城镇")) {
				show.setCityUnemploymentPersonPay(zhHome.getUnemploymentPersonPay());
				show.setCityUnemploymentCompanyPay(zhHome.getUnemploymentCompanyPay());
				show.setCityUnemploymentRadix(zhHome.getUnemploymentRadix());
			}

			show.setInjuryPersonPay(zhHome.getInjuryPersonPay());
			show.setInjuryCompanyPay(zhHome.getInjuryCompanyPay());
			show.setInjuryCompanyRadix(zhHome.getInjuryCompanyRadix());

			show.setProcreatePersonPay(zhHome.getProcreatePersonPay());
			show.setProcreateCompanyPay(zhHome.getProcreateCompanyPay());
			show.setProcreateCompanyRadix(zhHome.getProcreateCompanyRadix());

			show.setMedicalTreatmentPersonPay(zhHome.getMedicalTreatmentPersonPay());
			show.setMedicalTreatmentCompanyPay(zhHome.getMedicalTreatmentCompanyPay());
			show.setMedicalTreatmentRadix(zhHome.getMedicalTreatmentRadix());

			show.setSeriousIllnessPersonPay(zhHome.getSeriousIllnessPersonPay());
			show.setSeriousIllnessCompanyPay(zhHome.getSeriousIllnessCompanyPay());
			show.setSeriousIllnessRedix(zhHome.getSeriousIllnessRedix());

			Double Ppension = zhHome.getPensionPersonPay();
			Double Punemployment = zhHome.getUnemploymentPersonPay();
			Double Pinjury = zhHome.getInjuryPersonPay();
			Double Pprocreate = zhHome.getProcreatePersonPay();
			Double PmedicalTreatment = zhHome.getMedicalTreatmentPersonPay();
			Double PseriousIllness = zhHome.getSeriousIllnessPersonPay();
			Double Paccumulation = zhHome.getAccumulationFundPersonPay();
			// Double personTotal =
			// (Ppension+Punemployment+Pinjury)+(Pprocreate+PmedicalTreatment)+(PseriousIllness+Paccumulation);
			Double personTotal = zhHome.getPersonTotal();

			Double Cpension = zhHome.getPensionCompanyPay();
			Double Cunemployment = zhHome.getUnemploymentCompanyPay();
			Double Cinjury = zhHome.getInjuryCompanyPay();
			Double Cprocreate = zhHome.getProcreateCompanyPay();
			Double Cmedical = zhHome.getMedicalTreatmentCompanyPay();
			Double CseriousIllness = zhHome.getSeriousIllnessCompanyPay();
			Double Caccumulation = zhHome.getAccumulationFundCompanyPay();
			Double serviceFee = zhHome.getServiceCharge();
			// Double companyTotal =
			// (Cpension+Cunemployment+Cprocreate)+(Cmedical+CseriousIllness)+(Cinjury+Caccumulation);
			Double companyTotal = zhHome.getCompanyTotal();

			Double radix = (Double.valueOf(accumulationRadix)) == null ? 0.00
					: Double.valueOf(accumulationRadix.trim());
			show.setAccumulationRadix(radix);
			show.setAccumulationCompanyTotal(Caccumulation);
			show.setAccumulationPersonTotal(Paccumulation);
			Double accumulationTotal = OwnHomeUtils.add(Paccumulation, Caccumulation);
			show.setAccumulationTotalFee(accumulationTotal);
			show.setCompanyTotal(companyTotal);
			show.setPersonTotal(personTotal);

			show.setServiceFee(serviceFee);
			Double total = OwnHomeUtils.add(OwnHomeUtils.add(companyTotal, personTotal), serviceFee);
			show.setTotal(total);
			zhDetailList.add(show);
		}
		return zhDetailList;
	}

	// 导入珠海公积金
	@Transactional
	public List<ZHAccumulation> readZHAccumulationFee(String filePath, Integer userId, Integer supplierId,
			String yearMonth, String city)
			throws EncryptedDocumentException, InvalidFormatException, FileNotFoundException, IOException {
		List<ZHAccumulation> list = new ArrayList<ZHAccumulation>();
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
			String personAccount = rowLine.getCell(0).getStringCellValue() == "" ? ""
					: rowLine.getCell(0).getStringCellValue().trim();
			String name = rowLine.getCell(1).getStringCellValue() == "" ? ""
					: rowLine.getCell(1).getStringCellValue().trim();
			String sex = rowLine.getCell(2).getStringCellValue() == "" ? ""
					: rowLine.getCell(2).getStringCellValue().trim();
			String certificateType = rowLine.getCell(3).getStringCellValue() == "" ? ""
					: rowLine.getCell(3).getStringCellValue().trim();
			String certificateNum = rowLine.getCell(4).getStringCellValue() == "" ? ""
					: rowLine.getCell(4).getStringCellValue().trim();
			if (name == "" || certificateNum == "") {
				break;
			}
			String payType = rowLine.getCell(5).getStringCellValue() == "" ? ""
					: rowLine.getCell(5).getStringCellValue().trim();
			Double salaryRadix = rowLine.getCell(6).getStringCellValue() == "" ? 0.00
					: Double.valueOf(rowLine.getCell(6).getStringCellValue().trim());
			String accountStatus = rowLine.getCell(7).getStringCellValue() == "" ? ""
					: rowLine.getCell(7).getStringCellValue().trim();
			String openAccountDate = rowLine.getCell(8).getStringCellValue() == "" ? ""
					: rowLine.getCell(8).getStringCellValue().trim();
			Double monthTotalFee = rowLine.getCell(9).getStringCellValue() == "" ? 0.00
					: Double.valueOf(rowLine.getCell(9).getStringCellValue().trim());
			Double c_monthFee = rowLine.getCell(10).getStringCellValue() == "" ? 0.00
					: Double.valueOf(rowLine.getCell(10).getStringCellValue().trim());
			Double p_monthFee = rowLine.getCell(11).getStringCellValue() == "" ? 0.00
					: Double.valueOf(rowLine.getCell(11).getStringCellValue().trim());

			ZHAccumulation zhAccumulation = new ZHAccumulation();
			zhAccumulation.setPersonNumber(personAccount);
			zhAccumulation.setName(name);
			zhAccumulation.setSex(sex);
			zhAccumulation.setCertificateType(certificateType);
			zhAccumulation.setCertificatNum(certificateNum);
			zhAccumulation.setPayType(payType);
			zhAccumulation.setSalaryRadix(salaryRadix);
			zhAccumulation.setAccountStatus(accountStatus);
			zhAccumulation.setOpenAccount(openAccountDate);
			zhAccumulation.setMonthFee(monthTotalFee);
			zhAccumulation.setUnitMonthFee(c_monthFee);
			zhAccumulation.setPersonMonthFee(p_monthFee);

			if (name != null && name != "" && certificateNum != null && certificateNum != "") {
				list.add(zhAccumulation);
				/*
				 * costZhMapper.updateAccumulation(personAccount, c_monthFee,
				 * p_monthFee, supplierId, yearMonth, certificateNum, userId);
				 */
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

			String[] strs = { "个人账号", "姓名", "性别", "证件类型", "证件号码", "缴存类型", "工资基数", "账户状态", "开户日期", "月缴额", "单位月缴交额",
					"个人月缴交额" };
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

			String[] strs = { "序号", "姓名", "身份证号码", "购买基数", "比例", "公司金额", "个人金额", "合计", "工资始扣月", "始参月份", "帐号", "公司",
					"负责人" };
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
	private ZhangdanMingxi getSupplierDetail(int zaiceId, CostZh zhHome) {
		ZhangdanMingxi zdmingxi = new ZhangdanMingxi();
		zdmingxi.setZaiceId(zaiceId);
		Double Ppension = zhHome.getPensionPersonPay();
		Double Punemployment = zhHome.getUnemploymentPersonPay();
		Double Pinjury = zhHome.getInjuryPersonPay();
		Double Pprocreate = zhHome.getProcreatePersonPay();
		Double PmedicalTreatment = zhHome.getMedicalTreatmentPersonPay();
		Double PseriousIllness = zhHome.getSeriousIllnessPersonPay();

		Double Cpension = zhHome.getPensionCompanyPay();
		Double Cunemployment = zhHome.getUnemploymentCompanyPay();
		Double Cinjury = zhHome.getInjuryCompanyPay();
		Double Cprocreate = zhHome.getProcreateCompanyPay();
		Double Cmedical = zhHome.getMedicalTreatmentCompanyPay();
		Double CseriousIllness = zhHome.getSeriousIllnessCompanyPay();
		zdmingxi.setShijiYanglaoGongsi(Cpension);
		zdmingxi.setShijiYanglaoGeren(Ppension);
		zdmingxi.setShijiJibenYiliaoGongsi(Cmedical);
		zdmingxi.setShijiJibenYiliaoGeren(PmedicalTreatment);
		zdmingxi.setShijiDabingYiliaoGongsi(CseriousIllness);
		zdmingxi.setShijiDabingYiliaoGeren(PseriousIllness);
		zdmingxi.setShijiShiyeGongsi(Cunemployment);
		zdmingxi.setShijiShiyeGeren(Punemployment);
		zdmingxi.setShijiGongshangGongsi(Cinjury);
		;
		zdmingxi.setShijiShengyuGongsi(Cprocreate);

		Double Caccumulation = zhHome.getAccumulationFundCompanyPay();
		Double Paccumulation = zhHome.getAccumulationFundPersonPay();
		zdmingxi.setShijiGongjijinGongsi(Caccumulation);
		zdmingxi.setShijiGongjijinGeren(Paccumulation);
		zdmingxi.setShijiQitaGongsi(0.00);
		zdmingxi.setShijiQitaGeren(0.00);

		Double serviceFee = zhHome.getServiceCharge();
		zdmingxi.setShijiFuwufei(serviceFee);
		return zdmingxi;
	}

	// 导出珠海公积金
	@SuppressWarnings("deprecation")
	public String writeZHAccumulationFee(Integer supplierId, String yearMonth) throws IOException {
		String city = "珠海";
		String supplier = zaiceService.selectSupplierBySupplierId(city, supplierId);
		if (supplier == null) {
			supplier = "数据为空！";
		}
		Workbook wb = new HSSFWorkbook();
		HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle(); // 样式对象

		String[] strs = { "序号", "姓名", "身份证号码", "购买基数", "比例", "公司金额", "个人金额", "合计", "工资始扣月", "始参月份", "帐号", "公司", "负责人" };
		HSSFSheet sheet = (HSSFSheet) wb.createSheet(supplierId + "号供应商" + yearMonth + "月(珠海)公积金明细");

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
			rows.getCell(0).setCellValue(yearMonth + "月(珠海)-" + supplier + "公积金明细");
		}
		// 合并单元格
		CellRangeAddress cra = new CellRangeAddress(0, (short) 0, 1, (short) (rows.getLastCellNum() - 1)); // 起始行,
																											// 终止行,
																											// 起始列,
																											// 终止列
		sheet.addMergedRegion(cra);

		HSSFRow rowss = sheet.createRow((short) 1);
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

		List<CostZh> zhList = costZhMapper.selectZHhomeAllBysupplierId(supplierId, yearMonth);
		List<ZHExportAccumulation> zhAccumulations = new ArrayList<ZHExportAccumulation>();
		if (zhList == null) {
			return null;
		}
		Integer supplierIds = null;
		String accumulationNumber = null;
		String accumulationRadix = null;
		String accumulationRatio = null;
		for (CostZh zhHome : zhList) {
			String month = zhHome.getReportingPeriod();
			String certificateNum = zhHome.getCertificateNumber();
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
			ZHExportAccumulation zhExportAccumulation = new ZHExportAccumulation();

			zhExportAccumulation.setName(zhHome.getName());
			zhExportAccumulation.setCertificateNum(certificateNum);
			Double payRadix = Double.valueOf((accumulationRadix.trim()));
			zhExportAccumulation.setPayRadix(payRadix);

			int index = accumulationRatio.indexOf("%");
			int indexs = accumulationRatio.indexOf("+");
			String c_ratio = accumulationRatio.substring(0, index);
			String p_ratio = accumulationRatio.substring(indexs + 1, (accumulationRatio.trim().length()) - 1);
			Double c_payRatio = Double.valueOf((c_ratio.replace("%", "").trim()));
			Double p_payRatio = Double.valueOf((p_ratio.replace("%", "").trim()));
			zhExportAccumulation.setPayRatio(c_payRatio / 100.00);
			Double c_payMoney = OwnHomeUtils.mul(payRadix, (c_payRatio / 100.00));
			Double p_payMoney = OwnHomeUtils.mul(payRadix, (p_payRatio / 100.00));
			zhExportAccumulation.setUnitMonthFee(c_payMoney);
			zhExportAccumulation.setPersonMonthFee(p_payMoney);
			zhExportAccumulation.setTotalFee(OwnHomeUtils.add(c_payMoney, p_payMoney));

			String startmonth = zaiceService.findAccumulationFundBeginTime(city, supplierIds, month, certificateNum);
			if (startmonth != null) {
				startmonth = (startmonth.trim()).substring(0, 7).replace("-", "年");
				startmonth = startmonth + "月";
				zhExportAccumulation.setStartSalaryMonth(startmonth);
				zhExportAccumulation.setStartPayMonth(startmonth);
			} else {
				startmonth = "数据异常！";
			}
			zhExportAccumulation.setAccumulationAccount(accumulationNumber);
			String customerNmae = customerService.findCustomerName(zhHome.getCustomerId());
			if (customerNmae == null) {
				customerNmae = "********";
			}
			zhExportAccumulation.setCompany(customerNmae);
			String userName = userService.findUserName(zhHome.getUserId());
			if (userName == null) {
				userName = "数据为空！";
			}
			zhExportAccumulation.setManagerName(userName);

			if (zhHome.getName() != null && certificateNum != null) {
				zhAccumulations.add(zhExportAccumulation);
			}
		}

		for (int rowNum = 2; rowNum <= zhAccumulations.size() + 1; rowNum++) {
			HSSFRow row = sheet.createRow(rowNum);
			row.setHeightInPoints(20);
			HSSFCell cell = row.createCell(0, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(String.valueOf(rowNum - 1));
			cell = row.createCell(1, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(zhAccumulations.get(rowNum - 2).getName());
			cell = row.createCell(2, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(zhAccumulations.get(rowNum - 2).getCertificateNum());
			cell = row.createCell(3, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(zhAccumulations.get(rowNum - 2).getPayRadix());
			cell = row.createCell(4, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(zhAccumulations.get(rowNum - 2).getPayRatio());
			cell = row.createCell(5, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(zhAccumulations.get(rowNum - 2).getUnitMonthFee());
			cell = row.createCell(6, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(zhAccumulations.get(rowNum - 2).getPersonMonthFee());
			cell = row.createCell(7, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(zhAccumulations.get(rowNum - 2).getTotalFee());
			cell = row.createCell(8, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(zhAccumulations.get(rowNum - 2).getStartSalaryMonth());
			cell = row.createCell(9, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(zhAccumulations.get(rowNum - 2).getStartPayMonth());
			cell = row.createCell(10, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(zhAccumulations.get(rowNum - 2).getAccumulationAccount());
			cell = row.createCell(11, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(zhAccumulations.get(rowNum - 2).getCompany());
			cell = row.createCell(12, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(zhAccumulations.get(rowNum - 2).getManagerName());
			for (int i = 0; i < 13; i++) {
				sheet.setColumnWidth(i, (short) (16 * 256));
				if (i == 2 || i == 10 || i == 11) {
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

	// 珠海费用导出
	public String writeZHInfoAll(Integer supplierId, String yearMonth) throws IOException {
		String city = "珠海";
		String supplier = zaiceService.selectSupplierBySupplierId(city, supplierId);
		Workbook wb = new HSSFWorkbook();
		HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
		HSSFSheet sheet = (HSSFSheet) wb.createSheet(supplierId + "号供应商" + yearMonth + "月(珠海)费用明细");
		// 设置单元格内容垂直对其方式为居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
		style.setAlignment(HorizontalAlignment.CENTER);// 水平 // 创建一个居中格式

		String[] strs = { "姓名", "身份证号码", "个人社保号", "基本养老保险", "城镇工失业保险", "农民工失业保险", "工伤保险", "生育保险", "基本医疗保险", "补充基本医疗保险",
				"住房公积金", "残保金", "服务费", "单位合计", "个人合计", "合计" };
		String[] strArrs = { "个人", "单位", "基数", "个人", "单位", "基数", "个人", "单位", "基数", "个人", "单位", "基数", "个人", "单位", "基数",
				"个人", "单位", "基数", "个人", "单位", "基数", "基数", "公司", "个人", "合计" };
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
			rows.getCell(0).setCellValue(yearMonth + "月(珠海)-" + supplier + "费用明细");
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
			} else if (i == 24) {
				ce.setCellValue(strs[10]);
			} else if (i == 28) {
				ce.setCellValue(strs[11]);
			} else if (i == 29) {
				ce.setCellValue(strs[12]);
			} else if (i == 30) {
				ce.setCellValue(strs[13]);
			} else if (i == 31) {
				ce.setCellValue(strs[14]);
			} else if (i == 32) {
				ce.setCellValue(strs[15]);
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
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 21, (short) 23));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 24, (short) 27));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 28, (short) 28));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 29, (short) 29));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 30, (short) 30));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 31, (short) 31));
		sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 32, (short) 32));

		List<CostZh> list = costZhMapper.selectZHhomeAllBysupplierId(supplierId, yearMonth);
		List<ZHDetailShow> detailList = getZHShows(list);
		if (detailList == null || list == null) {
			return "";
		}
		for (int i = 3; i < (detailList.size() + 3); i++) {
			List ll = new ArrayList();
			ll.add(detailList.get(i - 3).getName());
			ll.add(detailList.get(i - 3).getCertificateNumber());
			ll.add(detailList.get(i - 3).getSocialSecurityNumber());

			ll.add(detailList.get(i - 3).getPensionPersonPay());
			ll.add(detailList.get(i - 3).getPensionCompanyPay());
			ll.add(detailList.get(i - 3).getPensionRadix());

			ll.add(detailList.get(i - 3).getCityUnemploymentPersonPay());
			ll.add(detailList.get(i - 3).getCityUnemploymentCompanyPay());
			ll.add(detailList.get(i - 3).getCityUnemploymentRadix());
			ll.add(detailList.get(i - 3).getFarmersUnemploymentPersonPay());
			ll.add(detailList.get(i - 3).getFarmersUnemploymentCompanyPay());
			ll.add(detailList.get(i - 3).getFarmersUnemploymentRadix());

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
					cc.setCellValue(ll.get(x) + "");
				} else {
					cc = rowLine.createCell(x, CellType.NUMERIC);
					sheet.setColumnWidth(x, (short) (13 * 256));
					cc.setCellStyle(style);
					Double value = (Double) ll.get(x);
					if (value == null) {
						value = 0.00;
					}
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

	// 批量删除珠海自有户信息
	public String deleteZHInfoAll(Integer supplierId, String yearMonth) {
		int a = -1;
		List<CostZh> gzList = costZhMapper.selectZHhomeAllBysupplierId(supplierId, yearMonth);
		a = costZhMapper.deleteZHhomeDetails(supplierId, yearMonth);
		if (a > 0 && a == (gzList.size())) {
			return "删除成功！";
		}
		return null;
	}

}
