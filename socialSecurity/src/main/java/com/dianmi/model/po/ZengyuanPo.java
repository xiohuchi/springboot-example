package com.dianmi.model.po;

import lombok.Data;

import java.util.Date;

/**
 * @author www
 *
 */
@Data
public class ZengyuanPo {
    private Integer id;
    
    private Integer supplierId;
    
    private String operator;

    private String customerTag;

    private String supplierName;

    private String fuwufanganName;

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


    @Override
    public String toString() {
        return "ZengyuanPo{" +
                "id=" + id +
                ", customerTag:'" + customerTag + '\'' +
                ", supplierName:'" + supplierName + '\'' +
                ", fuwufanganName:'" + fuwufanganName + '\'' +
                ", financialAccountingUnit:'" + financialAccountingUnit + '\'' +
                ", customerName:'" + customerName + '\'' +
                ", employeeName:'" + employeeName + '\'' +
                ", certificateType:'" + certificateType + '\'' +
                ", certificateNumber:'" + certificateNumber + '\'' +
                ", nation:'" + nation + '\'' +
                ", mobilePhone:'" + mobilePhone + '\'' +
                ", isExternalCall:'" + isExternalCall + '\'' +
                ", householdCity:'" + householdCity + '\'' +
                ", householdProperty:'" + householdProperty + '\'' +
                ", city:'" + city + '\'' +
                ", socialSecurityCardNumber:'" + socialSecurityCardNumber + '\'' +
                ", socialSecurityBase:'" + socialSecurityBase + '\'' +
                ", socialSecurityType:'" + socialSecurityType + '\'' +
                ", supplier:'" + supplier + '\'' +
                ", socialSecurityBeginTime:'" + socialSecurityBeginTime + '\'' +
                ", accumulationFundNumber:'" + accumulationFundNumber + '\'' +
                ", accumulationFundCardinalNumber:'" + accumulationFundCardinalNumber + '\'' +
                ", accumulationFundRatio:'" + accumulationFundRatio + '\'' +
                ", accumulationFundBeginTime:'" + accumulationFundBeginTime + '\'' +
                ", importDate:" + importDate +
                ", remark:'" + remark + '\'' +
                ", reportingPeriod:'" + reportingPeriod + '\'' +
                ", status:" + status +
                '}';
    }
}