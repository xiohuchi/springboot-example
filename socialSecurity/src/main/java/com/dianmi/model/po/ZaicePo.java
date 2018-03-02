package com.dianmi.model.po;

import lombok.Data;

import java.util.Date;

@Data
public class ZaicePo {
    private Integer id;

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

    private Byte zaiceFlag;
}