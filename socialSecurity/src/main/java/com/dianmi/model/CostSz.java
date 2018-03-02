package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CostSz {
	private Integer csId;

	private Integer userId;

	private Integer customerId;

	private Integer supplierId;

	private String customerName;

	private String reportingPeriod;

	private String paymentMonth;

	private String personNumber;

	private String name;

	private String certificateNumber;

	private Double pensionRadix;

	private Double pensionCompanyPay;

	private Double pensionPersonPay;

	private Double unemploymentRadix;

	private Double unemploymentCompanyPay;

	private Double unemploymentPersonPay;

	private Double injuryCompanyRadix;

	private Double injuryCompanyPay;

	private Double procreateCompanyRadix;

	private Double procreateCompanyPay;

	private Double medicalTreatmentRadix;

	private Double medicalTreatmentCompanyPay;

	private Double medicalTreatmentPersonPay;

	private Double companyTotal;

	private Double personTotal;

	private Double serviceCharge;

	private Double socialSecurityTotal;

	private String companyNum;

	private String accumulationFundAccount;

	private Double accumulationFundRadix;

	private Double accumulationFundCompanyRatio;

	private Double accumulationFundPersonRatio;

	private Double accumulationFundTotal;

	private String accumulationFundPaymentMonth;

	private String customer;

	public CostSz(Integer userId, Integer customerId, Integer supplierId, String customerName, String reportingPeriod,
			String paymentMonth, String personNumber, String name, String certificateNumber, Double pensionRadix,
			Double pensionCompanyPay, Double pensionPersonPay, Double unemploymentRadix, Double unemploymentCompanyPay,
			Double unemploymentPersonPay, Double injuryCompanyRadix, Double injuryCompanyPay,
			Double procreateCompanyRadix, Double procreateCompanyPay, Double medicalTreatmentRadix,
			Double medicalTreatmentCompanyPay, Double medicalTreatmentPersonPay, Double companyTotal,
			Double personTotal, Double serviceCharge, Double socialSecurityTotal, String companyNum,
			String accumulationFundAccount, Double accumulationFundRadix, Double accumulationFundCompanyRatio,
			Double accumulationFundPersonRatio, Double accumulationFundTotal, String accumulationFundPaymentMonth,
			String customer) {
		super();
		this.userId = userId;
		this.customerId = customerId;
		this.supplierId = supplierId;
		this.customerName = customerName;
		this.reportingPeriod = reportingPeriod;
		this.paymentMonth = paymentMonth;
		this.personNumber = personNumber;
		this.name = name;
		this.certificateNumber = certificateNumber;
		this.pensionRadix = pensionRadix;
		this.pensionCompanyPay = pensionCompanyPay;
		this.pensionPersonPay = pensionPersonPay;
		this.unemploymentRadix = unemploymentRadix;
		this.unemploymentCompanyPay = unemploymentCompanyPay;
		this.unemploymentPersonPay = unemploymentPersonPay;
		this.injuryCompanyRadix = injuryCompanyRadix;
		this.injuryCompanyPay = injuryCompanyPay;
		this.procreateCompanyRadix = procreateCompanyRadix;
		this.procreateCompanyPay = procreateCompanyPay;
		this.medicalTreatmentRadix = medicalTreatmentRadix;
		this.medicalTreatmentCompanyPay = medicalTreatmentCompanyPay;
		this.medicalTreatmentPersonPay = medicalTreatmentPersonPay;
		this.companyTotal = companyTotal;
		this.personTotal = personTotal;
		this.serviceCharge = serviceCharge;
		this.socialSecurityTotal = socialSecurityTotal;
		this.companyNum = companyNum;
		this.accumulationFundAccount = accumulationFundAccount;
		this.accumulationFundRadix = accumulationFundRadix;
		this.accumulationFundCompanyRatio = accumulationFundCompanyRatio;
		this.accumulationFundPersonRatio = accumulationFundPersonRatio;
		this.accumulationFundTotal = accumulationFundTotal;
		this.accumulationFundPaymentMonth = accumulationFundPaymentMonth;
		this.customer = customer;
	}

	public CostSz(Integer csId, Integer userId, Integer customerId, Integer supplierId, String customerName,
			String reportingPeriod, String paymentMonth, String personNumber, String name, String certificateNumber,
			Double pensionRadix, Double pensionCompanyPay, Double pensionPersonPay, Double unemploymentRadix,
			Double unemploymentCompanyPay, Double unemploymentPersonPay, Double injuryCompanyRadix,
			Double injuryCompanyPay, Double procreateCompanyRadix, Double procreateCompanyPay,
			Double medicalTreatmentRadix, Double medicalTreatmentCompanyPay, Double medicalTreatmentPersonPay,
			Double companyTotal, Double personTotal, Double serviceCharge, Double socialSecurityTotal,
			String companyNum, String accumulationFundAccount, Double accumulationFundRadix,
			Double accumulationFundCompanyRatio, Double accumulationFundPersonRatio, Double accumulationFundTotal,
			String accumulationFundPaymentMonth, String customer) {
		super();
		this.csId = csId;
		this.userId = userId;
		this.customerId = customerId;
		this.supplierId = supplierId;
		this.customerName = customerName;
		this.reportingPeriod = reportingPeriod;
		this.paymentMonth = paymentMonth;
		this.personNumber = personNumber;
		this.name = name;
		this.certificateNumber = certificateNumber;
		this.pensionRadix = pensionRadix;
		this.pensionCompanyPay = pensionCompanyPay;
		this.pensionPersonPay = pensionPersonPay;
		this.unemploymentRadix = unemploymentRadix;
		this.unemploymentCompanyPay = unemploymentCompanyPay;
		this.unemploymentPersonPay = unemploymentPersonPay;
		this.injuryCompanyRadix = injuryCompanyRadix;
		this.injuryCompanyPay = injuryCompanyPay;
		this.procreateCompanyRadix = procreateCompanyRadix;
		this.procreateCompanyPay = procreateCompanyPay;
		this.medicalTreatmentRadix = medicalTreatmentRadix;
		this.medicalTreatmentCompanyPay = medicalTreatmentCompanyPay;
		this.medicalTreatmentPersonPay = medicalTreatmentPersonPay;
		this.companyTotal = companyTotal;
		this.personTotal = personTotal;
		this.serviceCharge = serviceCharge;
		this.socialSecurityTotal = socialSecurityTotal;
		this.companyNum = companyNum;
		this.accumulationFundAccount = accumulationFundAccount;
		this.accumulationFundRadix = accumulationFundRadix;
		this.accumulationFundCompanyRatio = accumulationFundCompanyRatio;
		this.accumulationFundPersonRatio = accumulationFundPersonRatio;
		this.accumulationFundTotal = accumulationFundTotal;
		this.accumulationFundPaymentMonth = accumulationFundPaymentMonth;
		this.customer = customer;
	}




	public CostSz(Integer userId, Integer customerId, Integer supplierId, String customerName, String reportingPeriod,
			String name, String certificateNumber, String accumulationFundAccount, Double accumulationFundRadix,
			Double accumulationFundCompanyRatio, Double accumulationFundPersonRatio, Double accumulationFundTotal,
			String accumulationFundPaymentMonth, String customer) {
		super();
		this.userId = userId;
		this.customerId = customerId;
		this.supplierId = supplierId;
		this.customerName = customerName;
		this.reportingPeriod = reportingPeriod;
		this.name = name;
		this.certificateNumber = certificateNumber;
		this.accumulationFundAccount = accumulationFundAccount;
		this.accumulationFundRadix = accumulationFundRadix;
		this.accumulationFundCompanyRatio = accumulationFundCompanyRatio;
		this.accumulationFundPersonRatio = accumulationFundPersonRatio;
		this.accumulationFundTotal = accumulationFundTotal;
		this.accumulationFundPaymentMonth = accumulationFundPaymentMonth;
		this.customer = customer;
	}
	
	

}