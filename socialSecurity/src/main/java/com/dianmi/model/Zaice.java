package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Zaice {
	private Integer zcId;

	private Integer userId;
	
	private Integer deptId;

	private Integer customerId;

	private Integer supplierId;

	private Integer fuwufanganId;

	private String financialAccountingUnit;

	private String customerName;

	private String employeeName;

	private String certificateType;

	private String certificateNumber;

	private String nation;

	private String mobilePhone;

	private String isExternalCall;

	private String householdCity;

	private String householdProperty;

	private String city;

	private String socialSecurityCardNumber;

	private String socialSecurityBase;

	private String socialSecurityType;

	private String supplier;

	private String socialSecurityBeginTime;

	private String accumulationFundNumber;

	private String accumulationFundCardinalNumber;

	private String accumulationFundRatio;

	private String accumulationFundBeginTime;

	private Date importDate;

	private String remark;

	private String reportingPeriod;

	private Byte status;

	private Byte zaiceFlag;

	private Byte deleteFlag;

	public Zaice(Integer userId,Integer deptId, Integer customerId, Integer supplierId, Integer fuwufanganId,
			String financialAccountingUnit, String customerName, String employeeName, String certificateType,
			String certificateNumber, String nation, String mobilePhone, String isExternalCall, String householdCity,
			String householdProperty, String city, String socialSecurityCardNumber, String socialSecurityBase,
			String socialSecurityType, String supplier, String socialSecurityBeginTime, String accumulationFundNumber,
			String accumulationFundCardinalNumber, String accumulationFundRatio, String accumulationFundBeginTime,
			Date importDate, String remark, String reportingPeriod, Byte status, Byte zaiceFlag, Byte deleteFlag) {
		super();
		this.userId = userId;
		this.deptId = deptId;
		this.customerId = customerId;
		this.supplierId = supplierId;
		this.fuwufanganId = fuwufanganId;
		this.financialAccountingUnit = financialAccountingUnit;
		this.customerName = customerName;
		this.employeeName = employeeName;
		this.certificateType = certificateType;
		this.certificateNumber = certificateNumber;
		this.nation = nation;
		this.mobilePhone = mobilePhone;
		this.isExternalCall = isExternalCall;
		this.householdCity = householdCity;
		this.householdProperty = householdProperty;
		this.city = city;
		this.socialSecurityCardNumber = socialSecurityCardNumber;
		this.socialSecurityBase = socialSecurityBase;
		this.socialSecurityType = socialSecurityType;
		this.supplier = supplier;
		this.socialSecurityBeginTime = socialSecurityBeginTime;
		this.accumulationFundNumber = accumulationFundNumber;
		this.accumulationFundCardinalNumber = accumulationFundCardinalNumber;
		this.accumulationFundRatio = accumulationFundRatio;
		this.accumulationFundBeginTime = accumulationFundBeginTime;
		this.importDate = importDate;
		this.remark = remark;
		this.reportingPeriod = reportingPeriod;
		this.status = status;
		this.zaiceFlag = zaiceFlag;
		this.deleteFlag = deleteFlag;
	}

	public Zaice(Integer zcId, Integer userId, Integer deptId,Integer customerId, Integer supplierId, Integer fuwufanganId,
			String financialAccountingUnit, String customerName, String employeeName, String certificateType,
			String certificateNumber, String nation, String mobilePhone, String isExternalCall, String householdCity,
			String householdProperty, String city, String socialSecurityCardNumber, String socialSecurityBase,
			String socialSecurityType, String supplier, String socialSecurityBeginTime, String accumulationFundNumber,
			String accumulationFundCardinalNumber, String accumulationFundRatio, String accumulationFundBeginTime,
			Date importDate, String remark, String reportingPeriod, Byte status, Byte zaiceFlag, Byte deleteFlag) {
		this.zcId = zcId;
		this.userId = userId;
		this.deptId = deptId;
		this.customerId = customerId;
		this.supplierId = supplierId;
		this.fuwufanganId = fuwufanganId;
		this.financialAccountingUnit = financialAccountingUnit;
		this.customerName = customerName;
		this.employeeName = employeeName;
		this.certificateType = certificateType;
		this.certificateNumber = certificateNumber;
		this.nation = nation;
		this.mobilePhone = mobilePhone;
		this.isExternalCall = isExternalCall;
		this.householdCity = householdCity;
		this.householdProperty = householdProperty;
		this.city = city;
		this.socialSecurityCardNumber = socialSecurityCardNumber;
		this.socialSecurityBase = socialSecurityBase;
		this.socialSecurityType = socialSecurityType;
		this.supplier = supplier;
		this.socialSecurityBeginTime = socialSecurityBeginTime;
		this.accumulationFundNumber = accumulationFundNumber;
		this.accumulationFundCardinalNumber = accumulationFundCardinalNumber;
		this.accumulationFundRatio = accumulationFundRatio;
		this.accumulationFundBeginTime = accumulationFundBeginTime;
		this.importDate = importDate;
		this.remark = remark;
		this.reportingPeriod = reportingPeriod;
		this.status = status;
		this.zaiceFlag = zaiceFlag;
		this.deleteFlag = deleteFlag;
	}
}