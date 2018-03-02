package com.dianmi.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianmi.model.Customer;
import com.dianmi.model.EmailRecord;
import com.dianmi.model.EmployeeFuwufangan;
import com.dianmi.model.Fuwufangan;
import com.dianmi.model.Jianyuan;
import com.dianmi.model.JianyuanYichang;
import com.dianmi.model.OperationRecord;
import com.dianmi.model.SupplierTemplate;
import com.dianmi.model.User;
import com.dianmi.model.Zaice;
import com.dianmi.model.Zengyuan;
import com.dianmi.model.ZengyuanYichang;
import com.dianmi.model.ZhangdanMingxi;
import com.dianmi.model.po.JianyuanPo;
import com.dianmi.model.po.ZengyuanPo;
import com.dianmi.service.ZengyuanService;
import com.dianmi.utils.CorrectionUtil;
import com.dianmi.utils.DateTimeUtil;
import com.dianmi.utils.FormatUtil;
import com.dianmi.utils.GetBirthDayFromIDCard;
import com.dianmi.utils.GetSexFromIDCard;
import com.dianmi.utils.ParseUtil;
import com.dianmi.utils.VerificationUtil;
import com.dianmi.utils.file.CopyFile;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.DownloadFile;
import com.dianmi.utils.file.UploadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.dianmi.utils.poi.CreateExcelRow;
import com.dianmi.utils.poi.ReadExcel;
import com.dianmi.utils.validate.IDCardValidate;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;

/**
 * created by www 2017/10/19 15:22
 */
/**
 * @author 15354
 *
 */
@SuppressWarnings("all")
@Service
public class ZengyuanServiceImpl extends CommonService implements ZengyuanService {

	/**
	 * @param file
	 * @return filePath(文件上传后的路径)
	 * @description 上传增减员Excel文件
	 */

