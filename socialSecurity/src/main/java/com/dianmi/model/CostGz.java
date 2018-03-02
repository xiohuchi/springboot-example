package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CostGz {
    private Integer cgId;

    private Integer userId;

    private Integer customerId;
    
    private Integer supplierId;

    private String customerName;

    private String reportingPeriod;

    private String name;

    private String certificateNumber;

    private String certificateName;

    private String socialSecurityNumber;

    private String paymentMonth;

    private Double pensionRadix;

    private Double pensionCompanyPay;

    private Double pensionPersonPay;

    private Double unemploymentRadix;

    private Double unemploymentCompanyPay;

    private Double unemploymentPersonPay;

    private Double injuryCompanyRadix;

    private Double injuryCompanyPay;

    private Double injuryPersonPay;

    private Double procreateCompanyRadix;

    private Double procreateCompanyPay;

    private Double procreatePersonPay;

    private Double medicalTreatmentRadix;

    private Double medicalTreatmentCompanyPay;

    private Double medicalTreatmentPersonPay;

    private Double seriousIllnessRadix;

    private Double seriousIllnessCompanyPay;

    private Double seriousIllnessPersonPay;

    private Double serviceCharge;

    private Double companyTotal;

    private Double personTotal;

    private Double socialSecurityTotal;

    private Double accumulationFundRadix;

    private String accumulationFundCompanyRatio;

    private Double accumulationFundCompanyPay;

    private String accumulationFundPersonRatio;

    private Double accumulationFundPersonPay;

    private Double accumulationFundTotal;

    private String systemPersonNumber;

    private String company;

    private String manager;

   

	public CostGz(Integer userId, Integer customerId, Integer supplierId, String customerName, String reportingPeriod,
			String name, String certificateNumber, String certificateName, String socialSecurityNumber,
			String paymentMonth, Double pensionRadix, Double pensionCompanyPay, Double pensionPersonPay,
			Double unemploymentRadix, Double unemploymentCompanyPay, Double unemploymentPersonPay,
			Double injuryCompanyRadix, Double injuryCompanyPay, Double injuryPersonPay, Double procreateCompanyRadix,
			Double procreateCompanyPay, Double procreatePersonPay, Double medicalTreatmentRadix,
			Double medicalTreatmentCompanyPay, Double medicalTreatmentPersonPay, Double seriousIllnessRadix,
			Double seriousIllnessCompanyPay, Double seriousIllnessPersonPay, Double serviceCharge, Double companyTotal,
			Double personTotal, Double socialSecurityTotal, Double accumulationFundRadix,
			String accumulationFundCompanyRatio, Double accumulationFundCompanyPay, String accumulationFundPersonRatio,
			Double accumulationFundPersonPay, Double accumulationFundTotal, String systemPersonNumber, String company,
			String manager) {
		super();
		this.userId = userId;
		this.customerId = customerId;
		this.supplierId = supplierId;
		this.customerName = customerName;
		this.reportingPeriod = reportingPeriod;
		this.name = name;
		this.certificateNumber = certificateNumber;
		this.certificateName = certificateName;
		this.socialSecurityNumber = socialSecurityNumber;
		this.paymentMonth = paymentMonth;
		this.pensionRadix = pensionRadix;
		this.pensionCompanyPay = pensionCompanyPay;
		this.pensionPersonPay = pensionPersonPay;
		this.unemploymentRadix = unemploymentRadix;
		this.unemploymentCompanyPay = unemploymentCompanyPay;
		this.unemploymentPersonPay = unemploymentPersonPay;
		this.injuryCompanyRadix = injuryCompanyRadix;
		this.injuryCompanyPay = injuryCompanyPay;
		this.injuryPersonPay = injuryPersonPay;
		this.procreateCompanyRadix = procreateCompanyRadix;
		this.procreateCompanyPay = procreateCompanyPay;
		this.procreatePersonPay = procreatePersonPay;
		this.medicalTreatmentRadix = medicalTreatmentRadix;
		this.medicalTreatmentCompanyPay = medicalTreatmentCompanyPay;
		this.medicalTreatmentPersonPay = medicalTreatmentPersonPay;
		this.seriousIllnessRadix = seriousIllnessRadix;
		this.seriousIllnessCompanyPay = seriousIllnessCompanyPay;
		this.seriousIllnessPersonPay = seriousIllnessPersonPay;
		this.serviceCharge = serviceCharge;
		this.companyTotal = companyTotal;
		this.personTotal = personTotal;
		this.socialSecurityTotal = socialSecurityTotal;
		this.accumulationFundRadix = accumulationFundRadix;
		this.accumulationFundCompanyRatio = accumulationFundCompanyRatio;
		this.accumulationFundCompanyPay = accumulationFundCompanyPay;
		this.accumulationFundPersonRatio = accumulationFundPersonRatio;
		this.accumulationFundPersonPay = accumulationFundPersonPay;
		this.accumulationFundTotal = accumulationFundTotal;
		this.systemPersonNumber = systemPersonNumber;
		this.company = company;
		this.manager = manager;
	}

	public CostGz(Integer userId,Integer customerId, Integer supplierId, String customerName, String reportingPeriod, String name,
			String certificateNumber, Double accumulationFundRadix, String accumulationFundCompanyRatio,
			Double accumulationFundCompanyPay, String accumulationFundPersonRatio, Double accumulationFundPersonPay,
			Double accumulationFundTotal, String systemPersonNumber, String company, String manager) {
		super();
		this.userId = userId;
		this.customerId = customerId;
		this.supplierId = supplierId;
		this.customerName = customerName;
		this.reportingPeriod = reportingPeriod;
		this.name = name;
		this.certificateNumber = certificateNumber;
		this.accumulationFundRadix = accumulationFundRadix;
		this.accumulationFundCompanyRatio = accumulationFundCompanyRatio;
		this.accumulationFundCompanyPay = accumulationFundCompanyPay;
		this.accumulationFundPersonRatio = accumulationFundPersonRatio;
		this.accumulationFundPersonPay = accumulationFundPersonPay;
		this.accumulationFundTotal = accumulationFundTotal;
		this.systemPersonNumber = systemPersonNumber;
		this.company = company;
		this.manager = manager;
	}

	public CostGz(Integer cgId, Integer userId, Integer customerId, Integer supplierId, String customerName,
			String reportingPeriod, String name, String certificateNumber, String certificateName,
			String socialSecurityNumber, String paymentMonth, Double pensionRadix, Double pensionCompanyPay,
			Double pensionPersonPay, Double unemploymentRadix, Double unemploymentCompanyPay,
			Double unemploymentPersonPay, Double injuryCompanyRadix, Double injuryCompanyPay, Double injuryPersonPay,
			Double procreateCompanyRadix, Double procreateCompanyPay, Double procreatePersonPay,
			Double medicalTreatmentRadix, Double medicalTreatmentCompanyPay, Double medicalTreatmentPersonPay,
			Double seriousIllnessRadix, Double seriousIllnessCompanyPay, Double seriousIllnessPersonPay,
			Double serviceCharge, Double companyTotal, Double personTotal, Double socialSecurityTotal,
			Double accumulationFundRadix, String accumulationFundCompanyRatio, Double accumulationFundCompanyPay,
			String accumulationFundPersonRatio, Double accumulationFundPersonPay, Double accumulationFundTotal,
			String systemPersonNumber, String company, String manager) {
		super();
		this.cgId = cgId;
		this.userId = userId;
		this.customerId = customerId;
		this.supplierId = supplierId;
		this.customerName = customerName;
		this.reportingPeriod = reportingPeriod;
		this.name = name;
		this.certificateNumber = certificateNumber;
		this.certificateName = certificateName;
		this.socialSecurityNumber = socialSecurityNumber;
		this.paymentMonth = paymentMonth;
		this.pensionRadix = pensionRadix;
		this.pensionCompanyPay = pensionCompanyPay;
		this.pensionPersonPay = pensionPersonPay;
		this.unemploymentRadix = unemploymentRadix;
		this.unemploymentCompanyPay = unemploymentCompanyPay;
		this.unemploymentPersonPay = unemploymentPersonPay;
		this.injuryCompanyRadix = injuryCompanyRadix;
		this.injuryCompanyPay = injuryCompanyPay;
		this.injuryPersonPay = injuryPersonPay;
		this.procreateCompanyRadix = procreateCompanyRadix;
		this.procreateCompanyPay = procreateCompanyPay;
		this.procreatePersonPay = procreatePersonPay;
		this.medicalTreatmentRadix = medicalTreatmentRadix;
		this.medicalTreatmentCompanyPay = medicalTreatmentCompanyPay;
		this.medicalTreatmentPersonPay = medicalTreatmentPersonPay;
		this.seriousIllnessRadix = seriousIllnessRadix;
		this.seriousIllnessCompanyPay = seriousIllnessCompanyPay;
		this.seriousIllnessPersonPay = seriousIllnessPersonPay;
		this.serviceCharge = serviceCharge;
		this.companyTotal = companyTotal;
		this.personTotal = personTotal;
		this.socialSecurityTotal = socialSecurityTotal;
		this.accumulationFundRadix = accumulationFundRadix;
		this.accumulationFundCompanyRatio = accumulationFundCompanyRatio;
		this.accumulationFundCompanyPay = accumulationFundCompanyPay;
		this.accumulationFundPersonRatio = accumulationFundPersonRatio;
		this.accumulationFundPersonPay = accumulationFundPersonPay;
		this.accumulationFundTotal = accumulationFundTotal;
		this.systemPersonNumber = systemPersonNumber;
		this.company = company;
		this.manager = manager;
	}
}