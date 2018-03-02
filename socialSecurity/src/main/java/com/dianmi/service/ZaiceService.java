package com.dianmi.service;

import java.util.List;
import java.util.Map;

import com.dianmi.model.User;
import com.dianmi.model.Zaice;
import com.dianmi.model.po.ZaicePo;
import com.dianmi.utils.json.ResultJson;

/**
 * created by www 2017/10/25 19:23
 */
public interface ZaiceService {
	List<ZaicePo> zaicePaging(int type, String reportingPeriod, Integer userId, String paramName, Integer supplierId, Integer customerId, String cityName);

	Zaice selectByEmployeeMsg(String city, String reportingPeriod, String certificateNumber);

	int updateDeleteFlag(Integer zcId);

	void updateBillDetailStatus(Integer zaiceId, Byte isMatchs);

	String selectSupplierBySupplierId(String city, Integer supplierId);

	String selectHouseType(String cITY, String yearMonth, String certificateNumber);

	Integer getSupplierIdBySupplier(String city, String supplier, String certificateNumber);

	String findAccountingUnit(String city, Integer supplierId, String yearMonth, Integer userId);

	Map<String, Object> selectZaiceMsg(String reportingPeriod,String city, String certificateNumber);

	void updateAddSocialStatus(String yearMonth,String city,  String certificateNumber);

	void syncPreMonthZaice();

	String exportZaice(int type, String yearMonth, Integer userId, Integer supplierId, Integer customerId, String cityName);

	/**
	 * 查公积金信息
	 */
	Zaice findAccumulationInfos(String city, String month, String certificateNum);

	/**
	 * 查证件类型
	 */
	String findCertificateType(String name, String certificateNum, Integer supplierIds);

	String findAccumulationFundBeginTime(String city, Integer supplierIds, String month, String certificateNum);

	ResultJson findCustomerAndSupplierAndCity(String paramName, User user);

    ResultJson baseZaicePageList(Integer currPage, Integer pageSize, String customerName, String yearMonth, Integer type, User user);

    ResultJson exportZaiceSummary(String yearMonth, Integer type, User user);

    ResultJson zaiceStatistics(String yearMonth, User user);
}