	public String getRandomString(int length) {// length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * @param
	 * @return
	 * @description 导入增减员
	 */
	@Transactional
	public String importDate(MultipartFile file, int userId, int deptId, String yearMonth) {
		// 获取上传的增减员Excel文件路径
		String filePath = UploadFile.uploadFile(file);
		// 读取Excel中增减员数据
		List<List<String[]>> dataList = ReadExcel.readRegularExcel2List(filePath);
		DeleteFile.DeleteFolder(filePath);// 上传成功删除Excel模板
		if (dataList.get(0).get(0).length == 21 && dataList.get(1).get(0).length == 11) {
			// 获取模板中增员数据
			List<String[]> zengyuanList = dataList.get(0);
			// 获取减员数据
			List<String[]> jianyuanList = dataList.get(1);
			Date currentDateTime = new Date();
			int[] amount = new int[4];
			String checkCode = getRandomString(16);
			if (zengyuanList.size() > 2) {
				// 遍历Excel中增员数据
				for (int i = 2; i < zengyuanList.size(); i++) {
					String[] zengyuanArr = zengyuanList.get(i);
					String financialAccountingUnit = StringUtils.isEmpty(zengyuanArr[0]) ? "" : zengyuanArr[0].trim();// 财务核算单位
					String customerName = StringUtils.isEmpty(zengyuanArr[1]) ? "" : zengyuanArr[1].trim();// 客户名称
					String employeeName = StringUtils.isEmpty(zengyuanArr[2]) ? "" : zengyuanArr[2].trim();// 员工姓名
					String certificateType = StringUtils.isEmpty(zengyuanArr[3]) ? "" : zengyuanArr[3].trim();// 证件类型
					String certificateNumber = StringUtils.isEmpty(zengyuanArr[4]) ? ""
							: zengyuanArr[4].trim().replaceAll("x", "X");// 证件号码
					String nation = StringUtils.isEmpty(zengyuanArr[5]) ? "汉族" : zengyuanArr[5].trim();// 民族
					String mobilePhone = StringUtils.isEmpty(zengyuanArr[6]) ? "" : zengyuanArr[6].trim();// 联系方式号码
					String isExternalCall = StringUtils.isEmpty(zengyuanArr[7]) ? "" : zengyuanArr[7].trim();// 是否可以外呼
					String householdCity = StringUtils.isEmpty(zengyuanArr[8]) ? "" : zengyuanArr[8].trim();// 户籍地
					String householdProperty = StringUtils.isEmpty(zengyuanArr[9]) ? "" : zengyuanArr[9].trim();// 户口性质
					String city = StringUtils.isEmpty(zengyuanArr[10]) ? ""
							: CorrectionUtil.correctionCity(zengyuanArr[10].trim());// 参保城市
					String socialSecurityCardNumber = StringUtils.isEmpty(zengyuanArr[11]) ? ""
							: zengyuanArr[11].trim();// 社保/医保卡号
					String socialSecurityBase = StringUtils.isEmpty(zengyuanArr[12]) ? "" : zengyuanArr[12].trim();// 社保基数
					String socialSecurityType = StringUtils.isEmpty(zengyuanArr[13]) ? "" : zengyuanArr[13].trim();// 参保类型
					String supplier = StringUtils.isEmpty(zengyuanArr[14]) ? "" : zengyuanArr[14].trim();// 供应商
					String socialSecurityBeginTime = StringUtils.isEmpty(zengyuanArr[15]) ? ""
							: FormatUtil.yearMonth(zengyuanArr[15].trim());// 社保起缴年月
					String accumulationFundNumber = StringUtils.isEmpty(zengyuanArr[16]) ? "" : zengyuanArr[16].trim();// 公积金账号
					String accumulationFundCardinalNumber = StringUtils.isEmpty(zengyuanArr[17]) ? ""
							: zengyuanArr[17].trim();// 公积金基数
					String accumulationFundRatio = StringUtils.isEmpty(zengyuanArr[18]) ? "" : zengyuanArr[18].trim();// 公积金比例
					String accumulationFundBeginTime = StringUtils.isEmpty(zengyuanArr[19]) ? ""
							: zengyuanArr[19].trim();// 公积金起缴年月
					String remark = StringUtils.isEmpty(zengyuanArr[20]) ? "" : zengyuanArr[20].trim();// 备注信息
					if (StringUtils.isEmpty(zengyuanArr[0]) || StringUtils.isEmpty(zengyuanArr[1])
							|| StringUtils.isEmpty(zengyuanArr[2]) || StringUtils.isEmpty(zengyuanArr[3])
							|| StringUtils.isEmpty(zengyuanArr[4]) || StringUtils.isEmpty(zengyuanArr[10])
							|| StringUtils.isEmpty(zengyuanArr[12]) || StringUtils.isEmpty(zengyuanArr[15])) {
						// return "增员模板第" + (i + 1) + "行有必填信息未填写";
						// 记录操作异常信息
						zengyuanYichangMapper.insert(new ZengyuanYichang(userId, deptId, financialAccountingUnit,
								customerName, employeeName, certificateType, certificateNumber, nation, mobilePhone,
								isExternalCall, householdCity, householdProperty, city, socialSecurityCardNumber,
								socialSecurityBase, socialSecurityType, supplier, socialSecurityBeginTime,
								accumulationFundNumber, accumulationFundCardinalNumber, accumulationFundRatio,
								accumulationFundBeginTime, currentDateTime, remark, yearMonth, checkCode, "有必填信息未填写"));
						amount[1]++;
					} else if (!IDCardValidate.validate(zengyuanArr[4])) {
						// return "增员模板第" + (i + 1) + "行身份证格式填写有误";
						// 记录操作异常信息
						zengyuanYichangMapper.insert(new ZengyuanYichang(userId, deptId, financialAccountingUnit,
								customerName, employeeName, certificateType, certificateNumber, nation, mobilePhone,
								isExternalCall, householdCity, householdProperty, city, socialSecurityCardNumber,
								socialSecurityBase, socialSecurityType, supplier, socialSecurityBeginTime,
								accumulationFundNumber, accumulationFundCardinalNumber, accumulationFundRatio,
								accumulationFundBeginTime, currentDateTime, remark, yearMonth, checkCode, "身份证号码格式错误"));
						amount[1]++;
					} else if (!VerificationUtil.checkHouseholdType(zengyuanArr[9])) {
						// return "增员模板第" + (i + 1) + "行户口性质填写有误";
						zengyuanYichangMapper.insert(new ZengyuanYichang(userId, deptId, financialAccountingUnit,
								customerName, employeeName, certificateType, certificateNumber, nation, mobilePhone,
								isExternalCall, householdCity, householdProperty, city, socialSecurityCardNumber,
								socialSecurityBase, socialSecurityType, supplier, socialSecurityBeginTime,
								accumulationFundNumber, accumulationFundCardinalNumber, accumulationFundRatio,
								accumulationFundBeginTime, currentDateTime, remark, yearMonth, checkCode, "户口性质不符合规范"));
						amount[1]++;
					} else if (!VerificationUtil.checkSocialSecurityCardinalNumber(zengyuanArr[12])) {
						// return "增员模板第" + (i + 1) + "行社保基数填写有误";
						zengyuanYichangMapper.insert(new ZengyuanYichang(userId, deptId, financialAccountingUnit,
								customerName, employeeName, certificateType, certificateNumber, nation, mobilePhone,
								isExternalCall, householdCity, householdProperty, city, socialSecurityCardNumber,
								socialSecurityBase, socialSecurityType, supplier, socialSecurityBeginTime,
								accumulationFundNumber, accumulationFundCardinalNumber, accumulationFundRatio,
								accumulationFundBeginTime, currentDateTime, remark, yearMonth, checkCode, "社保基数填写错误"));
						amount[1]++;
					} else if (!VerificationUtil.checkYearMonth(zengyuanArr[15])) {
						// return "增员模板第" + (i + 1) + "行社保起缴年月填写有误";
						zengyuanYichangMapper.insert(new ZengyuanYichang(userId, deptId, financialAccountingUnit,
								customerName, employeeName, certificateType, certificateNumber, nation, mobilePhone,
								isExternalCall, householdCity, householdProperty, city, socialSecurityCardNumber,
								socialSecurityBase, socialSecurityType, supplier, socialSecurityBeginTime,
								accumulationFundNumber, accumulationFundCardinalNumber, accumulationFundRatio,
								accumulationFundBeginTime, currentDateTime, remark, yearMonth, checkCode,
								"社保起缴年月格式填写错误"));
						amount[1]++;
					} else {
						// 根据部门id和客户姓名来获取客户id
						Integer customerId = customerService.getIdByDeptIdAndCustomerName(deptId, customerName);
						// 如果客户不存在则创建客户
						if (null == customerId) {
							Customer customer = new Customer(deptId, customerName, "", "", (byte) 0, 0.0, (byte) 0);
							customerService.insert(customer);
							customerId = customer.getCId();
						}
						// 判断本月员工在某个城市是否已经增员
						if (isZengyuanExists(city, yearMonth, certificateNumber)) {
							// 记录操作异常信息
							zengyuanYichangMapper.insert(new ZengyuanYichang(userId, deptId, financialAccountingUnit,
									customerName, employeeName, certificateType, certificateNumber, nation, mobilePhone,
									isExternalCall, householdCity, householdProperty, city, socialSecurityCardNumber,
									socialSecurityBase, socialSecurityType, supplier, socialSecurityBeginTime,
									accumulationFundNumber, accumulationFundCardinalNumber, accumulationFundRatio,
									accumulationFundBeginTime, currentDateTime, remark, yearMonth, checkCode,
									"增员信息已存在"));
							// 参数：操作用户id、城市、客户id、当前时间、员工姓名、员工身份证号码、信息所属年月
							amount[1]++;
							// 未增员保存增员信息
						} else {
							Integer supplierId = 1;
							Integer fuwufanganId = 1;
							// 增员表中供应商列已指定供应商
							if (!StringUtils.isEmpty(supplier)) {
								// 根据城市和供应商名称模糊匹配供应商信息
								List<Integer> supplierIds = supplierMapper.selectBySupplierMsg("%" + city + "%",
										"%" + supplier + "%");
								if (null != supplierIds) {
									// 当存在唯一供应商
									if (supplierIds.size() == 1) {
										supplierId = supplierIds.get(0);
									} else if (supplierIds.size() == 0) {
										return "增员模板中第" + (i + 1) + "行指定的供应商在系统不存在";
									}
								}
								List<Integer> fuwufanganIds = fuwufanganMapper.selectByCustomerMsg(city, customerId,
										supplierId);
								if (fuwufanganIds.size() == 1) {
									fuwufanganId = fuwufanganIds.get(0);
								}
							}
							// 增员表中员工信息当月在册则删除在册（当修改用户险种信息增员进来）
							List<Integer> zcIdList = zaiceMapper.isZaiceExists(yearMonth, city, certificateNumber);
							if (zcIdList.size() > 0) {
								int zcId = zcIdList.get(0);
								zaiceMapper.updateDeleteFlagStatus(1, zcId);
							}
							Zengyuan zengyuan = new Zengyuan(userId, deptId, customerId, supplierId, fuwufanganId,
									financialAccountingUnit, customerName, employeeName, certificateType,
									certificateNumber, nation, mobilePhone, isExternalCall, householdCity,
									householdProperty, city, socialSecurityCardNumber, socialSecurityBase,
									socialSecurityType, supplier, socialSecurityBeginTime, accumulationFundNumber,
									accumulationFundCardinalNumber, accumulationFundRatio, accumulationFundBeginTime,
									currentDateTime, remark, yearMonth, (byte) 0);
							if (addZengyuan(zengyuan) == 1)
								amount[0]++;
						}
					}
				}
			}
			if (jianyuanList.size() > 2) {
				// 遍历减员数据
				for (int j = 2; j < jianyuanList.size(); j++) {
					String[] jianyuanArr = jianyuanList.get(j);
					String customerName = StringUtils.isEmpty(jianyuanArr[0]) ? "" : jianyuanArr[0].trim();
					String employeeName = StringUtils.isEmpty(jianyuanArr[1]) ? "" : jianyuanArr[1].trim();
					String certificateType = StringUtils.isEmpty(jianyuanArr[2]) ? "" : jianyuanArr[2].trim();
					String certificateNumber = StringUtils.isEmpty(jianyuanArr[3]) ? ""
							: jianyuanArr[3].trim().replaceAll("x", "X");
					String city = StringUtils.isEmpty(jianyuanArr[4]) ? ""
							: CorrectionUtil.correctionCity(jianyuanArr[4].trim());
					String dimissionReason = StringUtils.isEmpty(jianyuanArr[5]) ? "" : jianyuanArr[5].trim();
					String dimissionDate = StringUtils.isEmpty(jianyuanArr[6]) ? "" : jianyuanArr[6].trim();
					String socialSecurityStopDate = StringUtils.isEmpty(jianyuanArr[7]) ? ""
							: FormatUtil.yearMonth(jianyuanArr[7].trim());
					String accumulationFundStopDate = StringUtils.isEmpty(jianyuanArr[8]) ? ""
							: FormatUtil.yearMonth(jianyuanArr[8].trim());
					String supplier = StringUtils.isEmpty(jianyuanArr[9]) ? "" : jianyuanArr[9].trim();
					String remark = StringUtils.isEmpty(jianyuanArr[10]) ? "" : jianyuanArr[10].trim();
					if (StringUtils.isEmpty(jianyuanArr[0]) || StringUtils.isEmpty(jianyuanArr[1])
							|| StringUtils.isEmpty(jianyuanArr[2]) || StringUtils.isEmpty(jianyuanArr[3])
							|| StringUtils.isEmpty(jianyuanArr[4]) || StringUtils.isEmpty(jianyuanArr[6])
							|| StringUtils.isEmpty(jianyuanArr[7]) || StringUtils.isEmpty(jianyuanArr[8])) {
						// return "减员模板中有必填信息未填写";
						// 记录操作异常信息
						jianyuanYichangMapper.insert(new JianyuanYichang(userId, deptId, customerName, employeeName,
								certificateType, certificateNumber, city, dimissionReason, dimissionDate,
								socialSecurityStopDate, accumulationFundStopDate, supplier, remark, currentDateTime,
								yearMonth, checkCode, "有必填信息未填写"));
						amount[3]++;
						// 判断减员信息是否存在
					} else if (!IDCardValidate.validate(jianyuanArr[3])) {
						// return "减员模板第" + (j + 1) + "行身份证号码填写有误";
						jianyuanYichangMapper.insert(new JianyuanYichang(userId, deptId, customerName, employeeName,
								certificateType, certificateNumber, city, dimissionReason, dimissionDate,
								socialSecurityStopDate, accumulationFundStopDate, supplier, remark, currentDateTime,
								yearMonth, checkCode, "身份证号码格式错误"));
						amount[3]++;
					} else if (!VerificationUtil.checkYearMonth(jianyuanArr[7])) {
						// return "减员模板第" + (j + 1) + "行社保停缴年月填写有误";
						jianyuanYichangMapper.insert(new JianyuanYichang(userId, deptId, customerName, employeeName,
								certificateType, certificateNumber, city, dimissionReason, dimissionDate,
								socialSecurityStopDate, accumulationFundStopDate, supplier, remark, currentDateTime,
								yearMonth, checkCode, "社保停缴年月填写有误"));
						amount[3]++;
					} else if (!VerificationUtil.checkYearMonth(jianyuanArr[8])) {
						// return "减员模板第" + (j + 1) + "行公积金停缴年月填写有误";
						jianyuanYichangMapper.insert(new JianyuanYichang(userId, deptId, customerName, employeeName,
								certificateType, certificateNumber, city, dimissionReason, dimissionDate,
								socialSecurityStopDate, accumulationFundStopDate, supplier, remark, currentDateTime,
								yearMonth, checkCode, "公积金停缴年月有误"));
						amount[3]++;
					} else {
						// 根据部门id和客户姓名来获取客户id
						Integer customerId = customerService.getIdByDeptIdAndCustomerName(deptId, customerName);
						// 如果客户不存在则创建客户
						if (null == customerId) {
							Customer customer = new Customer(deptId, customerName, "", "", (byte) 0, 0.0, (byte) 0);
							customerService.insert(customer);
							customerId = customer.getCId();
						}
						if (isJianyuanExists(city, yearMonth, certificateNumber)) {// 参数：城市、月份、员工身份证号码
							// 记录操作异常信息
							jianyuanYichangMapper.insert(new JianyuanYichang(userId, deptId, customerName, employeeName,
									certificateType, certificateNumber, city, dimissionReason, dimissionDate,
									socialSecurityStopDate, accumulationFundStopDate, supplier, remark, currentDateTime,
									yearMonth, checkCode, "减员信息已经存在"));
							amount[3]++;
							// 参数：userId、城市、客户id、当前时间、员工姓名、身份证号码、月份
						} else {
							// 在册中是否存在
							Zaice zaice = zaiceService.selectByEmployeeMsg(city, yearMonth, certificateNumber);
							if (zaice == null) {
								jianyuanYichangMapper.insert(new JianyuanYichang(userId, deptId, customerName,
										employeeName, certificateType, certificateNumber, city, dimissionReason,
										dimissionDate, socialSecurityStopDate, accumulationFundStopDate, supplier,
										remark, currentDateTime, yearMonth, checkCode, "减员信息没有在册"));
								amount[3]++;
							} else {
								Jianyuan jianyuan = new Jianyuan(userId, deptId, customerId, customerName, employeeName,
										certificateType, certificateNumber, city, dimissionReason, dimissionDate,
										socialSecurityStopDate, accumulationFundStopDate, supplier, remark,
										currentDateTime, yearMonth, (byte) 0);
								if (addJianyuan(jianyuan) == 1)
									amount[2]++;
							}
						}
					}
				}
			}
			if (zengyuanList.size() > 2) {
				for (int index = 2; index < zengyuanList.size(); index++) {
					String[] zengyuanArr = zengyuanList.get(index);
					String city = zengyuanArr[10].trim();
					String certificateNumber = zengyuanArr[4].trim().replaceAll("x", "X");
					// 创建社保预期账单,参数：城市、年份、证件号
					createYuqiZhangdan(CorrectionUtil.correctionCity(city), yearMonth, certificateNumber);
				}
			}
			// 记录操作记录
			operationRecordService
					.insert(new OperationRecord(userId, deptId, yearMonth, currentDateTime, (byte) 0, "导入增减员"));
			return "导入增员" + amount[0] + "条，失败" + amount[1] + "条，导入减员" + amount[2] + "条，失败" + amount[3] + "条";
		} else {
			return "文件错误，请上传增减员模板";
		}
	}

	/**
	 * @param
	 * @return
	 * @description 新增增员信息
	 */
	public int addZengyuan(Zengyuan zengyuan) {
		return zengyuanMapper.insert(zengyuan);
	}

	/**
	 * @param
	 * @return
	 * @description 新增减员信息
	 */
	public int addJianyuan(Jianyuan jianyuan) {
		return jianyuanMapper.insert(jianyuan);
	}

	public boolean isZengyuanExists(String city, String reportingPeriod, String certificateNumber) {
		List<Map<String, Object>> list = zengyuanMapper.selectIsExistsByCertificateNumber(city, reportingPeriod,
				certificateNumber);
		if (list.size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * @param
	 * @return
	 * @description 判断减员是否存在
	 */
	public boolean isJianyuanExists(String city, String reportingPeriod, String certificateNumber) {
		List<Map<String, Object>> list = jianyuanMapper.isExistsByCertificateNumber(city, reportingPeriod,
				certificateNumber);
		if (list.size() > 0)
			return true;
		else
			return false;
	}

	// 生成预期账单
	public void createYuqiZhangdan(String chengshi, String yuefen, String zhengjianhao) {
		// 根據城市、月份、身份证号获取在册信息
		Zaice zaice = zaiceService.selectByEmployeeMsg(chengshi, yuefen, zhengjianhao);
		if (zaice != null) {
			// int userId = zaice.getUserId();
			String city = zaice.getCity();
			int customerId = zaice.getCustomerId();
			String householdProperty = zaice.getHouseholdProperty();
			String socialSecurityType = zaice.getSocialSecurityType();
			String reportingPeriod = zaice.getReportingPeriod();
			// String employeeName = zaice.getEmployeeName();
			String certificateNumber = zaice.getCertificateNumber();
			String gongjijinJishuStr = zaice.getAccumulationFundCardinalNumber();
			String accumulationFundRatio = zaice.getAccumulationFundRatio();// 5%+5%
			double accmulationFundCompanyPay = 0;
			double accmulationFundPersonPay = 0;
			if ((!StringUtils.isEmpty(accumulationFundRatio)) && (!StringUtils.isEmpty(gongjijinJishuStr))) {
				float[] ratioArr = ParseUtil.parseGongjijinStrRatioToFloat(accumulationFundRatio);
				float gongjijinJishu = Float.parseFloat(gongjijinJishuStr);
				accmulationFundCompanyPay = gongjijinJishu * ratioArr[0];
				accmulationFundPersonPay = gongjijinJishu * ratioArr[1];
			}
			// 当员工参加社保的城市是深圳
			if (city.equals("深圳") && socialSecurityType.contains("一档")) {
				socialSecurityType = "医疗一档";
			} else if (city.equals("深圳") && socialSecurityType.contains("综合")) {
				socialSecurityType = "综合医疗";
			} else if (city.equals("深圳") && socialSecurityType.contains("住院")) {
				socialSecurityType = "住院医疗";
			} else if (city.equals("深圳") && socialSecurityType.contains("农民工")) {
				socialSecurityType = "农民工医疗";
			} else {
				socialSecurityType = "";
			}
			Fuwufangan fuwufangan = null;
			int fuwufanganId = zaice.getFuwufanganId();
			if (fuwufanganId != 1) {
				fuwufangan = fuwufanganMapper.selectByPrimaryKey(fuwufanganId);
			} else {
				// 根据参保城市、客户id、户口性质、参保类型（深圳）获取服务方案信息
				List<Fuwufangan> fuwufanganList = fuwufanganMapper.selectBySocialMsg(city, customerId,
						"%" + householdProperty + "%", "%" + socialSecurityType + "%");
				if (fuwufanganList.size() == 1) {
					fuwufangan = fuwufanganList.get(0);
				}
			}
			if (null != fuwufangan) {
				// 匹配到服务方案修改增员表中用户服务方案信息
				updateFuwufanganMsg(fuwufangan.getSupplierId(), fuwufangan.getFwId(), reportingPeriod, city,
						certificateNumber);
				int zaiceId = zaice.getZcId();
				double yanglaoGongsi = fuwufangan.getPensionCompanyPay();
				double yanglaoGeren = fuwufangan.getPensionPersonPay();
				double shiyeGongsi = fuwufangan.getUnemploymentCompanyPay();
				double shiyeGeren = fuwufangan.getUnemploymentPersonPay();
				double gongshangGongsi = fuwufangan.getInjuryCompanyPay();
				double shengyuGongsi = fuwufangan.getProcreateCompanyPay();
				double jibenYiliaoGongsi = fuwufangan.getMedicalCompanyPay();
				double jibenYiliaoGeren = fuwufangan.getMedicalPersonPay();
				double dabingYiliaoGongsi = fuwufangan.getSeriousIllnessCompanyPay();
				double dabingYiliaoGeren = fuwufangan.getSeriousIllnessPersonPay();
				double gongjijinGongsi = accmulationFundCompanyPay;
				double gongjijinGeren = accmulationFundPersonPay;
				double canbaojinGongsi = fuwufangan.getDisabilityGuaranteeFund();
				double qitaGongsi = fuwufangan.getOtherCompanyPay();
				double qitaGeren = fuwufangan.getOtherPersonPay();
				double fuwufei = fuwufangan.getServiceCharge();
				// 生成员工服务方案信息
				employeeFuwufanganMapper
						.insert(new EmployeeFuwufangan(zaiceId, yanglaoGongsi, yanglaoGeren, jibenYiliaoGongsi,
								jibenYiliaoGeren, shiyeGongsi, shiyeGeren, dabingYiliaoGongsi, dabingYiliaoGeren,
								shengyuGongsi, gongjijinGongsi, fuwufei, canbaojinGongsi, qitaGongsi, qitaGeren));
				// 生成员工账单
				zhangdanMingxiMapper.insert(new ZhangdanMingxi(zaiceId, yanglaoGongsi, yanglaoGeren, jibenYiliaoGongsi,
						jibenYiliaoGeren, dabingYiliaoGongsi, dabingYiliaoGeren, shiyeGongsi, shiyeGeren,
						gongshangGongsi, shengyuGongsi, gongjijinGongsi, gongjijinGeren, qitaGongsi, qitaGeren,
						canbaojinGongsi, fuwufei, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
						0.0, 0.0));
			} else {
				// 没有匹配服务方案
			}
		}
	}

	/**
	 * 分组
	 * 
	 * @param list
	 * @param factor
	 * @return
	 */
	public Map<String, List<Map<String, Object>>> classifyData(List<Map<String, Object>> list, String factor) {
		// 分类后返回的数据集合
		Map<String, List<Map<String, Object>>> maps = new HashMap<>();
		// 遍历数据
		for (Map<String, Object> object : list) {
			// 临时存放分类数据集合
			List<Map<String, Object>> tempList = maps.get(String.valueOf(object.get(factor)));
			if (tempList == null) {
				tempList = new ArrayList<>();
				tempList.add(object);
				maps.put(String.valueOf(object.get(factor)), tempList);
			} else {
				tempList.add(object);
			}
		}
		return maps;
	}

	/**
	 * @param
	 * @return
	 * @description 确定增员操作
	 */
	public int confirmZengyuan(int userId, int operationRecordId) {
		return zengyuanMapper.updateStatusToConfirm(userId, operationRecordId);
	}

	/**
	 * @param
	 * @return
	 * @description 确定减员操作
	 */
	public int confirmJianyuan(int userId, int operationRecordId) {
		return jianyuanMapper.updateStatusToConfirm(userId, operationRecordId);
	}

	public List<Zaice> zaiceSocialMsg(int userId, int operationRecordId) {
		return zaiceMapper.selectBySocialMsg(userId, operationRecordId);
	}

	public int updateFuwufanganMsg(int supplierId, int fuwufanganId, String reportingPeriod, String city,
			String certificateNumber) {
		return zengyuanMapper.updateFuwufanganMsg(supplierId, fuwufanganId, reportingPeriod, city, certificateNumber);
	}

	public List<ZengyuanPo> zengyuanPaging(String reportingPeriod, String paramName, int supplierId) {
		List<ZengyuanPo> list = zengyuanMapper.zengyuanPaging(reportingPeriod, "%" + paramName + "%", supplierId);
		return list;
	}

	public List<Map<String, Object>> selectSupplier(String reportingPeriod, int userId, int roleType) {
		return zaiceMapper.selectAllSupplier(reportingPeriod, userId, roleType);
	}

	public Map<String, Object> currMonthTotalZengjianyuan(String reportingPeriod, int supplierId, int userId,
			byte type) {
		int zengyuanTotal = zengyuanMapper.countZengyuanTotal(reportingPeriod, supplierId);
		int jianyuanTotal = jianyuanMapper.countJianyuanTotal(reportingPeriod, supplierId);
		Map<String, Object> map = new HashMap<>();
		map.put("zengyuanTotal", zengyuanTotal);
		map.put("jianyuanTotal", jianyuanTotal);
		return map;
	}

	@Transactional
	public String sendEmailToSupplier(String reportingPeriod, int supplierId, User user) {
		Map<String, Object> maps = getZengjianyuanBySupplier(reportingPeriod, supplierId, user);
		boolean isSendEmail = (boolean) maps.get("isSendEmail");
		if (isSendEmail) {
			String filePath = maps.get("filePath").toString();
			// Supplier supplier =
			// supplierMapper.selectByPrimaryKey(supplierId);
			EmailAttachment attachment = new EmailAttachment();// 附件
			attachment.setPath(filePath);// 附件地址
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("" + reportingPeriod + "增减员.xlsx");// 附件描述信息
			attachment.setName("" + reportingPeriod + "增减员.xlsx");// 附件名
			MultiPartEmail email = new MultiPartEmail();
			email.setSmtpPort(587);
			email.setHostName("smtp.qq.com");
			email.setAuthentication("153549111@qq.com", "mqlcrwjntpugbggg");
			email.setHostName("smtp.qq.com");
			String[] emailTo = new String[] { "153549111@qq.com", "luojun@dianmi365.com" };
			String[] emailCc = new String[] { "153549111@qq.com", "153549111@qq.com" };
			String[] emailFrom = new String[] { "153549111@qq.com", "业务支持部" };
			String subject = "增减员数据(" + reportingPeriod + ")";
			String msg = "增减员数据请查收!";
			try {
				// email.addTo(new String[] {
				// supplier.getSupplierDockingPeopleEmail() });//供应商实操对接人邮箱
				// email.addCc(new String[] {
				// supplier.getSupplierDockingPeopleEmail(),
				// supplier.getSupplierEmail() });
				// email.setFrom(user.getEmail(), user.getName());
				email.addTo(emailTo);// 供应商实操对接人邮箱
				email.addCc(emailCc);// 邮件抄送人
				email.setFrom(emailFrom[0], emailFrom[1]);// 邮件发送者
				email.setSubject(subject);// 邮件主题
				email.setMsg(msg);// 邮件内容
				email.attach(attachment);// 邮件附件
				email.send();
				DeleteFile.DeleteFolder(filePath);
				EmailRecord emailRecord = new EmailRecord(user.getUId(), Arrays.toString(emailTo),
						Arrays.toString(emailCc), new Date(), subject, reportingPeriod, msg);
				emailRecordMapper.insert(emailRecord);
				return "邮件发送成功";
			} catch (EmailException e) {
				e.printStackTrace();
				return "邮件发送失败";
			}
		} else {
			return maps.get("result").toString();
		}
	}

	/**
	 * 按供应商生成增减员excel文件
	 * 
	 * @param reportingPeriod
	 * @param supplierId
	 * @param user
	 * @return
	 */
	public Map<String, Object> getZengjianyuanBySupplier(String reportingPeriod, int supplierId, User user) {
		List<ZengyuanPo> zengyuanList = zengyuanMapper.selectBySupplierMsg(reportingPeriod, supplierId);
		List<JianyuanPo> jianyuanList = jianyuanMapper.selectBySupplierMsg(reportingPeriod, supplierId);
		Map<String, Object> map = new HashMap<String, Object>();
		if (zengyuanList.size() == 0 && jianyuanList.size() == 0) {
			map.put("isSendEmail", false);
			map.put("result", "没有可发送的增减员");
		} else {
			try {
				SupplierTemplate template = template(supplierId);
				String templateName;
				String templatePath;
				if (null == template) {
					templateName = "北京点米";
					templatePath = "北京点米增减派单模板表.xlsx";
				} else {
					templateName = template.getTemplateName();
					templatePath = template.getFilePath();
				}
				String fromFilePath = realPath() + "template" + File.separator + "supplier" + File.separator
						+ templatePath;
				String toFilePath = realPath() + "download" + File.separator + "excel" + File.separator
						+ UUID.randomUUID().toString() + templatePath;
				CopyFile.copyFile(fromFilePath, toFilePath);
				String currentDate = DateTimeUtil.currentDateStr();
				CreateExcelRow cer = new CreateExcelRow();
				switch (templateName) {
				case "广东方胜":
					for (int i1 = 0; i1 < zengyuanList.size(); i1++) {
						String[] zengyuanArr = new String[27];
						ZengyuanPo zengyuanPo = zengyuanList.get(i1);
						zengyuanArr[0] = String.valueOf(zengyuanList.size() - i1);// 序号
						zengyuanArr[1] = "";// 组别
						zengyuanArr[2] = "";// 业务员
						zengyuanArr[3] = zengyuanPo.getCustomerName();// 单位名称
						zengyuanArr[4] = "";// 服务合同委托类型
						zengyuanArr[5] = zengyuanPo.getEmployeeName();// 员工姓名
						zengyuanArr[6] = zengyuanPo.getCertificateNumber();// 身份证号码
						zengyuanArr[7] = zengyuanPo.getMobilePhone();// 联系方式
						zengyuanArr[8] = zengyuanPo.getCity();// 社保缴纳地区
						zengyuanArr[9] = zengyuanPo.getSupplierName();// 供应商
						zengyuanArr[10] = "";// 入职时间
						zengyuanArr[11] = zengyuanPo.getHouseholdProperty();// 户籍类型
						zengyuanArr[12] = zengyuanPo.getSocialSecurityBeginTime();// 社保缴费月份
						zengyuanArr[13] = "";// 社保补缴月份
						zengyuanArr[14] = zengyuanPo.getSocialSecurityBase();// 社保基数
						zengyuanArr[15] = "";// 社保险种
						zengyuanArr[16] = zengyuanPo.getAccumulationFundBeginTime();// 公积金缴费月份
						zengyuanArr[17] = "";// 公积金补缴月份
						zengyuanArr[18] = zengyuanPo.getAccumulationFundCardinalNumber();// 公积金基数
						if ((!StringUtil.isEmpty(zengyuanPo.getAccumulationFundRatio()))
								&& (!StringUtils.isEmpty(zengyuanArr[18]))) {
							float[] gongjijinBili = ParseUtil
									.parseGongjijinStrRatioToFloat(zengyuanPo.getAccumulationFundRatio());// 公积金基数
							zengyuanArr[19] = String.valueOf(gongjijinBili[0] * 100) + "%";// 公积金单位比例
							zengyuanArr[20] = String.valueOf(gongjijinBili[1] * 100) + "%";// 公积金个人比例
							zengyuanArr[21] = String.valueOf(gongjijinBili[0] * Float.parseFloat(zengyuanArr[18]));// 单位应缴
							zengyuanArr[22] = String.valueOf(gongjijinBili[1] * Float.parseFloat(zengyuanArr[18]));// 个人应缴
						} else {
							zengyuanArr[19] = "";
							zengyuanArr[20] = "";
							zengyuanArr[21] = "";
							zengyuanArr[22] = "";
						}
						zengyuanArr[23] = "";
						zengyuanArr[24] = "";
						zengyuanArr[25] = "";
						zengyuanArr[26] = "";
						cer.insertRows(toFilePath, "增员", 3, zengyuanArr);
						zengyuanMapper.updateStatus(zengyuanPo.getId());
					}
					for (int i2 = 0; i2 < jianyuanList.size(); i2++) {
						String[] jianyuanArr = new String[12];
						JianyuanPo jianyuanPo = jianyuanList.get(i2);
						jianyuanArr[0] = String.valueOf(jianyuanList.size() - i2);// 序号
						jianyuanArr[1] = "";// 组别
						jianyuanArr[2] = "";// 业务员
						jianyuanArr[3] = jianyuanPo.getClientName();// 单位名称
						jianyuanArr[4] = jianyuanPo.getEmployeeName();// 姓名
						jianyuanArr[5] = jianyuanPo.getCertificateNumber();// 身份证号码
						jianyuanArr[6] = jianyuanPo.getDimissionReason();// 离职类型
						jianyuanArr[7] = jianyuanPo.getCity();// 社保缴纳地区
						jianyuanArr[8] = jianyuanPo.getDimissionDate();// 离职时间
						jianyuanArr[9] = jianyuanPo.getSocialSecurityStopDate();// 社保停缴时间
						jianyuanArr[10] = jianyuanPo.getAccumulationFundStopDate();// 公积金停缴时间
						jianyuanArr[11] = jianyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "减员", 3, jianyuanArr);
						jianyuanMapper.updateStatus(jianyuanPo.getJyId());
					}
					break;
				case "广东南油":
					for (int i1 = 0; i1 < zengyuanList.size(); i1++) {
						String[] zengyuanArr = new String[19];
						ZengyuanPo zengyuanPo = zengyuanList.get(i1);
						zengyuanArr[0] = String.valueOf(zengyuanList.size() - i1);// 序号
						zengyuanArr[1] = zengyuanPo.getReportingPeriod();// 报送月份
						zengyuanArr[2] = user.getName();// 报送人
						zengyuanArr[3] = zengyuanPo.getCustomerName();// 公司
						zengyuanArr[4] = zengyuanPo.getEmployeeName();// 员工姓名
						zengyuanArr[5] = zengyuanPo.getCertificateNumber();// 身份证号码
						zengyuanArr[6] = zengyuanPo.getCity();// 社保缴纳地
						zengyuanArr[7] = zengyuanPo.getSocialSecurityType();// 社保类型
						zengyuanArr[8] = zengyuanPo.getSocialSecurityBase();// 社保基数
						zengyuanArr[9] = zengyuanPo.getSocialSecurityBeginTime();// 社保缴费起始年月
						zengyuanArr[10] = zengyuanPo.getCity();// 公积金缴纳地
						zengyuanArr[11] = zengyuanPo.getAccumulationFundCardinalNumber();// 公积金基数
						if ((!StringUtil.isEmpty(zengyuanPo.getAccumulationFundRatio()))
								&& (!StringUtil.isEmpty(zengyuanArr[11]))) {
							float[] gongjijinBili = ParseUtil
									.parseGongjijinStrRatioToFloat(zengyuanPo.getAccumulationFundRatio());
							zengyuanArr[12] = String.valueOf(gongjijinBili[0] * 100) + "%";// 单位比例
							zengyuanArr[13] = String.valueOf(gongjijinBili[1] * 100) + "%";// 个人比例
						} else {
							zengyuanArr[12] = "";
							zengyuanArr[13] = "";
						}
						zengyuanArr[14] = zengyuanPo.getAccumulationFundBeginTime();// 公积金缴费起始年月
						zengyuanArr[15] = zengyuanPo.getHouseholdProperty();// 户籍性质
						zengyuanArr[16] = "";// 户籍所属省市县
						zengyuanArr[17] = "";// 劳动合同起止时间
						zengyuanArr[18] = zengyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "增员通知", 9, zengyuanArr);
						zengyuanMapper.updateStatus(zengyuanPo.getId());
					}
					for (int i2 = 0; i2 < jianyuanList.size(); i2++) {
						String[] jianyuanArr = new String[10];
						JianyuanPo jianyuanPo = jianyuanList.get(i2);
						jianyuanArr[0] = String.valueOf(jianyuanList.size() - i2);// 序号
						jianyuanArr[1] = jianyuanPo.getReportingPeriod();// 报送月份
						jianyuanArr[2] = user.getName();// 报送人
						jianyuanArr[3] = jianyuanPo.getCity();// 所属地区
						jianyuanArr[4] = jianyuanPo.getClientName();// 所属公司
						jianyuanArr[5] = jianyuanPo.getEmployeeName();// 员工姓名
						jianyuanArr[6] = jianyuanPo.getCertificateNumber();// 身份证号码
						jianyuanArr[7] = jianyuanPo.getSocialSecurityStopDate();// 社保停买年月
						jianyuanArr[8] = jianyuanPo.getAccumulationFundStopDate();// 公积金停买年月
						jianyuanArr[9] = jianyuanPo.getRemark();// 备注信息
						cer.insertRows(toFilePath, "减员通知", 5, jianyuanArr);
						jianyuanMapper.updateStatus(jianyuanPo.getJyId());
					}
					break;
				case "河南成就":
					for (int i1 = 0; i1 < zengyuanList.size(); i1++) {
						String[] zengyuanArr = new String[11];
						ZengyuanPo zengyuanPo = zengyuanList.get(i1);
						zengyuanArr[0] = String.valueOf(zengyuanList.size() - i1);// 序号
						zengyuanArr[1] = zengyuanPo.getCity();// 缴费地区
						zengyuanArr[2] = zengyuanPo.getCustomerName();// 公司
						zengyuanArr[3] = zengyuanPo.getEmployeeName();// 员工姓名
						zengyuanArr[4] = zengyuanPo.getCertificateNumber();// 身份证号码
						zengyuanArr[5] = zengyuanPo.getSocialSecurityBase();// 保险缴费基数
						zengyuanArr[6] = zengyuanPo.getSocialSecurityBeginTime();// 社保开始缴费时间
						zengyuanArr[7] = zengyuanPo.getAccumulationFundCardinalNumber();// 公积金基数
						zengyuanArr[8] = zengyuanPo.getAccumulationFundBeginTime();// 公积金开始缴费时间
						zengyuanArr[9] = zengyuanPo.getMobilePhone();// 联系电话
						zengyuanArr[10] = zengyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "增加", 2, zengyuanArr);
						zengyuanMapper.updateStatus(zengyuanPo.getId());
					}
					for (int i2 = 0; i2 < jianyuanList.size(); i2++) {
						String[] jianyuanArr = new String[9];
						JianyuanPo jianyuanPo = jianyuanList.get(i2);
						jianyuanArr[0] = String.valueOf(jianyuanList.size() - i2);// 序号
						jianyuanArr[1] = jianyuanPo.getCity();// 城市
						jianyuanArr[2] = jianyuanPo.getClientName();// 公司
						jianyuanArr[3] = jianyuanPo.getEmployeeName();// 员工姓名
						jianyuanArr[4] = jianyuanPo.getCertificateNumber();// 身份证号码
						jianyuanArr[5] = jianyuanPo.getSocialSecurityStopDate();// 社保停缴时间
						jianyuanArr[6] = jianyuanPo.getAccumulationFundStopDate();// 公积金停缴时间
						jianyuanArr[7] = jianyuanPo.getDimissionReason();// 停缴原因
						jianyuanArr[8] = jianyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "减员", 2, jianyuanArr);
						jianyuanMapper.updateStatus(jianyuanPo.getJyId());
					}
					break;
				case "重庆聚焦":
					for (int i2 = 0; i2 < jianyuanList.size(); i2++) {
						String[] jianyuanArr = new String[16];
						JianyuanPo jianyuanPo = jianyuanList.get(i2);
						jianyuanArr[0] = String.valueOf(jianyuanList.size() - i2);// 序号
						jianyuanArr[1] = "";// 财务核算单位
						jianyuanArr[2] = jianyuanPo.getEmployeeName();// 员工姓名
						jianyuanArr[3] = "";// 险种
						jianyuanArr[4] = "";// 社保基数
						jianyuanArr[5] = jianyuanPo.getCertificateNumber();// 身份证号码
						jianyuanArr[6] = "";// 联系电话
						jianyuanArr[7] = "";// 民族
						jianyuanArr[8] = "";// 参加工作
						jianyuanArr[9] = "";// 户粮
						jianyuanArr[10] = "";// 增员
						jianyuanArr[11] = "";// 续保
						jianyuanArr[12] = "√";// 减员
						jianyuanArr[13] = "";// 公积金购买基数
						jianyuanArr[14] = "";// 购买公积金
						jianyuanArr[15] = jianyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "增员表", 4, jianyuanArr);
						jianyuanMapper.updateStatus(jianyuanPo.getJyId());
					}
					for (int i1 = 0; i1 < zengyuanList.size(); i1++) {
						String[] zengyuanArr = new String[16];
						ZengyuanPo zengyuanPo = zengyuanList.get(i1);
						zengyuanArr[0] = String.valueOf(zengyuanList.size() - i1);// 序号
						zengyuanArr[1] = zengyuanPo.getFinancialAccountingUnit();// 财务核算单位
						zengyuanArr[2] = zengyuanPo.getEmployeeName();// 员工姓名
						zengyuanArr[3] = zengyuanPo.getSocialSecurityType();// 险种
						zengyuanArr[4] = zengyuanPo.getSocialSecurityBase();// 社保基数
						zengyuanArr[5] = zengyuanPo.getCertificateNumber();// 身份证号码
						zengyuanArr[6] = zengyuanPo.getMobilePhone();// 联系电话
						zengyuanArr[7] = zengyuanPo.getNation();// 民族
						zengyuanArr[8] = "";// 参加工作
						zengyuanArr[9] = zengyuanPo.getHouseholdProperty();// 户口性质
						zengyuanArr[10] = "√";// 增加
						zengyuanArr[11] = "";// 续保
						zengyuanArr[12] = "";// 减少
						zengyuanArr[13] = zengyuanPo.getAccumulationFundCardinalNumber();// 公积金基数
						if ((!StringUtils.isEmpty(zengyuanArr[13]))
								&& (!StringUtils.isEmpty(zengyuanPo.getAccumulationFundRatio()))) {
							zengyuanArr[14] = "√";// 是否购买公积金
						} else {
							zengyuanArr[14] = "";// 是否购买公积金
						}
						zengyuanArr[15] = zengyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "增员表", 4, zengyuanArr);
						zengyuanMapper.updateStatus(zengyuanPo.getId());
					}
					break;
				case "江苏点米":
					for (int i1 = 0; i1 < zengyuanList.size(); i1++) {
						String[] zengyuanArr = new String[26];
						ZengyuanPo zengyuanPo = zengyuanList.get(i1);
						zengyuanArr[0] = String.valueOf(zengyuanList.size() - i1);// 序号
						zengyuanArr[1] = currentDate;// 派单时间
						zengyuanArr[2] = zengyuanPo.getFinancialAccountingUnit();// 财务核算单位
						zengyuanArr[3] = zengyuanPo.getCustomerName();// 客户名称
						zengyuanArr[4] = zengyuanPo.getEmployeeName();// 员工姓名
						zengyuanArr[5] = zengyuanPo.getCertificateNumber();// 身份证号码
						zengyuanArr[6] = zengyuanPo.getHouseholdCity();// 户籍地
						zengyuanArr[7] = zengyuanPo.getHouseholdProperty();// 户口性质
						zengyuanArr[8] = zengyuanPo.getMobilePhone();// 手机号码
						zengyuanArr[9] = zengyuanPo.getIsExternalCall();// 是否外呼
						zengyuanArr[10] = "";// 岗位
						zengyuanArr[11] = zengyuanPo.getCity();// 社保缴纳地
						zengyuanArr[12] = zengyuanPo.getSocialSecurityBeginTime();// 社保起缴月
						zengyuanArr[13] = zengyuanPo.getSocialSecurityBase();// 社保基数
						zengyuanArr[14] = zengyuanPo.getSocialSecurityCardNumber();// 社保卡号
						zengyuanArr[15] = zengyuanPo.getSocialSecurityCardNumber();// 医保卡号
						zengyuanArr[16] = zengyuanPo.getAccumulationFundBeginTime();// 公积金起缴月
						zengyuanArr[17] = zengyuanPo.getAccumulationFundCardinalNumber();// 公积金基数
						zengyuanArr[18] = zengyuanPo.getAccumulationFundRatio();// 公积金比例
						zengyuanArr[19] = zengyuanPo.getAccumulationFundNumber();// 公积金账号
						zengyuanArr[20] = "";// 是否首次参保
						zengyuanArr[21] = "";// 劳动合同开始时间
						zengyuanArr[23] = "";// 劳动合同结束时间
						zengyuanArr[23] = "";// 接单时间
						zengyuanArr[24] = "";// 办理进度
						zengyuanArr[25] = zengyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "增员", 3, zengyuanArr);
						zengyuanMapper.updateStatus(zengyuanPo.getId());
					}
					for (int i2 = 0; i2 < jianyuanList.size(); i2++) {
						String[] jianyuanArr = new String[14];
						JianyuanPo jianyuanPo = jianyuanList.get(i2);
						jianyuanArr[0] = String.valueOf(jianyuanList.size() - i2);// 序号
						jianyuanArr[1] = currentDate;// 派单时间
						jianyuanArr[2] = "";// 财务核算单位
						jianyuanArr[3] = jianyuanPo.getClientName();// 客户名称
						jianyuanArr[4] = jianyuanPo.getEmployeeName();// 员工姓名
						jianyuanArr[5] = jianyuanPo.getCertificateNumber();// 身份证号码
						jianyuanArr[6] = jianyuanPo.getCity();// 参保城市
						jianyuanArr[7] = jianyuanPo.getSocialSecurityStopDate();// 社保停缴日期
						jianyuanArr[8] = jianyuanPo.getAccumulationFundStopDate();// 公积金停保日期
						jianyuanArr[9] = jianyuanPo.getDimissionDate();// 离职时间
						jianyuanArr[10] = jianyuanPo.getDimissionReason();// 离职原因
						jianyuanArr[11] = "";// 接单时间
						jianyuanArr[12] = "";// 办理进展
						jianyuanArr[13] = jianyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "减员", 3, jianyuanArr);
						jianyuanMapper.updateStatus(jianyuanPo.getJyId());
					}
					break;
				case "济南众诚":
					for (int i2 = 0; i2 < jianyuanList.size(); i2++) {
						String[] jianyuanArr = new String[13];
						JianyuanPo jianyuanPo = jianyuanList.get(i2);
						jianyuanArr[0] = String.valueOf(jianyuanList.size() - i2);// 序号
						jianyuanArr[1] = jianyuanPo.getCity();// 城市
						jianyuanArr[2] = jianyuanPo.getEmployeeName();// 姓名
						jianyuanArr[3] = jianyuanPo.getCertificateNumber();// 身份证号码
						jianyuanArr[4] = "";// 户口相性质
						jianyuanArr[5] = "";// 社保基数
						jianyuanArr[6] = "";// 公积金基数
						jianyuanArr[7] = "";// 公积金比例
						jianyuanArr[8] = "减员";
						jianyuanArr[9] = jianyuanPo.getSocialSecurityStopDate();// 操作月份
						jianyuanArr[10] = "";// 联系方式
						jianyuanArr[11] = "";// 是够需要办理医保卡
						jianyuanArr[12] = jianyuanPo.getRemark();// 备注信息
						cer.insertRows(toFilePath, "Sheet1", 4, jianyuanArr);
						jianyuanMapper.updateStatus(jianyuanPo.getJyId());
					}
					for (int i1 = 0; i1 < zengyuanList.size(); i1++) {
						String[] zengyuanArr = new String[13];
						ZengyuanPo zengyuanPo = zengyuanList.get(i1);
						zengyuanArr[0] = String.valueOf(zengyuanList.size() - i1);// 序号
						zengyuanArr[1] = zengyuanPo.getCity();// 社保购买城市
						zengyuanArr[2] = zengyuanPo.getEmployeeName();// 员工姓名
						zengyuanArr[3] = zengyuanPo.getCertificateNumber();// 身份证号码
						zengyuanArr[4] = zengyuanPo.getHouseholdProperty();// 户口性质
						zengyuanArr[5] = zengyuanPo.getSocialSecurityBase();// 社保基数
						zengyuanArr[6] = zengyuanPo.getAccumulationFundCardinalNumber();// 公积金基数
						zengyuanArr[7] = zengyuanPo.getAccumulationFundRatio();// 公积金比例
						zengyuanArr[8] = "增员";// 增员
						zengyuanArr[9] = zengyuanPo.getSocialSecurityBeginTime();// 社保起缴年月
						zengyuanArr[10] = zengyuanPo.getMobilePhone();// 联系方式
						zengyuanArr[11] = "";// 是否办理社保卡
						zengyuanArr[12] = zengyuanPo.getRemark();// 备注信息
						cer.insertRows(toFilePath, "Sheet1", 4, zengyuanArr);
						zengyuanMapper.updateStatus(zengyuanPo.getId());
					}
					break;
				case "内蒙卓悦":
					for (int i2 = 0; i2 < jianyuanList.size(); i2++) {
						String[] jianyuanArr = new String[30];
						JianyuanPo jianyuanPo = jianyuanList.get(i2);
						jianyuanArr[0] = String.valueOf(jianyuanList.size() - i2);// 序号
						jianyuanArr[1] = jianyuanPo.getCity();// 参保城市
						jianyuanArr[2] = "";// 委托单位
						jianyuanArr[3] = jianyuanPo.getEmployeeName();// 姓名
						jianyuanArr[4] = "";// 性别
						jianyuanArr[5] = jianyuanPo.getCertificateNumber();// 身份证号码
						jianyuanArr[6] = "";// 民族
						jianyuanArr[7] = "";// 户籍性质
						jianyuanArr[8] = "";// 婚否
						jianyuanArr[9] = "";// 育否
						jianyuanArr[10] = "";// 联系电话
						jianyuanArr[11] = "";// 所属职位
						jianyuanArr[12] = "减";// 社保增减
						jianyuanArr[13] = "";// 在职单位名称
						jianyuanArr[14] = jianyuanPo.getSocialSecurityStopDate();// 社保变更月
						jianyuanArr[15] = "";// 社保补缴月份
						jianyuanArr[16] = "";// 养老基数
						jianyuanArr[17] = "";// 失业基数
						jianyuanArr[18] = "";// 医疗基数
						jianyuanArr[19] = "";// 生育基数
						jianyuanArr[20] = "";// 工伤基数
						jianyuanArr[21] = "";// 大病基数
						jianyuanArr[22] = "";// 大病个人
						jianyuanArr[23] = "减";// 公积金增减
						jianyuanArr[24] = jianyuanPo.getAccumulationFundStopDate();// 公积金变更月份
						jianyuanArr[25] = "";// 补缴月份
						jianyuanArr[26] = "";// 缴费基数
						jianyuanArr[27] = "";// 比例
						jianyuanArr[28] = "";// 金额
						jianyuanArr[29] = jianyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "样表", 3, jianyuanArr);
						jianyuanMapper.updateStatus(jianyuanPo.getJyId());
					}
					for (int i1 = 0; i1 < zengyuanList.size(); i1++) {
						String[] zengyuanArr = new String[30];
						ZengyuanPo zengyuanPo = zengyuanList.get(i1);
						zengyuanArr[0] = String.valueOf(zengyuanList.size() - i1);// 序号
						zengyuanArr[1] = zengyuanPo.getCity();// 社保缴费城市
						zengyuanArr[2] = "";// 委托单位名称
						zengyuanArr[3] = zengyuanPo.getEmployeeName();// 员工姓名
						zengyuanArr[4] = "";// 性别
						zengyuanArr[5] = zengyuanPo.getCertificateNumber();// 身份呢证号码
						zengyuanArr[6] = zengyuanPo.getNation();// 民族
						zengyuanArr[7] = zengyuanPo.getHouseholdProperty();// 户籍性质
						zengyuanArr[8] = "";// 婚否
						zengyuanArr[9] = "";// 育否
						zengyuanArr[10] = zengyuanPo.getMobilePhone();// 联系电话
						zengyuanArr[11] = "";// 所属职位
						zengyuanArr[12] = "增";// 社保增减
						zengyuanArr[13] = zengyuanPo.getCustomerName();// 在职单位名称
						zengyuanArr[14] = zengyuanPo.getSocialSecurityBeginTime();// 社保变更月
						zengyuanArr[15] = "";// 社保补缴月份
						zengyuanArr[16] = zengyuanPo.getSocialSecurityBase();// 养老基数
						zengyuanArr[17] = "";// 失业基数
						zengyuanArr[18] = "";// 医疗基数
						zengyuanArr[19] = "";// 生育基数
						zengyuanArr[20] = "";// 工伤基数
						zengyuanArr[21] = "";// 大病单位
						zengyuanArr[22] = "";// 大病个人
						zengyuanArr[23] = "增";// 公积金增减
						zengyuanArr[24] = zengyuanPo.getAccumulationFundBeginTime();// 公积金变更月份
						zengyuanArr[25] = "";// 补缴月份
						zengyuanArr[26] = zengyuanPo.getAccumulationFundCardinalNumber();// 缴费基数
						zengyuanArr[27] = zengyuanPo.getAccumulationFundRatio();// 比例
						zengyuanArr[28] = "";// 金额
						zengyuanArr[29] = zengyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "样表", 3, zengyuanArr);
						zengyuanMapper.updateStatus(zengyuanPo.getId());
					}
					break;
				case "北京点米":
					for (int i1 = 0; i1 < zengyuanList.size(); i1++) {
						String[] zengyuanArr = new String[27];
						ZengyuanPo zengyuanPo = zengyuanList.get(i1);
						zengyuanArr[0] = String.valueOf(zengyuanList.size() - i1);// 序号
						zengyuanArr[1] = zengyuanPo.getCustomerName();// 客户名称
						zengyuanArr[2] = zengyuanPo.getFinancialAccountingUnit();// 财务核算单位
						zengyuanArr[3] = zengyuanPo.getCity();// 缴纳地区
						zengyuanArr[4] = zengyuanPo.getEmployeeName();// 员工姓名
						zengyuanArr[5] = zengyuanPo.getCertificateNumber();// 身份证号码
						zengyuanArr[6] = "";// 是否新参保
						zengyuanArr[7] = zengyuanPo.getHouseholdProperty();// 户籍性质
						zengyuanArr[8] = zengyuanPo.getMobilePhone();// 手机号
						zengyuanArr[9] = zengyuanPo.getSocialSecurityBeginTime();// 社保起缴月
						zengyuanArr[10] = zengyuanPo.getSocialSecurityBase();// 社保基数
						zengyuanArr[11] = "";// 社保办理状态
						zengyuanArr[12] = "";// 操作日期
						zengyuanArr[13] = "";// 备注
						zengyuanArr[14] = zengyuanPo.getAccumulationFundBeginTime();// 公积金起缴月份
						zengyuanArr[15] = zengyuanPo.getAccumulationFundCardinalNumber();// 公积金基数
						zengyuanArr[16] = "";// 公积金办理状态
						zengyuanArr[17] = "";// 操作日期
						zengyuanArr[18] = "";// 备注
						zengyuanArr[19] = "";// 是够补缴
						zengyuanArr[20] = "";// 户口所在地地址
						zengyuanArr[21] = "";// 户口所在地邮编
						zengyuanArr[22] = "";// 银行卡号
						zengyuanArr[23] = "";// 开户行
						zengyuanArr[24] = user.getName();// 派单人
						zengyuanArr[25] = currentDate;// 派单日期
						zengyuanArr[26] = zengyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "增员表", 2, zengyuanArr);
						zengyuanMapper.updateStatus(zengyuanPo.getId());
					}
					for (int i2 = 0; i2 < jianyuanList.size(); i2++) {
						String[] jianyuanArr = new String[17];
						JianyuanPo jianyuanPo = jianyuanList.get(i2);
						jianyuanArr[0] = String.valueOf(jianyuanList.size() - i2);// 序号
						jianyuanArr[1] = jianyuanPo.getClientName();// 客户明湖曾
						jianyuanArr[2] = "";// 核算单位
						jianyuanArr[3] = jianyuanPo.getEmployeeName();// 员工姓名
						jianyuanArr[4] = jianyuanPo.getCertificateNumber();// 身份证号码
						jianyuanArr[5] = jianyuanPo.getSocialSecurityStopDate();// 社保停缴月
						jianyuanArr[6] = "";// 社保办理状态
						jianyuanArr[7] = "";// 操作日期
						jianyuanArr[8] = "";// 备注
						jianyuanArr[9] = jianyuanPo.getAccumulationFundStopDate();// 公积金停缴月
						jianyuanArr[10] = "";// 公积金个人编号
						jianyuanArr[11] = "";// 公积金办理状态
						jianyuanArr[12] = "";// 操作日期
						jianyuanArr[13] = "";// 备注
						jianyuanArr[14] = user.getName();// 派单人
						jianyuanArr[15] = currentDate;// 派单日期
						jianyuanArr[16] = jianyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "减员表", 2, jianyuanArr);
						jianyuanMapper.updateStatus(jianyuanPo.getJyId());
					}
					break;
				case "上海点米":
					for (int i1 = 0; i1 < zengyuanList.size(); i1++) {
						String[] zengyuanArr = new String[26];
						ZengyuanPo zengyuanPo = zengyuanList.get(i1);
						zengyuanArr[0] = String.valueOf(zengyuanList.size() - i1);// 序号
						zengyuanArr[1] = currentDate;// 派单时间
						zengyuanArr[2] = zengyuanPo.getFinancialAccountingUnit();// 财务核算单位
						zengyuanArr[3] = zengyuanPo.getCustomerName();// 客户名称
						zengyuanArr[4] = zengyuanPo.getEmployeeName();// 员工姓名
						zengyuanArr[5] = zengyuanPo.getCertificateNumber();// 身份证号码
						zengyuanArr[6] = zengyuanPo.getHouseholdCity();// 户籍地
						zengyuanArr[7] = zengyuanPo.getHouseholdProperty();// 户口性质
						zengyuanArr[8] = zengyuanPo.getMobilePhone();// 联系电话
						zengyuanArr[9] = zengyuanPo.getIsExternalCall();// 手机号
						zengyuanArr[10] = "";// 岗位
						zengyuanArr[11] = zengyuanPo.getCity();// 社保缴纳城市
						zengyuanArr[12] = zengyuanPo.getSocialSecurityBeginTime();// 社保起缴月
						zengyuanArr[13] = zengyuanPo.getSocialSecurityBase();// 社保基数
						zengyuanArr[14] = zengyuanPo.getSocialSecurityCardNumber();// 社保号
						zengyuanArr[15] = zengyuanPo.getSocialSecurityCardNumber();// 医保号
						zengyuanArr[16] = zengyuanPo.getAccumulationFundBeginTime();// 公积金起缴月份
						zengyuanArr[17] = zengyuanPo.getAccumulationFundCardinalNumber();// 公积金基数
						zengyuanArr[18] = zengyuanPo.getAccumulationFundRatio();// 公积金比例
						zengyuanArr[19] = zengyuanPo.getAccumulationFundNumber();// 公积金账号
						zengyuanArr[20] = "";// 是否首次参保
						zengyuanArr[21] = "";// 劳动合同开始时间
						zengyuanArr[22] = "";// 劳动合同结束时间
						zengyuanArr[23] = "";// 接单时间
						zengyuanArr[24] = "";// 办理进度
						zengyuanArr[25] = zengyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "增员", 3, zengyuanArr);
						zengyuanMapper.updateStatus(zengyuanPo.getId());
					}
					for (int i2 = 0; i2 < jianyuanList.size(); i2++) {
						String[] jianyuanArr = new String[14];
						JianyuanPo jianyuanPo = jianyuanList.get(i2);
						jianyuanArr[0] = String.valueOf(jianyuanList.size() - i2);// 序号
						jianyuanArr[1] = currentDate;// 派单时间
						jianyuanArr[2] = "";// 财务核算单位
						jianyuanArr[3] = jianyuanPo.getClientName();// 客户名称
						jianyuanArr[4] = jianyuanPo.getEmployeeName();// 员工姓名
						jianyuanArr[5] = jianyuanPo.getCertificateNumber();// 身份证号码
						jianyuanArr[6] = jianyuanPo.getCity();// 参保城市
						jianyuanArr[7] = jianyuanPo.getSocialSecurityStopDate();// 社保停缴时间
						jianyuanArr[8] = jianyuanPo.getAccumulationFundStopDate();// 公积金停缴时间
						jianyuanArr[9] = jianyuanPo.getDimissionDate();// 离职时间
						jianyuanArr[10] = jianyuanPo.getDimissionReason();// 停保原因
						jianyuanArr[11] = "";// 接单时间
						jianyuanArr[12] = "";// 办理进度
						jianyuanArr[13] = jianyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "减员", 3, jianyuanArr);
						jianyuanMapper.updateStatus(jianyuanPo.getJyId());
					}
					break;
				case "上海栗道":
					for (int i1 = 0; i1 < zengyuanList.size(); i1++) {
						String[] zengyuanArr = new String[26];
						ZengyuanPo zengyuanPo = zengyuanList.get(i1);
						zengyuanArr[0] = String.valueOf(zengyuanList.size() - i1);// 序号
						zengyuanArr[1] = currentDate;// 派单时间
						zengyuanArr[2] = zengyuanPo.getFinancialAccountingUnit();// 财务核算单位
						zengyuanArr[3] = zengyuanPo.getCustomerName();// 客户名称
						zengyuanArr[4] = zengyuanPo.getEmployeeName();// 姓名
						zengyuanArr[5] = zengyuanPo.getCertificateNumber();// 身份证号码
						zengyuanArr[6] = zengyuanPo.getHouseholdCity();// 户籍地
						zengyuanArr[7] = zengyuanPo.getHouseholdProperty();// 户口性质
						zengyuanArr[8] = zengyuanPo.getMobilePhone();// 手机号
						zengyuanArr[9] = zengyuanPo.getIsExternalCall();// 是否外呼
						zengyuanArr[10] = "";// 岗位
						zengyuanArr[11] = zengyuanPo.getCity();// 社保缴纳城市
						zengyuanArr[12] = zengyuanPo.getSocialSecurityBeginTime();// 社保起缴月份
						zengyuanArr[13] = zengyuanPo.getSocialSecurityBase();// 社保基数
						zengyuanArr[14] = zengyuanPo.getSocialSecurityCardNumber();// 社保号
						zengyuanArr[15] = zengyuanPo.getSocialSecurityCardNumber();// 医保号
						zengyuanArr[16] = zengyuanPo.getAccumulationFundBeginTime();// 公积金起缴月份
						zengyuanArr[17] = zengyuanPo.getAccumulationFundCardinalNumber();// 公积金基数
						zengyuanArr[18] = zengyuanPo.getAccumulationFundRatio();// 公积金比例
						zengyuanArr[19] = zengyuanPo.getAccumulationFundNumber();// 公积金账号
						zengyuanArr[20] = "";// 是否首次参保
						zengyuanArr[21] = "";// 劳动合同开始时间
						zengyuanArr[22] = "";// 劳动合同结束时间
						zengyuanArr[23] = "";// 接单时间
						zengyuanArr[24] = "";// 办理进度
						zengyuanArr[25] = zengyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "增员", 3, zengyuanArr);
						zengyuanMapper.updateStatus(zengyuanPo.getId());
					}
					for (int i2 = 0; i2 < jianyuanList.size(); i2++) {
						String[] jianyuanArr = new String[14];
						JianyuanPo jianyuanPo = jianyuanList.get(i2);
						jianyuanArr[0] = String.valueOf(jianyuanList.size() - i2);// 序号
						jianyuanArr[1] = currentDate;// 派单时间
						jianyuanArr[2] = "";// 财务核算单位
						jianyuanArr[3] = jianyuanPo.getClientName();// 客户名称
						jianyuanArr[4] = jianyuanPo.getEmployeeName();// 员工姓名
						jianyuanArr[5] = jianyuanPo.getCertificateNumber();// 身份证号码
						jianyuanArr[6] = jianyuanPo.getCity();// 参保城市
						jianyuanArr[7] = jianyuanPo.getSocialSecurityStopDate();// 社保停缴日期
						jianyuanArr[8] = jianyuanPo.getAccumulationFundStopDate();// 公积金停缴日期
						jianyuanArr[9] = jianyuanPo.getDimissionDate();// 离职日期
						jianyuanArr[10] = jianyuanPo.getDimissionReason();// 停保原因
						jianyuanArr[11] = "";// 接单时间
						jianyuanArr[12] = "";// 办理进度
						jianyuanArr[13] = jianyuanPo.getRemark();// 备注
						cer.insertRows(toFilePath, "减员", 3, jianyuanArr);
						jianyuanMapper.updateStatus(jianyuanPo.getJyId());
					}
					break;
				}
				map.put("isSendEmail", true);
				map.put("filePath", toFilePath);
			} catch (Exception e) {
				map.put("isSendEmail", false);
				map.put("result", "系统错误");
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 获取邮件模板
	 * 
	 * @param supplierId
	 * @return
	 */
	public SupplierTemplate template(int supplierId) {
		List<SupplierTemplate> list = supplierTemplateMapper.selectBySupplierId(supplierId);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	// 删除增员信息
	public int deleteById(int zyId) {
		return zengyuanMapper.deleteById(zyId);
	}

	/**
	 * 查询所有的增员信息
	 */
	public List<Zengyuan> selectZengyuanAll() {
		List<Zengyuan> ll = zengyuanMapper.selectZengyuanAll();
		if (ll == null) {
			return new ArrayList<>();
		}
		return ll;
	}

	/**
	 * 修改增员状态
	 */
	public void updateAddStatus(String certificateNumber, String city, String month) {
		zengyuanMapper.updateAddStatus(certificateNumber, city, month);
	}

	// 去编辑
	public Zengyuan getByZyId(int zyId) {
		return zengyuanMapper.selectByPrimaryKey(zyId);
	}

	// 更新
	public int update(String zengyuanStr) {
		zengyuanStr = zengyuanStr.substring(zengyuanStr.indexOf("{"), zengyuanStr.lastIndexOf("}") + 1);
		JSONObject jsonObject = JSONObject.parseObject(zengyuanStr);
		Zengyuan zengyuan = new Zengyuan(jsonObject.getInteger("zyId"), jsonObject.getInteger("userId"),
				jsonObject.getInteger("customerId"), jsonObject.getInteger("supplierId"),
				jsonObject.getInteger("fuwufanganId"), jsonObject.getString("financialAccountingUnit"),
				jsonObject.getString("customerName"), jsonObject.getString("employeeName"),
				jsonObject.getString("certificateType"), jsonObject.getString("certificateNumber").replace("x", "X"),
				jsonObject.getString("nation"), jsonObject.getString("mobilePhone"),
				jsonObject.getString("isExternalCall"), jsonObject.getString("householdCity"),
				jsonObject.getString("householdProperty"), jsonObject.getString("city"),
				jsonObject.getString("socialSecurityCardNumber"), jsonObject.getString("socialSecurityBase"),
				jsonObject.getString("socialSecurityType"), jsonObject.getString("supplier"),
				jsonObject.getString("socialSecurityBeginTime"), jsonObject.getString("accumulationFundNumber"),
				jsonObject.getString("accumulationFundCardinalNumber"), jsonObject.getString("accumulationFundRatio"),
				jsonObject.getString("accumulationFundBeginTime"), jsonObject.getDate("importDate"),
				jsonObject.getString("remark"), jsonObject.getString("reportingPeriod"), jsonObject.getByte("status"));
		return zengyuanMapper.updateByPrimaryKey(zengyuan);
	}

	@Override
	public String selectAddEmployeeName(String city, Integer supplierId, String month, String certificateNumber) {
		return zengyuanMapper.selectAddEmployeeName(city, supplierId, month, certificateNumber);
	}

	// 根据员工姓名修改状态
	public void updateAddSocialStatus(String certificateNumber, String month, String city) {
		zengyuanMapper.updateAddSocialStatus(certificateNumber, month, city);
	}

	public int[] amount(String yearMonth, int supplierId) {
		int[] amount = new int[2];
		amount[0] = zengyuanMapper.amount(yearMonth, supplierId, 1);// 待发送
		amount[1] = zengyuanMapper.amount(yearMonth, supplierId, 2);// 已发送
		return amount;
	}

	/**
	 * 修改员工供应商信息
	 * 
	 * @param fw_id
	 * @param employeeInfoStr
	 */
	@Transactional
	public void updateSupplier(int fwId, String employeeInfo) {
		JSONArray jsonArray = JSONArray.parseArray(employeeInfo);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject object = jsonArray.getJSONObject(i);
			String city = object.getString("city");
			String certificateNumber = object.getString("certificateNumber");
			String reportingPeriod = object.getString("reportingPeriod");
			int supplierId = object.getInteger("supplierId");
			updateOrCreateYuqiZhangdan(fwId, city, reportingPeriod, certificateNumber, supplierId);
		}
	}

	// 修改账单
	public void updateOrCreateYuqiZhangdan(int fwId, String city, String reportingPeriod, String certificateNumber,
			int supplierId) {
		Zaice zaice = zaiceService.selectByEmployeeMsg(city, reportingPeriod, certificateNumber);
		if (zaice != null) {
			String accumulationFundRatio = zaice.getAccumulationFundRatio();// 5%+5%
			double accmulationFundCompanyPay = 0;
			double accmulationFundPersonPay = 0;
			if (!StringUtils.isEmpty(accumulationFundRatio)) {
				float[] ratioArr = ParseUtil.parseGongjijinStrRatioToFloat(accumulationFundRatio);
				double accumulationFundCardinalNumber = Double
						.parseDouble(zaice.getAccumulationFundCardinalNumber().trim());
				accmulationFundCompanyPay = accumulationFundCardinalNumber * ratioArr[0];
				accmulationFundPersonPay = accumulationFundCardinalNumber * ratioArr[1];
			}
			Fuwufangan fuwufangan = fuwufanganMapper.selectByPrimaryKey(fwId);
			if (null != fuwufangan) {
				// 匹配到服务方案修改增员表中用户服务方案信息
				updateFuwufanganMsg(fuwufangan.getSupplierId(), fuwufangan.getFwId(), reportingPeriod, city,
						certificateNumber);
				int zaiceId = zaice.getZcId();
				double yanglaoGongsi = fuwufangan.getPensionCompanyPay();
				double yanglaoGeren = fuwufangan.getPensionPersonPay();
				double shiyeGongsi = fuwufangan.getUnemploymentCompanyPay();
				double shiyeGeren = fuwufangan.getUnemploymentPersonPay();
				double gongshangGongsi = fuwufangan.getInjuryCompanyPay();
				double shengyuGongsi = fuwufangan.getProcreateCompanyPay();
				double jibenYiliaoGongsi = fuwufangan.getMedicalCompanyPay();
				double jibenYiliaoGeren = fuwufangan.getMedicalPersonPay();
				double dabingYiliaoGongsi = fuwufangan.getSeriousIllnessCompanyPay();
				double dabingYiliaoGeren = fuwufangan.getSeriousIllnessPersonPay();
				double gongjijinGongsi = accmulationFundCompanyPay;
				double gongjijinGeren = accmulationFundPersonPay;
				double canbaojinGongsi = fuwufangan.getDisabilityGuaranteeFund();
				double qitaGongsi = fuwufangan.getOtherCompanyPay();
				double qitaGeren = fuwufangan.getOtherPersonPay();
				double fuwufei = fuwufangan.getServiceCharge();
				if (supplierId == 1) {
					// 新增员工服务方案信息
					employeeFuwufanganMapper
							.insert(new EmployeeFuwufangan(zaiceId, yanglaoGongsi, yanglaoGeren, jibenYiliaoGongsi,
									jibenYiliaoGeren, shiyeGongsi, shiyeGeren, dabingYiliaoGongsi, dabingYiliaoGeren,
									shengyuGongsi, gongjijinGongsi, fuwufei, canbaojinGongsi, qitaGongsi, qitaGeren));
					// 新增员工账单
					zhangdanMingxiMapper.insert(new ZhangdanMingxi(zaiceId, yanglaoGongsi, yanglaoGeren,
							jibenYiliaoGongsi, jibenYiliaoGeren, dabingYiliaoGongsi, dabingYiliaoGeren, shiyeGongsi,
							shiyeGeren, gongshangGongsi, shengyuGongsi, gongjijinGongsi, gongjijinGeren, qitaGongsi,
							qitaGeren, canbaojinGongsi, fuwufei, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
							0.0, 0.0, 0.0, 0.0, 0.0));
				} else {
					// 修改员工服务方案信息
					employeeFuwufanganMapper.updateByEmployeeInfo(
							new EmployeeFuwufangan(zaiceId, yanglaoGongsi, yanglaoGeren, jibenYiliaoGongsi,
									jibenYiliaoGeren, shiyeGongsi, shiyeGeren, dabingYiliaoGongsi, dabingYiliaoGeren,
									shengyuGongsi, gongjijinGongsi, fuwufei, canbaojinGongsi, qitaGongsi, qitaGeren));
					// 修改员工账单
					zhangdanMingxiMapper.updateByEmployeeInfo(new ZhangdanMingxi(zaiceId, yanglaoGongsi, yanglaoGeren,
							jibenYiliaoGongsi, jibenYiliaoGeren, dabingYiliaoGongsi, dabingYiliaoGeren, shiyeGongsi,
							shiyeGeren, gongshangGongsi, shengyuGongsi, gongjijinGongsi, gongjijinGeren, qitaGongsi,
							qitaGeren, canbaojinGongsi, fuwufei));
				}
			}
		}
	}

	// 按供应商导出增减员
	@SuppressWarnings("resource")
	public String exportZengjianyuan(String yearMonth, int supplierId) {
		List<ZengyuanPo> zengyuanList = zengyuanMapper.selectBySupplierId(yearMonth, supplierId);
		List<JianyuanPo> jianyuanList = jianyuanMapper.selectBySupplierId(yearMonth, supplierId);
		// 创建excel工作簿
		SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(), 100);
		// 创建第一个sheet（页），命名
		Sheet zengyuanSheet = wb.createSheet("增员");
		Row zyTitleRow = zengyuanSheet.createRow((short) 0);
		String[] titleArr = new String[] { "财务核算单位", "客户名称", "员工姓名", "证件类型", "证件号码", "民族", "手机", "是否外呼", "户籍城市", "户口性质",
				"参保城市", "社保/医保号", "社保基数", "参保类型", "供应商", "社保起缴年月", "公积金账号", "公积金基数", "公积金比例", "公积金起缴年月", "备注" };
		for (int zyTitleIndex = 0; zyTitleIndex < titleArr.length; zyTitleIndex++) {
			zyTitleRow.createCell(zyTitleIndex).setCellValue(titleArr[zyTitleIndex]);
		}
		for (int i1 = 0; i1 < zengyuanList.size(); i1++) {
			ZengyuanPo zengyuanPo = zengyuanList.get(i1);
			Row row = zengyuanSheet.createRow((short) i1 + 1);
			row.createCell(0).setCellValue(zengyuanPo.getFinancialAccountingUnit());
			row.createCell(1).setCellValue(zengyuanPo.getCustomerName());
			row.createCell(2).setCellValue(zengyuanPo.getEmployeeName());
			row.createCell(3).setCellValue(zengyuanPo.getCertificateType());
			row.createCell(4).setCellValue(zengyuanPo.getCertificateNumber());
			row.createCell(5).setCellValue(zengyuanPo.getNation());
			row.createCell(6).setCellValue(zengyuanPo.getMobilePhone());
			row.createCell(7).setCellValue(zengyuanPo.getIsExternalCall());
			row.createCell(8).setCellValue(zengyuanPo.getHouseholdCity());
			row.createCell(9).setCellValue(zengyuanPo.getHouseholdProperty());
			row.createCell(10).setCellValue(zengyuanPo.getCity());
			row.createCell(11).setCellValue(zengyuanPo.getSocialSecurityCardNumber());
			row.createCell(12).setCellValue(zengyuanPo.getSocialSecurityBase());
			row.createCell(13).setCellValue(zengyuanPo.getSocialSecurityType());
			row.createCell(14).setCellValue(zengyuanPo.getSupplier());
			row.createCell(15).setCellValue(zengyuanPo.getSocialSecurityBeginTime());
			row.createCell(16).setCellValue(zengyuanPo.getAccumulationFundNumber());
			row.createCell(17).setCellValue(zengyuanPo.getAccumulationFundCardinalNumber());
			row.createCell(18).setCellValue(zengyuanPo.getAccumulationFundRatio());
			row.createCell(19).setCellValue(zengyuanPo.getAccumulationFundBeginTime());
			row.createCell(20).setCellValue(zengyuanPo.getRemark());
		}
		// 创建第二个sheet（页），命名
		Sheet jianyuanSheet = wb.createSheet("减员");
		Row jyTitleRow = jianyuanSheet.createRow((short) 0);
		String[] jyTitleArr = new String[] { "客户名称", "员工姓名", "证件类型", "证件号码", "参保城市", "离职原因", "离职日期", "社保停缴年月",
				"公积金停缴年月", "供应商", "备注" };
		for (int jyTitleIndex = 0; jyTitleIndex < jyTitleArr.length; jyTitleIndex++) {
			jyTitleRow.createCell(jyTitleIndex).setCellValue(jyTitleArr[jyTitleIndex]);
		}
		for (int i2 = 0; i2 < jianyuanList.size(); i2++) {
			JianyuanPo jianyuanPo = jianyuanList.get(i2);
			// 创建一行，在页sheet上
			Row row = jianyuanSheet.createRow((short) i2 + 1);
			row.createCell(0).setCellValue(jianyuanPo.getClientName());
			row.createCell(1).setCellValue(jianyuanPo.getEmployeeName());
			row.createCell(3).setCellValue(jianyuanPo.getCertificateNumber());
			row.createCell(4).setCellValue(jianyuanPo.getCity());
			row.createCell(5).setCellValue(jianyuanPo.getDimissionReason());
			row.createCell(6).setCellValue(jianyuanPo.getDimissionDate());
			row.createCell(7).setCellValue(jianyuanPo.getSocialSecurityStopDate());
			row.createCell(8).setCellValue(jianyuanPo.getAccumulationFundStopDate());
			row.createCell(9).setCellValue(jianyuanPo.getSupplier());
			row.createCell(10).setCellValue(jianyuanPo.getRemark());
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
	 * @param type
	 * @param roleType
	 * @return 用户所有供应商未发邮件总数
	 */
	public int[] emailAmount(String yearMonth, int userId, int roleType) {
		int[] emailAmount = new int[2];
		emailAmount[0] = zengyuanMapper.emailAmount(yearMonth, 1, userId, roleType);// 未发邮件数量
		emailAmount[1] = zengyuanMapper.emailAmount(yearMonth, 2, userId, roleType);// 已发邮件数量
		return emailAmount;
	}

	/**
	 * @param yearMonth
	 * @param userId
	 * @return 用户所有增员数量
	 */
	public int[] zengjianYuanAmount(String yearMonth, int userId, int roleType) {
		int zengyuanAmount = zengyuanMapper.zengyuanAmount(yearMonth, userId, roleType);
		int jianyuanAmount = jianyuanMapper.jianyuanAmount(yearMonth, userId, roleType);
		return new int[] { zengyuanAmount, jianyuanAmount };
	}

	/**
	 * @param yearMonth
	 * @return 为匹配供应商的增员数量
	 */
	public int notMatchSupplierAmount(String yearMonth) {
		return zengyuanMapper.notMatchSupplierAmount(yearMonth);
	}

	/**
	 * @param yearMonth
	 * @param userId
	 * @param roleType
	 * @return 用户增员信息中所有城市信息
	 */
	public List<Map<String, String>> allCity(String yearMonth, int userId, int roleType) {
		return zengyuanMapper.allCity(yearMonth, userId, roleType);
	}

	/**
	 * @param yearMonth
	 * @param city
	 * @param userId
	 * @param roleType
	 * @return 获取城市所有增员信息
	 */
	public List<ZengyuanPo> selectByCity(String yearMonth, String city, int userId, int roleType) {
		return zengyuanMapper.selectByCity(yearMonth, city, userId, roleType);
	}

	/**
	 * @param yearMonth
	 * @param userId
	 * @param roleType
	 * @return 用户供应商下所有部门信息
	 */
	public List<Map<String, String>> allDept(String yearMonth, int userId, int roleType) {
		return zengyuanMapper.allDept(yearMonth, userId, roleType);
	}

	public List<ZengyuanPo> selectByDept(String yearMonth, int userId, int roleType, String deptName) {
		return zengyuanMapper.selectByDept(yearMonth, userId, roleType, deptName);
	}

	public ResultJson isSendEmailZengyuan(String yearMonth, Integer isSendEmail, User user, Integer currPage,
			Integer pageSize) {
		if (StringUtils.isEmpty(yearMonth) || null == isSendEmail || null == currPage || null == pageSize)
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		PageHelper.startPage(currPage, pageSize);
		List<ZengyuanPo> list = zengyuanMapper.isSendEmailZengyuan(yearMonth, isSendEmail, user.getUId(),
				user.getRoleType());
		PageInfo<ZengyuanPo> pageInfo = new PageInfo<>(list);
		return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
	}

	/**
	 * @param yearMonth
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	public ResultJson noSupplierZengyuan(String yearMonth, Integer currPage, Integer pageSize) {
		if (StringUtils.isEmpty(yearMonth) || null == currPage || null == pageSize)
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		PageHelper.startPage(currPage, pageSize);
		List<ZengyuanPo> list = zengyuanMapper.noSupplierZengyuan(yearMonth);
		PageInfo<ZengyuanPo> pageInfo = new PageInfo<>(list);
		return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
	}

	/**
	 * @param yearMonth
	 * @param city
	 * @param reportingPeriod
	 * @return
	 */
	public int zengyuanFaild(String yearMonth, String city, String certificateNumber) {
		return zengyuanMapper.zengyuanFaild(yearMonth, city, certificateNumber);
	}

	/**
	 * @param yearMonth
	 * @param supplierId
	 * @return
	 */
	public List<String> getAllCertificateNumber(String yearMonth, Integer supplierId) {
		return zengyuanMapper.getAllCertificateNumber(yearMonth, supplierId);
	}

	@Transactional
	public ResultJson export(String yearMonth, Integer city,Integer type) throws IOException {
		if (StringUtils.isEmpty(yearMonth) || null == city || null == type)
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		// 广州
		if (city == 1) {
			List<Zengyuan> zengyuanList = zengyuanMapper.getAllByCity(yearMonth, GUANGZHOU,type);
			List<Jianyuan> jianyuanList = jianyuanMapper.getAllByCity(yearMonth, GUANGZHOU,type);
			if(zengyuanList.size() == 0 && jianyuanList.size() == 0){
				return ResultUtil.success(RestEnum.SUCCESS,"没有增减员数据可以导出");
			}
			String filePath = zengjianyuanTemplateGz(zengyuanList, jianyuanList);
			DownloadFile.downloadFile(filePath, "广州增减员" + yearMonth + ".xlsx");
			DeleteFile.DeleteFolder(filePath);
			// 深圳
		} else if (city == 2) {
			List<Zengyuan> zengyuanList = zengyuanMapper.getAllByCity(yearMonth, SHENZHEN,type);
			List<Jianyuan> jianyuanList = jianyuanMapper.getAllByCity(yearMonth, SHENZHEN,type);
			if(zengyuanList.size() == 0 && jianyuanList.size() == 0){
				return ResultUtil.success(RestEnum.SUCCESS,"没有增减员数据可以导出");
			}
			String jianyuanFilePath = zengjianyuanTemplateSz(zengyuanList,jianyuanList);
			DownloadFile.downloadFile(jianyuanFilePath, "深圳增减员" + yearMonth + ".xlsx");
			DeleteFile.DeleteFolder(jianyuanFilePath);
			// 珠海
		} else if (city == 3) {
			List<Zengyuan> zengyuanList = zengyuanMapper.getAllByCity(yearMonth, ZHUHAI,type);
			List<Jianyuan> jianyuanList = jianyuanMapper.getAllByCity(yearMonth, ZHUHAI,type);
			if(zengyuanList.size() == 0 && jianyuanList.size() == 0){
				return ResultUtil.success(RestEnum.SUCCESS,"没有增减员数据可以导出");
			}
			String filePath = zengjianyuanTemplateZh(zengyuanList, jianyuanList);
			DownloadFile.downloadFile(filePath, "珠海增减员" + yearMonth + ".xlsx");
			DeleteFile.DeleteFolder(filePath);
			// 东莞
		} else if (city == 4) {
			List<Zengyuan> zengyuanList = zengyuanMapper.getAllByCity(yearMonth, DONGGUAN,type);
			List<Jianyuan> jianyuanList = jianyuanMapper.getAllByCity(yearMonth, DONGGUAN,type);
			if(zengyuanList.size() == 0 && jianyuanList.size() == 0){
				return ResultUtil.success(RestEnum.SUCCESS,"没有增减员数据可以导出");
			}
			String filePath = zengjianyuanTemplateDg(zengyuanList, jianyuanList);
			DownloadFile.downloadFile(filePath, "东莞增减员" + yearMonth + ".xlsx");
			DeleteFile.DeleteFolder(filePath);
		}
		return ResultUtil.success(RestEnum.SUCCESS);
	}

	/**
	 * @param list
	 * @return 广州增减员模板
	 */
	public String zengjianyuanTemplateGz(List<Zengyuan> zyList, List<Jianyuan> jyList) {
		String fromFilePath = realPath() + "template" + File.separator + "zengjianyuan" + File.separator + "广州.xlsx";
		String toFilePath = realPath() + "download" + File.separator + "excel" + File.separator
				+ UUID.randomUUID().toString() + ".xlsx";
		CopyFile.copyFile(fromFilePath, toFilePath);
		CreateExcelRow cer = new CreateExcelRow();
		for (int i = 0; i < jyList.size(); i++) {
			Jianyuan jianyuan = jyList.get(i);
			String[] strArr = new String[] { "12|减员", demilissionTypeGz(jianyuan.getDimissionReason()), "",
					idCardType(jianyuan.getCertificateType()), jianyuan.getCertificateNumber(),
					jianyuan.getEmployeeName(), sexGz(jianyuan.getCertificateNumber()), "156|中国", "",
					GetBirthDayFromIDCard.getBirthday(jianyuan.getCertificateNumber()), "", "", "", "", "", "", "0|不参保",
					"0|不参保", "0|不参保", "0|不参保", "0|不参保", "0|不参保" };
			cer.insertRows(toFilePath, "Sheet1", 5, strArr);
			jianyuanMapper.updateExportStatus(jianyuan.getJyId());
		}
		for (int j = 0; j < zyList.size(); j++) {
			Zengyuan zengyuan = zyList.get(j);
			String[] strArr = new String[] { "11|增员", "11|新参保", zengyuan.getSocialSecurityCardNumber(),
					idCardType(zengyuan.getCertificateType()), zengyuan.getCertificateNumber(),
					zengyuan.getEmployeeName(), sexGz(zengyuan.getCertificateNumber()), "156|中国",
					zengyuan.getSocialSecurityBase(),
					GetBirthDayFromIDCard.getBirthday(zengyuan.getCertificateNumber()), "06|工人", "0|在职", "40|合同",
					householdPropertyGz(zengyuan.getHouseholdProperty()), zengyuan.getSocialSecurityBeginTime(),
					zengyuan.getMobilePhone(), "1|参保", "1|参保", "1|参保", "1|参保", "1|参保", "1|参保" };
			cer.insertRows(toFilePath, "Sheet1", 5, strArr);
			zengyuanMapper.updateExportStatus(zengyuan.getZyId());
		}
		return toFilePath;
	}

	/**
	 * @param list
	 * @return 深圳增员模板
	 */
	public String zengjianyuanTemplateSz(List<Zengyuan> zengyuanList,List<Jianyuan> jianyuanList) {
		SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(), 100);
		Sheet zySheet = wb.createSheet("新增上传人员模板");
		String[] zyTitle = new String[] { "身份证号码", "姓名", "配偶姓名（非必填项）", "户籍", "职工性质", "民族", "缴费工资", "利手", "参保人手机号",
				"工作部门（非必填项）", "个人银行名称（非必填项）", "个人银行账号（非必填项）", "养老保险", "医疗保险", "工伤保险", "失业保险" };
		Row zyTitleRow = zySheet.createRow((short) 0);
		// 创建样式
		CellStyle style = wb.createCellStyle();
		// 设置背景色
		style.setFillForegroundColor(IndexedColors.TAN.getIndex());
		// 设置背景图案
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for (int i = 0; i < zyTitle.length; i++) {
			Cell cell = zyTitleRow.createCell(i);
			cell.setCellValue(zyTitle[i]);
			cell.setCellStyle(style);
		}
		// 设置字体大小
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		style.setFont(font);
		for (int j = 0; j < zengyuanList.size(); j++) {
			Zengyuan zengyuan = zengyuanList.get(j);
			Row zyRow = zySheet.createRow((short) j + 1);
			zyRow.createCell(0).setCellValue(zengyuan.getCertificateNumber());
			zyRow.createCell(1).setCellValue(zengyuan.getEmployeeName());
			zyRow.createCell(2).setCellValue("");// 配偶姓名
			zyRow.createCell(3).setCellValue(4);
			zyRow.createCell(4).setCellValue(3);
			zyRow.createCell(5).setCellValue(1);// 民族
			zyRow.createCell(6).setCellValue(zengyuan.getSocialSecurityBase());
			zyRow.createCell(7).setCellValue(1);
			zyRow.createCell(8).setCellValue(zengyuan.getMobilePhone());
			zyRow.createCell(9).setCellValue("");
			zyRow.createCell(10).setCellValue("");
			zyRow.createCell(11).setCellValue("");
			zyRow.createCell(12).setCellValue(1);
			zyRow.createCell(13).setCellValue(4);
			zyRow.createCell(14).setCellValue(1);
			zyRow.createCell(15).setCellValue(1);
			zengyuanMapper.updateExportStatus(zengyuan.getZyId());
		}
		Sheet jySheet = wb.createSheet("批量停缴上传人员模板");
		String[] jyTitle = new String[] { "电脑号", "姓名", "身份证号", "停交原因" };
		Row jyTitleRow = jySheet.createRow((short) 0);
		for (int i2 = 0; i2 < jyTitle.length; i2++) {
			Cell cell = jyTitleRow.createCell(i2);
			cell.setCellValue(jyTitle[i2]);
			cell.setCellStyle(style);
		}
		for (int j2 = 0; j2 < jianyuanList.size(); j2++) {
			Jianyuan jianyuan = jianyuanList.get(j2);
			Row jyRow = jySheet.createRow((short) j2 + 1);
			jyRow.createCell(0).setCellValue("");
			jyRow.createCell(1).setCellValue(jianyuan.getEmployeeName());
			jyRow.createCell(2).setCellValue(jianyuan.getCertificateNumber());
			jyRow.createCell(3).setCellValue(jianyuan.getDimissionReason());
			jianyuanMapper.updateExportStatus(jianyuan.getJyId());
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
	 * @return
	 */
	public String zengjianyuanTemplateZh(List<Zengyuan> zengyuanList, List<Jianyuan> jianyuanList) {
		String fromFilePath = realPath() + "template" + File.separator + "zengjianyuan" + File.separator + "珠海.xlsx";
		String toFilePath = realPath() + "download" + File.separator + "excel" + File.separator
				+ UUID.randomUUID().toString() + ".xlsx";
		CopyFile.copyFile(fromFilePath, toFilePath);
		CreateExcelRow cer = new CreateExcelRow();
		for (int i = 0; i < jianyuanList.size(); i++) {
			Jianyuan jianyuan = jianyuanList.get(i);
			String[] strArr = new String[] { "12|减员", "22|中断缴费", "", idCardType(jianyuan.getCertificateNumber()),
					jianyuan.getCertificateNumber(), jianyuan.getEmployeeName(), sexGz(jianyuan.getCertificateNumber()), "156|中国", "",
					GetBirthDayFromIDCard.getBirthday(jianyuan.getCertificateNumber()), "", "", "", "", "", "", "0|不参保",
					"0|不参保", "0|不参保", "0|不参保", "0|不参保", "0|不参保", "0|不参保" };
			cer.insertRows(toFilePath, "Sheet1", 5, strArr);
			jianyuanMapper.updateExportStatus(jianyuan.getJyId());
		}
		for (int j = 0; j < zengyuanList.size(); j++) {
			Zengyuan zengyuan = zengyuanList.get(j);
			String householdProperty = zengyuan.getHouseholdProperty();
			String nongmingongShiye = "";
			String chengzhenShiye = "";
			String jibenYiliao = "";
			String buchongYiliao = "";
			if (householdProperty.contains("本地")) {
				jibenYiliao = "1|参保";
			} else if (householdProperty.contains("外埠")) {
				buchongYiliao = "1|参保";
			}
			if (householdProperty.contains("农业")) {
				nongmingongShiye = "1|参保";
			} else if (householdProperty.contains("城镇")) {
				chengzhenShiye = "1|参保";
			}
			String[] strArr = new String[] { "11|增员", "11|新参保", zengyuan.getSocialSecurityCardNumber(),
					idCardType(zengyuan.getCertificateType()), zengyuan.getCertificateNumber(),
					zengyuan.getEmployeeName(), sexGz(zengyuan.getCertificateNumber()), "156|中国",
					zengyuan.getSocialSecurityBase(),
					GetBirthDayFromIDCard.getBirthday(zengyuan.getCertificateNumber()), "06|工人", "0|在职", "40|合同",
					householdPropertyGz(householdProperty), zengyuan.getSocialSecurityBeginTime(),
					zengyuan.getMobilePhone(), "1|参保", "1|参保", nongmingongShiye, chengzhenShiye, jibenYiliao,
					buchongYiliao, "1|参保" };
			cer.insertRows(toFilePath, "Sheet1", 5, strArr);
			zengyuanMapper.updateExportStatus(zengyuan.getZyId());
		}
		return toFilePath;
	}

	/**
	 * @param list
	 * @return
	 */
	public String zengjianyuanTemplateDg(List<Zengyuan> zengyuanList, List<Jianyuan> jianyuanList) {
		String fromFilePath = realPath() + "template" + File.separator + "zengjianyuan" + File.separator + "东莞.xlsx";
		String toFilePath = realPath() + "download" + File.separator + "excel" + File.separator
				+ UUID.randomUUID().toString() + ".xlsx";
		CopyFile.copyFile(fromFilePath, toFilePath);
		CreateExcelRow cer = new CreateExcelRow();
		for (int i = 0; i < jianyuanList.size(); i++) {
			Jianyuan jianyuan = jianyuanList.get(i);
			String[] strArr = new String[] { String.valueOf(jianyuanList.size() - i), "减员", jianyuan.getEmployeeName(),
					"居民身份证（户口簿）", jianyuan.getCertificateNumber(), "", "", jianyuan.getDimissionReason(), "工人", "合同制",
					"", "", "", "", "不选择", "不选择", "不选择", "不选择", "不选择", "不选择", "不选择", "不选择", "不选择", "" };
			cer.insertRows(toFilePath, "Sheet1", 9, strArr);
			jianyuanMapper.updateExportStatus(jianyuan.getJyId());
		}
		for (int j = 0; j < zengyuanList.size(); j++) {
			Zengyuan zengyuan = zengyuanList.get(j);
			String[] strArr = new String[] { String.valueOf(zengyuanList.size() - j), "增员", zengyuan.getEmployeeName(),
					zengyuan.getCertificateType(), zengyuan.getCertificateNumber(), "", zengyuan.getNation(), "在职",
					"工人", "合同制", zengyuan.getHouseholdProperty(), zengyuan.getHouseholdCity(),
					zengyuan.getReportingPeriod(), zengyuan.getSocialSecurityBase(), "选择", "选择", "选择", "选择", "选择",
					"不选择", "不选择", "不选择", "不选择", zengyuan.getMobilePhone() };
			cer.insertRows(toFilePath, "Sheet1", 9, strArr);
			zengyuanMapper.updateExportStatus(zengyuan.getZyId());
		}
		return toFilePath;
	}

	/**
	 * @param type
	 * @return
	 */
	protected String idCardType(String type) {
		String idCardType = "6|身份证";
		if (type.equals("护照")) {
			idCardType = "1|护照";
		}
		return idCardType;
	}

	/**
	 * @param type
	 * @return
	 */
	protected String sexGz(String idCard) {
		String sex = "0|男";
		if ("女".equals(GetSexFromIDCard.getSex(idCard))) {
			sex = "1|女";
		}
		return sex;
	}

	/**
	 * @param type
	 * @return
	 */
	protected String householdPropertyGz(String type) {
		String horseholdProperty = null;
		switch (type) {
		case "本地城镇":
			horseholdProperty = "03|本地非农业户口";
			break;
		case "本地农业":
			horseholdProperty = "04|本地农业户口";
			break;
		case "外埠城镇":
			horseholdProperty = "05|外地非农业户口";
			break;
		case "外埠农业":
			horseholdProperty = "06|外地农业户口";
			break;
		default:
			horseholdProperty = null;
			break;
		}
		return horseholdProperty;
	}

	/**
	 * @param type
	 * @return
	 */
	private String demilissionTypeGz(String type) {
		String demission = null;
		switch (type) {
		case "辞职":
			demission = "28|辞职";
			break;
		case "辞退":
			demission = "29|解雇";
			break;
		case "合同到期":
			demission = "99|其他";
			break;
		default:
			demission = null;
			break;
		}
		return demission;
	}

}