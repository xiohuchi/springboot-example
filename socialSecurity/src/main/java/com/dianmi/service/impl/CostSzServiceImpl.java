package com.dianmi.service.impl;

import java.io.File;
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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.common.OwnHomeUtils;
import com.dianmi.mapper.CostSzMapper;
import com.dianmi.model.CostSz;
import com.dianmi.model.Zaice;
import com.dianmi.model.ZhangdanMingxi;
import com.dianmi.model.accumulation.SZAccumulation;
import com.dianmi.model.po.ZaiceCustomerMsg;
import com.dianmi.service.CostSzService;
import com.dianmi.utils.MathArithmetic;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.UploadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.dianmi.utils.poi.ReadExcel;

/**
 * @author www
 *
 */
@Service
@SuppressWarnings("all")
public class CostSzServiceImpl extends CommonService implements CostSzService {

	/**
	 * 
	 * 导入深圳社保费用
	 */
	@Transactional
	public ResultJson importCost(MultipartFile file, Integer userId, Integer supplierId, String yearMonth) {
		if (file == null || file.isEmpty())
			return ResultUtil.error(RestEnum.FILE_NOT_EXISTS);
		String fileName = file.getOriginalFilename();
		if (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))
			return ResultUtil.error(RestEnum.FILE_FORMATS_ERROR);
		if (null == userId || StringUtils.isEmpty(yearMonth))
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		String filePath = UploadFile.uploadFile(file);// 上传深圳社保费用文件
		List<String[]> list = ReadExcel.readIrregularExcel2List(filePath).get(0);// 从excel读取数据
		DeleteFile.DeleteFolder(filePath);// 上传成功删除Excel模板
		if (list.isEmpty())
			return ResultUtil.error(RestEnum.FILE_DATA_IS_NULL);
		int startLine = getSocialSecurityStartLine(list);
		if (!isShenzhenSocialSecurityTemplate(list.get(startLine)))
			return ResultUtil.error(RestEnum.FAILD, "请上传深圳社保模板");
		String companyNum = list.get(startLine - 3)[0].replace("单位编号：", ""); // 单位编号
		String paymentMonth = list.get(startLine - 2)[0].replace("台帐年月：", "");// 台账年月（台帐年月：台帐年月：2017年9月）
		int result = 0;
		List<String> allCertificateNumberList = new ArrayList<String>();// 所有读取的身份证号码集合
		// 遍历excel中数据
		for (int i = startLine + 1; i < list.size(); i++) {
			List<String> strList = Arrays.asList(list.get(i));
			String personNumber = strList.get(1); // 个人编号
			String name = strList.get(2); // 客户姓名
			String certificateNumber = strList.get(3).replaceAll("x", "X").trim(); // 客户身份证号

			Double total = Double.parseDouble(strList.get(4)); // 默认0.00，合计应收金额
			Double personTotal = Double.parseDouble(strList.get(5)); // 默认0.00，个人部分合计
			Double companyTotal = Double.parseDouble(strList.get(6)); // 默认0.00，公司部分合计
			Double pensionRadix = Double.parseDouble(strList.get(7)); // 默认0.00，养老缴纳基数
			Double pensionPersonPay = Double.parseDouble(strList.get(8));// 养老个人缴费
			Double pensionCompanyPay = Double.parseDouble(strList.get(9));// 养老公司缴费
			Double medicalTreatmentRadix = Double.parseDouble(strList.get(10)); // 默认0.00，医疗缴费基数
			Double medicalTreatmentPersonPay = Double.parseDouble(strList.get(11));// 医疗个人缴费
			Double medicalTreatmentCompanyPay = Double.parseDouble(strList.get(12));// 医疗公司缴费
			Double injuryCompanyRadix = Double.parseDouble(strList.get(13)); // 默认0.00，工伤保险企业缴费基数
			Double injuryCompanyPay = Double.parseDouble(strList.get(14));// 工伤企业缴费
			Double unemploymentRadix = Double.parseDouble(strList.get(15)); // 默认0.00，失业保险缴纳基数
			Double unemploymentPersonPay = Double.parseDouble(strList.get(16));// 失业个人缴费
			Double unemploymentCompanyPay = Double.parseDouble(strList.get(17));// 失业单位缴费
			Double procreateCompanyRadix = Double.parseDouble(strList.get(18)); // 默认0.00，生育保险企业缴费基数
			Double procreateCompanyPay = Double.parseDouble(strList.get(19));// 生育单位缴费
			allCertificateNumberList.add(certificateNumber);
			ZaiceCustomerMsg customerMsg = zaiceMsg(yearMonth, SHENZHEN, certificateNumber);
			// 费用信息
			CostSz costSz = new CostSz(userId, customerMsg.getCustomerId(), supplierId, customerMsg.getCustomerName(),
					yearMonth, paymentMonth, personNumber, name, certificateNumber, pensionRadix, pensionCompanyPay,
					pensionPersonPay, unemploymentRadix, unemploymentCompanyPay, unemploymentPersonPay,
					injuryCompanyRadix, injuryCompanyPay, procreateCompanyRadix, procreateCompanyPay,
					medicalTreatmentRadix, medicalTreatmentCompanyPay, medicalTreatmentPersonPay, companyTotal,
					personTotal, 0.0, total, companyNum, null, 0.0, 0.0, 0.0, 0.0, null, null);
			// 判断费用是否已经导入
			if (isCostImport(supplierId, yearMonth, certificateNumber)) {
				costSzMapper.updateCostSz(costSz);
				result += 1;
			} else {
				// 新增深圳费用信息
				costSzMapper.insertSelective(costSz);
				result += 1;
			}
			// 账单明细信息更新
			zhangdanService.updateZhangdanByZaiceId(setShijiZhangdanMingxi(customerMsg.getZaiceId(), costSz));
			// 确定增员成功
			zengyuanService.updateAddStatus(certificateNumber, SHENZHEN, yearMonth);
			zaiceService.updateAddSocialStatus(yearMonth, SHENZHEN, certificateNumber);
		}
		List<String> allCertificateNumber = zengyuanService.getAllCertificateNumber(yearMonth, supplierId);// 当前月份下按供应商分类的所有身份证号码集合
		List<String> newZengyuanList = new ArrayList<String>(allCertificateNumber);
		newZengyuanList.removeAll(allCertificateNumberList);
		updateToZengyuanFailed(newZengyuanList, yearMonth, SHENZHEN);
		List<String> allJianyuanCertificateNumber = jianyuanService.getAllCertificateNumber(yearMonth, supplierId);
		List<String> jianyuanFaildList = new ArrayList<String>(allJianyuanCertificateNumber);
		jianyuanFaildList.removeAll(newZengyuanList);
		updateToJianyuanFailed(jianyuanFaildList, yearMonth, SHENZHEN);
		// 减员成功的用户信息
		List<String> jianyuanSuccessList = new ArrayList<String>(allJianyuanCertificateNumber);
		jianyuanSuccessList.removeAll(jianyuanFaildList);
		updateToJianyuanSuccess(jianyuanSuccessList, yearMonth, SHENZHEN);
		return ResultUtil.success(RestEnum.SUCCESS, "处理：" + result + "条");
	}

	/**
	 * @param strArr
	 * @return
	 */
	public boolean isShenzhenSocialSecurityTemplate(String[] strArr) {
		if (strArr[0].contains("序号") && strArr[1].contains("个人编号") && strArr[2].contains("姓名")
				&& strArr[3].contains("身份证号") && strArr[4].contains("应收合计") && strArr[5].contains("个人合计")) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * @param list
	 * @return 获取社保数据起始行
	 */
	public int getSocialSecurityStartLine(List<String[]> list) {
		int startLine = 0;// 缴费数据起始行
		for (int i = 0; i < list.size(); i++) {
			String[] strArr = list.get(i);
			if (strArr.length > 4)
				if (!StringUtils.isEmpty(strArr[1]) && !StringUtils.isEmpty(strArr[2])
						&& !StringUtils.isEmpty(strArr[3]) && !StringUtils.isEmpty(strArr[4]))
					if (strArr[1].equals("个人编号") && strArr[2].equals("姓名") && strArr[3].equals("身份证号")
							&& strArr[4].equals("应收合计"))
						return startLine = i;
		}
		return startLine;
	}

	/**
	 * @param supplierId
	 * @param yearMonth
	 * @param certificateNumber
	 * @return
	 */
	private boolean isCostImport(int supplierId, String yearMonth, String certificateNumber) {
		if (costSzMapper.isCostImport(supplierId, yearMonth, certificateNumber).size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * @param zaiceId
	 * @param costSz
	 * @return
	 */
	private ZhangdanMingxi setShijiZhangdanMingxi(int zaiceId, CostSz costSz) {
		ZhangdanMingxi zm = new ZhangdanMingxi();
		zm.setZaiceId(zaiceId);
		zm.setShijiYanglaoGongsi(costSz.getPensionCompanyPay());
		zm.setShijiYanglaoGeren(costSz.getPensionPersonPay());
		zm.setShijiJibenYiliaoGongsi(costSz.getMedicalTreatmentCompanyPay());
		zm.setShijiJibenYiliaoGeren(costSz.getMedicalTreatmentPersonPay());
		zm.setShijiShiyeGongsi(costSz.getUnemploymentCompanyPay());
		zm.setShijiShiyeGeren(costSz.getUnemploymentPersonPay());
		zm.setShijiGongshangGongsi(costSz.getInjuryCompanyPay());
		zm.setShijiShengyuGongsi(costSz.getProcreateCompanyPay());
		return zm;
	}

	/**
	 * @param file
	 * @param userId
	 * @param supplierId
	 * @param yearMonth
	 *            導入公積金
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
		DeleteFile.DeleteFolder(filePath);
		if (list.isEmpty())
			return ResultUtil.error(RestEnum.FILE_DATA_IS_NULL);
		int startLine = getAccumulationFundStartLine(list);
		if (!isShenzhenAccumulationFundTemplate(list.get(startLine)))
			return ResultUtil.error(RestEnum.FAILD, "请上传深圳公积金模板");
		int result = 0;
		for (int j = startLine + 1; j < list.size(); j++) {
			String[] strArr = list.get(j);
			String accumulationFundAccount = strArr[1];// 公积金账号
			String employeeName = strArr[2];// 姓名
			String certificateNumber = strArr[3].replaceAll("x", "X").trim();// 身份证号码
			double accumulationFundRadix = Double.parseDouble(strArr[4]);// 公積金基數
			double accumulationFundCompanyRatio = Double.parseDouble(strArr[5]);// 公積金單位比例
			double accumulationFundPersonRatio = Double.parseDouble(strArr[6]);// 公積金個人比例
			double accumulationFundCompanyPay = MathArithmetic.mul(accumulationFundRadix, accumulationFundCompanyRatio);// 公積金公司繳費
			double accumulationFundPersonPay = MathArithmetic.mul(accumulationFundRadix, accumulationFundPersonRatio);// 公積金個人繳費
			double accumulationFundTotal = Double.parseDouble(strArr[7]);
			String accumulationFundPaymentMonth = strArr[8];// 缴存月份
			String customer = strArr[9];// 客户
			ZaiceCustomerMsg customerMsg = zaiceMsg(yearMonth, SHENZHEN, certificateNumber);
			CostSz costSz = new CostSz( userId,  customerMsg.getCustomerId(),  supplierId,  customerMsg.getCustomerName(), yearMonth,
					employeeName,  certificateNumber,  accumulationFundAccount,  accumulationFundRadix,
					 accumulationFundCompanyRatio,  accumulationFundPersonRatio,  accumulationFundTotal,
					 accumulationFundPaymentMonth,  customer);
			if (isCostImport(supplierId, yearMonth, certificateNumber)) {
				// 更新深圳费用表中公积金信息
				costSzMapper.updateGongjijin(costSz);
				result += 1;
			} else {
				costSzMapper.insertSelective(costSz);
				result += 1;
			}
			// 更新账单明细表中公积金信息
			zhangdanService.updateGongjijin(accumulationFundCompanyPay, accumulationFundPersonPay,
					customerMsg.getZaiceId());
		}
		return ResultUtil.success(RestEnum.SUCCESS, "处理：" + result + "条");
	}

	public boolean isShenzhenAccumulationFundTemplate(String[] strArr) {
		if (strArr[0].contains("序号") && strArr[1].contains("个人公积金账号") && strArr[2].contains("姓名")
				&& strArr[3].contains("身份证号码") && strArr[4].contains("缴存基数") && strArr[5].contains("单位缴存比例")
				&& strArr[6].contains("个人缴存比例") && strArr[7].contains("缴存额") && strArr[8].contains("缴存月份")
				&& strArr[9].contains("客户")) {
			return true;
		} else {
			return false;
		}
	}

	// 返回公积金表头行
	private int getAccumulationFundStartLine(List<String[]> list) {
		int startLine = 0;// 缴费数据起始行
		for (int i = 0; i < list.size(); i++) {
			String[] strArr = list.get(i);
			if (strArr.length > 5)
				if (!StringUtils.isEmpty(strArr[1]) && !StringUtils.isEmpty(strArr[2])
						&& !StringUtils.isEmpty(strArr[3]) && !StringUtils.isEmpty(strArr[4])
						&& !StringUtils.isEmpty(strArr[5]))
					if (strArr[1].equals("个人公积金账号") && strArr[2].equals("姓名") && strArr[3].equals("身份证号码")
							&& strArr[4].equals("缴存基数") && strArr[5].equals("单位缴存比例"))
						return startLine = i;
		}
		return startLine;
	}

	// 是否增员成功
	private Boolean getUpdateSZAddResult(CostSzMapper costSzMapper, String city, Integer supplierId, String month,
			String certificateNumber) {
		Boolean flag = false;
		city = city.trim();
		certificateNumber = certificateNumber.trim();
		month = month.trim();
		String addEmployeeName = costSzMapper.selectEmployeeName(supplierId, month, certificateNumber);
		if (addEmployeeName != null && addEmployeeName != "") {
			flag = true;
		}
		return flag;
	}

	// 是否减员成功
	private Boolean getUpdateSZMinusResult(CostSzMapper costSzMapper, String city, Integer supplierId, String month,
			String certificateNumber) {
		Boolean flag = false;
		city = city.trim();
		certificateNumber = certificateNumber.trim();
		month = month.trim();
		String minusEmployeeName = costSzMapper.selectEmployeeName(supplierId, month, certificateNumber);
		if (minusEmployeeName == null || minusEmployeeName == "") {
			flag = true;
		}
		return flag;
	}

	// 获取分页对象
	/*
	 * private List<SZLDDGShow> getSGShow(List<CostSz> list) { List<SZLDDGShow>
	 * showList = new ArrayList<SZLDDGShow>(); for (CostSz costSz : list) {
	 * SZLDDGShow shows = new SZLDDGShow(); Map<String, String> strs =
	 * customerService.findCustomerInfo(costSz.getCustomerId());
	 * shows.setReportingPeriod((costSz.getReportingPeriod() == null ? "" :
	 * costSz.getReportingPeriod())); String customerName = null; String dept =
	 * null; if (strs == null) { customerName = "数据为空!"; dept = "数据为空!"; } else
	 * { customerName = strs.get("customerName"); dept = strs.get("deptName"); }
	 * shows.setCustomerName(customerName); shows.setDept(dept);
	 * 
	 * Double personPension = costSz.getPensionPersonPay(); Double unitPension =
	 * costSz.getPensionCompanyPay(); Double pensionFees =
	 * OwnHomeUtils.add(personPension, unitPension);
	 * shows.setPensionSocial(pensionFees);
	 * 
	 * Double personMedical = costSz.getMedicalTreatmentPersonPay(); Double
	 * unitMedical = costSz.getMedicalTreatmentCompanyPay(); Double medicalFees
	 * = OwnHomeUtils.add(personMedical, unitMedical);
	 * shows.setMedicaltreatmentSocial(medicalFees);
	 * 
	 * Double personSeriousIllness = costSz.getSeriousIllnessPersonPay(); Double
	 * unitSeriousIllness = costSz.getSeriousIllnessCompanyPay(); Double
	 * seriousIllnessFees = OwnHomeUtils.add(personSeriousIllness,
	 * unitSeriousIllness); shows.setSeriousIllnessSocial(seriousIllnessFees);
	 * 
	 * Double personInjury = costSz.getInjuryPersonPay(); Double unitInjury =
	 * costSz.getInjuryCompanyPay(); Double injuryFees =
	 * OwnHomeUtils.add(personInjury, unitInjury);
	 * shows.setInjurySocial(injuryFees);
	 * 
	 * Double personUnemployment = costSz.getUnemploymentPersonPay(); Double
	 * unitUnemployment = costSz.getUnemploymentCompanyPay(); Double
	 * unemploymentFees = OwnHomeUtils.add(personUnemployment,
	 * unitUnemployment); shows.setUnemploymentSocial(unemploymentFees);
	 * 
	 * Double personProcreate = costSz.getProcreatePersonPay(); Double
	 * unitProcreate = costSz.getProcreateCompanyPay(); Double procreateFees =
	 * OwnHomeUtils.add(personProcreate, unitProcreate);
	 * shows.setProcreateSocial(procreateFees);
	 * 
	 * // Double personFee = //
	 * ((personPension+personUnemployment+personInjury)+(personProcreate+
	 * personMedical+personSeriousIllness)); Double personFee =
	 * costSz.getPersonTotal(); // Double companyFee = //
	 * ((unitPension+unitUnemployment+unitInjury)+(unitProcreate+unitMedical+
	 * unitSeriousIllness)); Double companyFee = costSz.getCompanyTotal();
	 * shows.setPersonSocialTotal(personFee);
	 * shows.setCompanySocialTotal(companyFee); Double socialTotal =
	 * OwnHomeUtils.add(personFee, companyFee);
	 * shows.setSocialTotalPay(socialTotal);
	 * 
	 * Double disabilityBenefit = costSz.getDisabilityBenefit(); Double
	 * serviceFee = costSz.getServiceCharge(); Double Paccumulation =
	 * costSz.getAccumulationFundPersonPay(); Double Caccumulation =
	 * costSz.getAccumulationFundCompanyPay();
	 * shows.setDisabilityBenefitFee(disabilityBenefit);
	 * shows.setCompanyAccumulation(Caccumulation);
	 * shows.setPersonAccumulation(Paccumulation);
	 * 
	 * Double accumulationTotalFee = OwnHomeUtils.add(Paccumulation,
	 * Caccumulation); shows.setAccumulationTotalPay(accumulationTotalFee);
	 * shows.setServiceFee(serviceFee); Double Total =
	 * OwnHomeUtils.add(OwnHomeUtils.add(socialTotal, accumulationTotalFee),
	 * OwnHomeUtils.add(disabilityBenefit, serviceFee));
	 * shows.setTotalFee(Total); showList.add(shows); } return showList; }
	 */

	// 深圳分页展示
	/*
	 * public List selectSZhomeAll(Integer supplierId, String yearMonth, Integer
	 * currPage, Integer pageSize) { // PageHelper.startPage(page, //
	 * pageSize);这段代码表示，程序开始分页了，page默认值是1，pageSize默认是10，意思是从第1页开始，每页显示10条记录。
	 * List<CostSz> list = szHomeMapper.selectSZhomeAll(supplierId, yearMonth,
	 * (currPage - 1), pageSize); if (list == null) { return new ArrayList(); }
	 * PageHelper.startPage(currPage, pageSize); List<SZLDDGShow> showList =
	 * getSGShow(list); if (showList == null) { return new ArrayList(); }
	 * PageInfo<SZLDDGShow> pageList = new PageInfo<SZLDDGShow>(showList); //
	 * pageList.getTotal(); return pageList.getList(); }
	 */

	// 深圳详情分页
	/*
	 * public List selectSZInfoAll(Integer supplierId, String yearMonth, Integer
	 * currPage, Integer pageSize) { List<CostSz> list =
	 * szHomeMapper.selectSZhomeAll(supplierId, yearMonth, (currPage - 1),
	 * pageSize); if (list == null) { return new ArrayList(); }
	 * PageHelper.startPage(currPage, pageSize);
	 * 
	 * List<SZDetailShow> detailList = getSZDetailShow(list); if (detailList ==
	 * null) { return new ArrayList(); } PageInfo<SZDetailShow> pageList = new
	 * PageInfo<SZDetailShow>(detailList); return pageList.getList(); }
	 */

	/*
	 * // 详情分页对象 private List<SZDetailShow> getSZDetailShow(List<CostSz> list) {
	 * String city = "深圳"; List<SZDetailShow> detailList = new
	 * ArrayList<SZDetailShow>();
	 * 
	 * String accumulationNumber = null; String accumulationRadix = null; String
	 * accumulationRatio = null; for (CostSz szhome : list) { Zaice zaice =
	 * zaiceService.findAccumulationInfos(city, szhome.getReportingPeriod(),
	 * szhome.getCertificateNumber()); if (zaice == null) { // zaice = new
	 * Zaice(); //
	 * //accumulation_fund_number:公积金账号,accumulation_fund_cardinal_number:公积金基数,
	 * accumulation_fund_ratio:公积金比例(公司%+个人%) accumulationNumber = "账号为空!请检查数据";
	 * accumulationRadix = "0"; accumulationRatio = "0%"; } else {
	 * accumulationNumber = zaice.getAccumulationFundNumber(); accumulationRadix
	 * = zaice.getAccumulationFundCardinalNumber(); accumulationRatio =
	 * zaice.getAccumulationFundRatio(); } SZDetailShow show = new
	 * SZDetailShow(); show.setName(szhome.getName());
	 * show.setCertificateNumber(szhome.getCertificateNumber());
	 * 
	 * Double Ppension = szhome.getPensionPersonPay(); Double Punemployment =
	 * szhome.getUnemploymentPersonPay(); Double Pinjury =
	 * szhome.getInjuryPersonPay(); Double Pprocreate =
	 * szhome.getProcreatePersonPay(); Double Pmedical =
	 * szhome.getMedicalTreatmentPersonPay(); Double PseriousIllness =
	 * szhome.getSeriousIllnessPersonPay(); Double Paccumulation =
	 * szhome.getAccumulationFundPersonPay(); // Double personTotal = //
	 * ((Paccumulation+Ppension)+(Punemployment+Pinjury)+(Pmedical+Pprocreate+
	 * PseriousIllness)); Double personTotal = szhome.getPersonTotal();
	 * 
	 * Double Cpension = szhome.getPensionCompanyPay(); Double Cunemployment =
	 * szhome.getUnemploymentCompanyPay(); Double Cinjury =
	 * szhome.getInjuryCompanyPay(); Double Cprocreate =
	 * szhome.getProcreateCompanyPay(); Double Cmedical =
	 * szhome.getMedicalTreatmentCompanyPay(); Double CseriousIllness =
	 * szhome.getSeriousIllnessCompanyPay(); Double Caccumulation =
	 * szhome.getAccumulationFundCompanyPay(); Double disabilityBenefit =
	 * szhome.getDisabilityBenefit(); Double serviceFee =
	 * szhome.getServiceCharge(); // Double companyTotal = //
	 * ((Cpension+Cunemployment)+(Cinjury+Cprocreate)+(Cmedical+CseriousIllness+
	 * Caccumulation)); Double companyTotal = szhome.getCompanyTotal();
	 * 
	 * Double radix = (Double.valueOf(accumulationRadix)) == null ? 0 :
	 * (Double.valueOf(accumulationRadix.trim()));
	 * show.setPersonTotal(personTotal); show.setCompanyTotal(companyTotal);
	 * 
	 * show.setPensionPersonTotal(Ppension);
	 * show.setPensionCompanyTotal(Cpension);
	 * show.setPensionRadix(szhome.getPensionRadix());
	 * 
	 * show.setUnemploymentPersonTotal(Punemployment);
	 * show.setUnemploymentCompanyTotal(Cunemployment);
	 * show.setUnemploymentRadix(szhome.getUnemploymentRadix());
	 * 
	 * show.setInjuryPersonTotal(Pinjury); show.setInjuryCompanyTotal(Cinjury);
	 * show.setInjuryCompanyRadix(szhome.getInjuryCompanyRadix());
	 * 
	 * show.setProcreatePersonTotal(Pprocreate);
	 * show.setProcreateCompanyTotal(Cprocreate);
	 * show.setProcreateCompanyRadix(szhome.getProcreateCompanyRadix());
	 * 
	 * show.setMedicalTreatmentPersonTotal(Pmedical);
	 * show.setMedicalTreatmentCompanyTotal(Cmedical);
	 * show.setMedicalTreatmentRadix(szhome.getMedicalTreatmentRadix());
	 * 
	 * show.setSeriousIllnessPersonTotal(PseriousIllness);
	 * show.setSeriousIllnessCompanyTotal(CseriousIllness);
	 * show.setSeriousIllnessRadix(szhome.getSeriousIllnessRedix());
	 * 
	 * show.setAccumulationRadix(radix);
	 * show.setAccumulationCompanyTotal(Caccumulation);
	 * show.setAccumulationPersonTotal(Paccumulation); Double accumulationTotal
	 * = OwnHomeUtils.add(Caccumulation, Paccumulation);
	 * show.setAccumulationTotalFee(accumulationTotal);
	 * 
	 * show.setDisabilityBenefit(disabilityBenefit);
	 * show.setServiceFee(serviceFee); Double total =
	 * OwnHomeUtils.add(OwnHomeUtils.add(personTotal, companyTotal),
	 * OwnHomeUtils.add(disabilityBenefit, serviceFee)); show.setTotal(total);
	 * detailList.add(show); } return detailList; }
	 */

	// 判断公积金表头
	private Integer getAccumulationLine(Sheet sheet) {
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

			String[] strs = { "序号", "个人公积金账号", "姓名", "身份证号码", "缴存基数", "单位缴存比例", "个人缴存比例", "缴存额", "缴存月份", "客户" };
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

	// 导出深圳自有户费用
	/*
	 * @SuppressWarnings("deprecation") public String writeSZInfoAll(Integer
	 * supplierId, String yearMonth) throws IOException,
	 * EncryptedDocumentException, InvalidFormatException { String city = "深圳";
	 * String supplier = zaiceService.selectSupplierBySupplierId(city,
	 * supplierId);
	 * 
	 * Workbook wb = new HSSFWorkbook(); HSSFCellStyle style = (HSSFCellStyle)
	 * wb.createCellStyle(); // 样式对象 String[] strs = { "姓名", "身份证号码", "应收金额",
	 * "养老保险", "失业保险", "工伤保险", "生育保险", "医疗保险", "住房公积金", "残保金", "服务费", "合计" };
	 * String[] strArrs = { "个人", "单位", "个人", "单位", "基数", "个人", "单位", "基数",
	 * "个人", "单位", "基数", "个人", "单位", "基数", "个人", "单位", "基数", "基数", "公司", "个人",
	 * "合计" }; HSSFSheet sheet = (HSSFSheet) wb.createSheet(supplierId + "号供应商"
	 * + yearMonth + "月(深圳)费用明细");
	 * 
	 * // 设置默认行高,列宽 // sheet.setDefaultRowHeight((short)(20 * 256));
	 * sheet.setDefaultRowHeightInPoints(20); sheet.setDefaultColumnWidth(13);
	 * 
	 * // 创建字体设置字体为宋体 HSSFFont font = (HSSFFont) wb.createFont();
	 * font.setFontName("宋体"); // 设置字体高度 font.setFontHeightInPoints((short) 12);
	 * CreationHelper createHelper = wb.getCreationHelper();
	 * style.setFont(font); // 设置自动换行 style.setWrapText(true); //
	 * 设置单元格内容垂直对其方式为居中 style.setVerticalAlignment(VerticalAlignment.CENTER);//
	 * 垂直 style.setAlignment(HorizontalAlignment.CENTER);// 水平 居中格式
	 * 
	 * HSSFRow rows = sheet.createRow((short) 0); rows.setHeightInPoints(30);
	 * for (int y = 0; y < strArrs.length + 5; y++) { HSSFCellStyle styles =
	 * (HSSFCellStyle) wb.createCellStyle(); // 样式对象
	 * styles.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
	 * sheet.autoSizeColumn(y); // 自动调整宽度 HSSFCell cells = rows.createCell(y,
	 * CellType.STRING); cells.setCellStyle(styles); HSSFFont fonts = (HSSFFont)
	 * wb.createFont(); fonts.setColor(BLUE.index);// HSSFColor.VIOLET.index
	 * //字体颜色 // fonts.setFontName("宋体"); fonts.setBold(true);
	 * styles.setFont(fonts); // 字体增粗 fonts.setFontHeightInPoints((short) 18);
	 * rows.getCell(0).setCellValue(yearMonth + "月(深圳)-" + supplier + "费用明细"); }
	 * // 合并单元格 CellRangeAddress cra = new CellRangeAddress(0, (short) 0, 0,
	 * (short) (rows.getLastCellNum() - 1)); // 起始行, // 终止行, // 起始列, // 终止列
	 * sheet.addMergedRegion(cra);
	 * 
	 * HSSFRow row = sheet.createRow((short) 1); row.setHeightInPoints(20);
	 * HSSFRow row2 = sheet.createRow((short) 2); row2.setHeightInPoints(20);
	 * for (int i = 0; i < (strArrs.length) + 5; i++) { HSSFCellStyle stylet =
	 * (HSSFCellStyle) wb.createCellStyle(); // 样式对象
	 * stylet.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
	 * stylet.setAlignment(HorizontalAlignment.CENTER);// 水平 // 创建一个居中格式
	 * HSSFCell ce = row.createCell(i); sheet.setColumnWidth(i, (short) (13 *
	 * 256)); HSSFFont fontt = (HSSFFont) wb.createFont(); fontt.setBold(true);
	 * stylet.setFont(fontt); ce.setCellStyle(stylet); if (i < 3) {
	 * ce.setCellValue(strs[i]); // 表格的第二行第一列显示的数据 } else if (i == 4) {
	 * ce.setCellValue(strs[3]); } else if (i == 7) { ce.setCellValue(strs[4]);
	 * } else if (i == 10) { ce.setCellValue(strs[5]); } else if (i == 13) {
	 * ce.setCellValue(strs[6]); } else if (i == 16) { ce.setCellValue(strs[7]);
	 * } else if (i == 19) { ce.setCellValue(strs[8]); } else if (i == 23) {
	 * ce.setCellValue(strs[9]); } else if (i == 24) {
	 * ce.setCellValue(strs[10]); } else if (i == 25) {
	 * ce.setCellValue(strs[11]); } else { ce.setCellValue(""); } HSSFCell ces =
	 * null; if (i >= 2 && i < (strArrs.length) + 2) { ces = row2.createCell(i);
	 * sheet.setColumnWidth(i, (short) (13 * 256)); ces.setCellStyle(stylet);
	 * ces.setCellValue(strArrs[i - 2]); } else { ces = row2.createCell(i);
	 * sheet.setColumnWidth(i, (short) (13 * 256)); ces.setCellStyle(stylet);
	 * ces.setCellValue(""); } if (i == 1) { sheet.autoSizeColumn(i); // 自动调整宽度
	 * } } sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 0, (short)
	 * 0));// 设置单元格合并 sheet.addMergedRegion(new CellRangeAddress(1, (short) 2,
	 * 1, (short) 1)); sheet.addMergedRegion(new CellRangeAddress(1, (short) 1,
	 * 2, (short) 3)); sheet.addMergedRegion(new CellRangeAddress(1, (short) 1,
	 * 4, (short) 6)); sheet.addMergedRegion(new CellRangeAddress(1, (short) 1,
	 * 7, (short) 9)); sheet.addMergedRegion(new CellRangeAddress(1, (short) 1,
	 * 10, (short) 12)); sheet.addMergedRegion(new CellRangeAddress(1, (short)
	 * 1, 13, (short) 15)); sheet.addMergedRegion(new CellRangeAddress(1,
	 * (short) 1, 16, (short) 18)); sheet.addMergedRegion(new
	 * CellRangeAddress(1, (short) 1, 19, (short) 22));
	 * sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 23, (short)
	 * 23)); sheet.addMergedRegion(new CellRangeAddress(1, (short) 2, 24,
	 * (short) 24)); sheet.addMergedRegion(new CellRangeAddress(1, (short) 2,
	 * 25, (short) 25));
	 * 
	 * List<CostSz> list = szHomeMapper.selectSZhomeAllBySupplierId(supplierId,
	 * yearMonth); List<SZDetailShow> detailList = getSZDetailShow(list); if
	 * (detailList == null || list == null) { return null; } for (int i = 3; i <
	 * (detailList.size() + 3); i++) { List ll = new ArrayList();
	 * ll.add(detailList.get(i - 3).getName()); ll.add(detailList.get(i -
	 * 3).getCertificateNumber()); ll.add(detailList.get(i -
	 * 3).getPersonTotal()); ll.add(detailList.get(i - 3).getCompanyTotal());
	 * 
	 * ll.add(detailList.get(i - 3).getPensionPersonTotal());
	 * ll.add(detailList.get(i - 3).getPensionCompanyTotal()); // *********
	 * ll.add(detailList.get(i - 3).getPensionRadix());
	 * 
	 * ll.add(detailList.get(i - 3).getUnemploymentPersonTotal());
	 * ll.add(detailList.get(i - 3).getUnemploymentCompanyTotal());
	 * ll.add(detailList.get(i - 3).getUnemploymentRadix());
	 * 
	 * ll.add(detailList.get(i - 3).getInjuryPersonTotal());
	 * ll.add(detailList.get(i - 3).getInjuryCompanyTotal());
	 * ll.add(detailList.get(i - 3).getInjuryCompanyRadix());
	 * 
	 * ll.add(detailList.get(i - 3).getProcreatePersonTotal());
	 * ll.add(detailList.get(i - 3).getProcreateCompanyTotal());
	 * ll.add(detailList.get(i - 3).getProcreateCompanyRadix());
	 * 
	 * ll.add(detailList.get(i - 3).getMedicalTreatmentPersonTotal());
	 * ll.add(detailList.get(i - 3).getMedicalTreatmentCompanyTotal()); //
	 * ********** ll.add(detailList.get(i - 3).getMedicalTreatmentRadix());
	 * 
	 * ll.add(detailList.get(i - 3).getAccumulationRadix());
	 * ll.add(detailList.get(i - 3).getAccumulationCompanyTotal());
	 * ll.add(detailList.get(i - 3).getAccumulationPersonTotal());
	 * ll.add(detailList.get(i - 3).getAccumulationTotalFee());
	 * 
	 * ll.add(detailList.get(i - 3).getDisabilityBenefit());
	 * ll.add(detailList.get(i - 3).getServiceFee()); ll.add(detailList.get(i -
	 * 3).getTotal()); HSSFRow rowLine = sheet.createRow((short) i);
	 * rowLine.setHeightInPoints(20); sheet.setDefaultColumnWidth(10); for (int
	 * x = 0; x < (strArrs.length + 5); x++) { HSSFCell cc = null; if (x == 0) {
	 * cc = rowLine.createCell(x, CellType.STRING); sheet.setColumnWidth(x,
	 * (short) (13 * 256)); cc.setCellStyle(style); cc.setCellValue(ll.get(x) +
	 * "".trim()); } else if (x == 1) { cc = rowLine.createCell(x,
	 * CellType.STRING); sheet.setColumnWidth(x, (short) (13 * 256));
	 * cc.setCellStyle(style); cc.setCellValue(ll.get(x) + "".trim()); } else {
	 * cc = rowLine.createCell(x, CellType.NUMERIC); sheet.setColumnWidth(x,
	 * (short) (13 * 256)); cc.setCellStyle(style); Object obj = ll.get(x); if
	 * (obj == null) { obj = 0.00; } Double value = (Double) obj;
	 * cc.setCellValue(value); } if (x == 1) { sheet.autoSizeColumn(x); //
	 * 自动调整宽度 } } }
	 * 
	 * HttpServletRequest request = ((ServletRequestAttributes)
	 * RequestContextHolder.getRequestAttributes()) .getRequest(); String
	 * excelPath = request.getSession().getServletContext().getRealPath("/") +
	 * "download" + File.separator + "excel" + File.separator + supplierId +
	 * File.separator; // String path = //
	 * "E:"+File.separator+"ownhomeInfo"+File.separator+supplierId+File.
	 * separator; // System.out.println(
	 * "==============================================================写入成功");
	 * String filePath1 = excelPath + "编号" + supplierId + "的供应商" + yearMonth +
	 * "月社保费用明细.xls"; /// String filePath2 = ///
	 * path+"编号"+supplierId+"的供应商"+yearMonth+"月社保费用明细.xls"; File file1 = new
	 * File(excelPath); // File file2 = new File(path); if (!file1.exists() &&
	 * !file1.isDirectory()) { file1.mkdirs(); }
	 * 
	 * if(!file2.exists() && !file2.isDirectory()){ file2.mkdirs(); }
	 * 
	 * File files1 = new File(filePath1); // File files2 = new File(filePath2);
	 * if (!files1.exists() && !files1.isFile()) { files1.createNewFile(); }
	 * 
	 * if(!files2.exists() && !files2.isFile()){ files2.createNewFile(); }
	 * 
	 * FileOutputStream out1 = null; // FileOutputStream out2=null; try { out1 =
	 * new FileOutputStream(files1); // out2 = new FileOutputStream(files2);
	 * wb.write(out1); // wb.write(out2); out1.flush(); // out2.flush(); return
	 * filePath1; } catch (Exception e) { e.printStackTrace(); return null; }
	 * finally { if (out1 != null) { out1.close(); } out1 = null; // out2=null;
	 * } }
	 */

	// 导出深圳公积金
	@SuppressWarnings("deprecation")
	public String writeSZAccumulationInfo(Integer supplierId, String yearMonth)
			throws EncryptedDocumentException, InvalidFormatException, FileNotFoundException, IOException {
		String city = "深圳";
		String supplier = zaiceService.selectSupplierBySupplierId(city, supplierId);
		if (supplier == null) {
			supplier = "数据为空！";
		}
		Workbook wb = new HSSFWorkbook();
		HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle(); // 样式对象
		HSSFSheet sheet = (HSSFSheet) wb.createSheet(supplierId + "号供应商" + yearMonth + "月(深圳)公积金明细");
		String[] strs = { "序号", "个人公积金账号", "姓名", "身份证号码", "缴存基数", "单位缴存比例", "个人缴存比例", "缴存额", "缴存月份", "客户" };
		/*
		 * style.setFillBackgroundColor(IndexedColors.AQUA.getIndex()); //填满
		 * style.setFillForegroundColor(IndexedColors.ORANGE.getIndex()); //填满前
		 */

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
			rows.getCell(0).setCellValue(yearMonth + "月(深圳)-" + supplier + "公积金明细");
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
			sheet.setColumnWidth(i, (short) (13 * 256));
			cell.setCellStyle(stylet);
			cell.setCellValue(strs[i]);
		}

		List<CostSz> szList = costSzMapper.selectSZhomeAllBySupplierId(supplierId, yearMonth);
		if (szList == null) {
			return null;
		}
		List<SZAccumulation> szAccumulations = new ArrayList<SZAccumulation>();
		String accumulationNumber = null;
		String accumulationRadix = null;
		String accumulationRatio = null;
		for (CostSz szHome : szList) {
			String month = szHome.getReportingPeriod();
			String certificateNum = szHome.getCertificateNumber();
			if (month == null || month == "" || certificateNum == null || certificateNum == "") {
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
			SZAccumulation szAccumulation = new SZAccumulation();
			szAccumulation.setAccumulationNum(accumulationNumber.trim());
			szAccumulation.setName(szHome.getName());
			szAccumulation.setCertificateNum(certificateNum);
			Double payRadix = ((Double.valueOf(accumulationRadix)) == null ? 0
					: ((Double.valueOf(accumulationRadix.trim()))));
			szAccumulation.setPayRadix(payRadix);
			int index = accumulationRatio.indexOf("%");
			int indexs = accumulationRatio.indexOf("+");
			String c_ratio = accumulationRatio.substring(0, index);
			String p_ratio = accumulationRatio.substring(indexs + 1, (accumulationRatio.trim().length()) - 1);
			int c_payRatio = (int) (Double.valueOf((c_ratio.trim())) * 100);
			int p_payRatio = (int) (Double.valueOf(p_ratio.trim()) * 100);
			szAccumulation.setUnitPayRatio(c_payRatio / 10000.00);
			szAccumulation.setPersonPayRatio(p_payRatio / 10000.00);
			// Double payMoney = ((payRadix*100)*c_payRatio)/100.00;
			Double c_payMoney = OwnHomeUtils.mul(payRadix, (c_payRatio / 100.00));
			Double p_payMoney = OwnHomeUtils.mul(payRadix, (p_payRatio / 100.00));
			szAccumulation.setPayMoney(OwnHomeUtils.add(c_payMoney, p_payMoney));

			month = month.replace("-", "").trim();
			szAccumulation.setPayMonth(month + "-" + month);
			String customerNmae = customerService.findCustomerName(szHome.getCustomerId());
			if (customerNmae == null) {
				customerNmae = "*****请检查数据";
			}
			szAccumulation.setCustomerName(customerNmae);
			if (szHome.getName() != null && certificateNum != null) {
				szAccumulations.add(szAccumulation);
			}
		}

		for (int rowNum = 2; rowNum < (szAccumulations.size() + 2); rowNum++) {
			HSSFRow row = sheet.createRow(rowNum);
			row.setHeightInPoints(20);
			HSSFCell cell = row.createCell(0, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(String.valueOf(rowNum - 1));
			cell = row.createCell(1, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(szAccumulations.get(rowNum - 2).getAccumulationNum());
			cell = row.createCell(2, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(szAccumulations.get(rowNum - 2).getName());
			cell = row.createCell(3, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(szAccumulations.get(rowNum - 2).getCertificateNum());
			cell = row.createCell(4, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(szAccumulations.get(rowNum - 2).getPayRadix());
			cell = row.createCell(5, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(szAccumulations.get(rowNum - 2).getUnitPayRatio());
			cell = row.createCell(6, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(szAccumulations.get(rowNum - 2).getPersonPayRatio());
			cell = row.createCell(7, CellType.NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(szAccumulations.get(rowNum - 2).getPayMoney());
			cell = row.createCell(8, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(szAccumulations.get(rowNum - 2).getPayMonth());
			cell = row.createCell(9, CellType.STRING);
			cell.setCellStyle(style);
			cell.setCellValue(szAccumulations.get(rowNum - 2).getCustomerName());
			for (int i = 0; i < 10; i++) {
				sheet.setColumnWidth(i, (short) (16 * 256));
				if (i == 1 || i == 3 || i == 9) {
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

	// 批量删除深圳自有户的信息
	public String deleteSZInfoAll(Integer supplierId, String yearMonth) {
		int a = -1;
		List<CostSz> szList = costSzMapper.selectSZhomeAllBySupplierId(supplierId, yearMonth);
		a = costSzMapper.deleteSZhomeDetails(supplierId, yearMonth);
		if (a > 0 && a == (szList.size())) {
			return "删除成功！";
		}
		return null;
	}

	/**
	 * @param yearMonth
	 * @return 按月份获取供应商费用合计
	 */
	public ResultJson costGroupBySupplier(String yearMonth) {
		if (StringUtils.isEmpty(yearMonth))
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		List<Map<String, Object>> map = costSzMapper.costGroupBySupplier(yearMonth);
		return ResultUtil.error(RestEnum.SUCCESS, map);
	}

}