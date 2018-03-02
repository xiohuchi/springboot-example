package com.dianmi.model.po;


import lombok.Data;

@Data
public class FuwufanganPo {
	private Integer fwId;
	private String cityId;
	private String supplier;
	private String customer;
	private String productName;
	private String householdProperty;
	private String socialSecurityType;
	private String implementDate;
	private Double socialSecurityMinBase;
	private Double pensionCompanyPay;
	private Double pensionPersonPay;
	private Double medicalCompanyPay;
	private Double medicalPersonPay;
	private Double unemploymentCompanyPay;
	private Double unemploymentPersonPay;
	private Double seriousIllnessCompanyPay;
	private Double seriousIllnessPersonPay;
	private Double procreateCompanyPay;
	private Double otherCompanyPay;
	private Double otherPersonPay;
	private Double injuryCompanyPay;
	private Double serviceCharge;
	private Double disabilityGuaranteeFund;
	private String version;
	
}