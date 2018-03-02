package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ZengyuanYichang {
    private Integer ycId;

    private Integer userId;
    
    private Integer deptId;

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

    private String checkCode;

    private String yichangReason;
    
    public ZengyuanYichang(Integer userId,Integer deptId, String financialAccountingUnit, String customerName, String employeeName,
			String certificateType, String certificateNumber, String nation, String mobilePhone, String isExternalCall,
			String householdCity, String householdProperty, String city, String socialSecurityCardNumber,
			String socialSecurityBase, String socialSecurityType, String supplier, String socialSecurityBeginTime,
			String accumulationFundNumber, String accumulationFundCardinalNumber, String accumulationFundRatio,
			String accumulationFundBeginTime, Date importDate, String remark, String reportingPeriod, String checkCode,
			String yichangReason) {
		super();
		this.userId = userId;
		this.deptId = deptId;
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
		this.checkCode = checkCode;
		this.yichangReason = yichangReason;
	}

	public ZengyuanYichang(Integer ycId, Integer userId,Integer deptId, String financialAccountingUnit, String customerName, String employeeName, String certificateType, String certificateNumber, String nation, String mobilePhone, String isExternalCall, String householdCity, String householdProperty, String city, String socialSecurityCardNumber, String socialSecurityBase, String socialSecurityType, String supplier, String socialSecurityBeginTime, String accumulationFundNumber, String accumulationFundCardinalNumber, String accumulationFundRatio, String accumulationFundBeginTime, Date importDate, String remark, String reportingPeriod, String checkCode, String yichangReason) {
        this.ycId = ycId;
        this.userId = userId;
        this.deptId = deptId;
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
        this.checkCode = checkCode;
        this.yichangReason = yichangReason;
    }
}