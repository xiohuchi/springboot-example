package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CostDg {
    private Integer cdId;

    private Integer userId;

    private Integer customerId;
    
    private Integer supplierId;

    private String customerName;

    private String reportingPeriod;

    private String name;

    private String certificateNumber;

    private Double pensionRadix;

    private Double pensionCompanyPay;

    private Double pensionPersonPay;

    private Double pensionCompanySupplementPay;

    private Double pensionCompanyPersonPay;

    private Double pensionSupplementInterest;

    private Double pensionLateFees;

    private Double unemploymentRadix;

    private Double unemploymentCompanyPay;

    private Double unemploymentPersonPay;

    private Double unemploymentCompanySupplementPay;

    private Double unemploymentCompanyPersonPay;

    private Double unemploymentSupplementInterest;

    private Double unemploymentLateFees;

    private Double injuryRadix;

    private Double injuryCompanyPay;

    private Double injuryPersonPay;

    private Double injuryCompanySupplementPay;

    private Double injuryCompanyPersonPay;

    private Double injurySupplementInterest;

    private Double injuryLateFees;

    private Double procreateRadix;

    private Double procreateCompanyPay;

    private Double procreatePersonPay;

    private Double procreateCompanySupplementPay;

    private Double procreateCompanyPersonPay;

    private Double procreateSupplementInterest;

    private Double procreateLateFees;

    private Double medicalTreatmentRadix;

    private Double medicalTreatmentCompanyPay;

    private Double medicalTreatmentPersonPay;

    private Double medicalTreatmentCompanySupplementPay;

    private Double medicalTreatmentCompanyPersonPay;

    private Double medicalTreatmentSupplementInterest;

    private Double medicalTreatmentLateFees;

    private Double seriousIllnessTreatmentRadix;

    private Double seriousIllnessTreatmentCompanyPay;

    private Double seriousIllnessTreatmentPersonPay;

    private Double seriousIllnessTreatmentCompanySupplementPay;

    private Double seriousIllnessTreatmentCompanyPersonPay;

    private Double seriousIllnessTreatmentSupplementInterest;

    private Double seriousIllnessTreatmentLateFees;

    private Double disabilityBenefit;

    private Double serviceCharge;

    private Double socialSecurityTotal;

    private Double accumulationFundRadix;

    private Double accumulationFundOldRadix;

    private String accumulationFundOldRatio;

    private String accumulationFundPersonRatio;

    private String accumulationFundCompanyRatio;

    private Double accumulationFundCompanyPay;

    private Double accumulationFundPersonPay;

    private Double accumulationFundTotal;

    private String accumulationFundAccount;

    private String company;

    private String manager;

	public CostDg(Integer cdId, Integer userId, Integer customerId,Integer supplierId, String customerName, String reportingPeriod, String name, String certificateNumber, Double pensionRadix, Double pensionCompanyPay, Double pensionPersonPay, Double pensionCompanySupplementPay, Double pensionCompanyPersonPay, Double pensionSupplementInterest, Double pensionLateFees, Double unemploymentRadix, Double unemploymentCompanyPay, Double unemploymentPersonPay, Double unemploymentCompanySupplementPay, Double unemploymentCompanyPersonPay, Double unemploymentSupplementInterest, Double unemploymentLateFees, Double injuryRadix, Double injuryCompanyPay, Double injuryPersonPay, Double injuryCompanySupplementPay, Double injuryCompanyPersonPay, Double injurySupplementInterest, Double injuryLateFees, Double procreateRadix, Double procreateCompanyPay, Double procreatePersonPay, Double procreateCompanySupplementPay, Double procreateCompanyPersonPay, Double procreateSupplementInterest, Double procreateLateFees, Double medicalTreatmentRadix, Double medicalTreatmentCompanyPay, Double medicalTreatmentPersonPay, Double medicalTreatmentCompanySupplementPay, Double medicalTreatmentCompanyPersonPay, Double medicalTreatmentSupplementInterest, Double medicalTreatmentLateFees, Double seriousIllnessTreatmentRadix, Double seriousIllnessTreatmentCompanyPay, Double seriousIllnessTreatmentPersonPay, Double seriousIllnessTreatmentCompanySupplementPay, Double seriousIllnessTreatmentCompanyPersonPay, Double seriousIllnessTreatmentSupplementInterest, Double seriousIllnessTreatmentLateFees, Double disabilityBenefit, Double serviceCharge, Double socialSecurityTotal, Double accumulationFundRadix, Double accumulationFundOldRadix, String accumulationFundOldRatio, String accumulationFundPersonRatio, String accumulationFundCompanyRatio, Double accumulationFundCompanyPay, Double accumulationFundPersonPay, Double accumulationFundTotal, String accumulationFundAccount, String company, String manager) {
        this.cdId = cdId;
        this.userId = userId;
        this.customerId = customerId;
        this.supplierId = supplierId;
        this.customerName = customerName;
        this.reportingPeriod = reportingPeriod;
        this.name = name;
        this.certificateNumber = certificateNumber;
        this.pensionRadix = pensionRadix;
        this.pensionCompanyPay = pensionCompanyPay;
        this.pensionPersonPay = pensionPersonPay;
        this.pensionCompanySupplementPay = pensionCompanySupplementPay;
        this.pensionCompanyPersonPay = pensionCompanyPersonPay;
        this.pensionSupplementInterest = pensionSupplementInterest;
        this.pensionLateFees = pensionLateFees;
        this.unemploymentRadix = unemploymentRadix;
        this.unemploymentCompanyPay = unemploymentCompanyPay;
        this.unemploymentPersonPay = unemploymentPersonPay;
        this.unemploymentCompanySupplementPay = unemploymentCompanySupplementPay;
        this.unemploymentCompanyPersonPay = unemploymentCompanyPersonPay;
        this.unemploymentSupplementInterest = unemploymentSupplementInterest;
        this.unemploymentLateFees = unemploymentLateFees;
        this.injuryRadix = injuryRadix;
        this.injuryCompanyPay = injuryCompanyPay;
        this.injuryPersonPay = injuryPersonPay;
        this.injuryCompanySupplementPay = injuryCompanySupplementPay;
        this.injuryCompanyPersonPay = injuryCompanyPersonPay;
        this.injurySupplementInterest = injurySupplementInterest;
        this.injuryLateFees = injuryLateFees;
        this.procreateRadix = procreateRadix;
        this.procreateCompanyPay = procreateCompanyPay;
        this.procreatePersonPay = procreatePersonPay;
        this.procreateCompanySupplementPay = procreateCompanySupplementPay;
        this.procreateCompanyPersonPay = procreateCompanyPersonPay;
        this.procreateSupplementInterest = procreateSupplementInterest;
        this.procreateLateFees = procreateLateFees;
        this.medicalTreatmentRadix = medicalTreatmentRadix;
        this.medicalTreatmentCompanyPay = medicalTreatmentCompanyPay;
        this.medicalTreatmentPersonPay = medicalTreatmentPersonPay;
        this.medicalTreatmentCompanySupplementPay = medicalTreatmentCompanySupplementPay;
        this.medicalTreatmentCompanyPersonPay = medicalTreatmentCompanyPersonPay;
        this.medicalTreatmentSupplementInterest = medicalTreatmentSupplementInterest;
        this.medicalTreatmentLateFees = medicalTreatmentLateFees;
        this.seriousIllnessTreatmentRadix = seriousIllnessTreatmentRadix;
        this.seriousIllnessTreatmentCompanyPay = seriousIllnessTreatmentCompanyPay;
        this.seriousIllnessTreatmentPersonPay = seriousIllnessTreatmentPersonPay;
        this.seriousIllnessTreatmentCompanySupplementPay = seriousIllnessTreatmentCompanySupplementPay;
        this.seriousIllnessTreatmentCompanyPersonPay = seriousIllnessTreatmentCompanyPersonPay;
        this.seriousIllnessTreatmentSupplementInterest = seriousIllnessTreatmentSupplementInterest;
        this.seriousIllnessTreatmentLateFees = seriousIllnessTreatmentLateFees;
        this.disabilityBenefit = disabilityBenefit;
        this.serviceCharge = serviceCharge;
        this.socialSecurityTotal = socialSecurityTotal;
        this.accumulationFundRadix = accumulationFundRadix;
        this.accumulationFundOldRadix = accumulationFundOldRadix;
        this.accumulationFundOldRatio = accumulationFundOldRatio;
        this.accumulationFundPersonRatio = accumulationFundPersonRatio;
        this.accumulationFundCompanyRatio = accumulationFundCompanyRatio;
        this.accumulationFundCompanyPay = accumulationFundCompanyPay;
        this.accumulationFundPersonPay = accumulationFundPersonPay;
        this.accumulationFundTotal = accumulationFundTotal;
        this.accumulationFundAccount = accumulationFundAccount;
        this.company = company;
        this.manager = manager;
    }



	public CostDg(Integer userId, Integer customerId, Integer supplierId, String customerName, String reportingPeriod,
			String name, String certificateNumber, Double accumulationFundRadix, Double accumulationFundOldRadix,
			String accumulationFundOldRatio, String accumulationFundPersonRatio, String accumulationFundCompanyRatio,
			Double accumulationFundCompanyPay, Double accumulationFundPersonPay, Double accumulationFundTotal,
			String accumulationFundAccount, String company, String manager) {
		super();
		this.userId = userId;
		this.customerId = customerId;
		this.supplierId = supplierId;
		this.customerName = customerName;
		this.reportingPeriod = reportingPeriod;
		this.name = name;
		this.certificateNumber = certificateNumber;
		this.accumulationFundRadix = accumulationFundRadix;
		this.accumulationFundOldRadix = accumulationFundOldRadix;
		this.accumulationFundOldRatio = accumulationFundOldRatio;
		this.accumulationFundPersonRatio = accumulationFundPersonRatio;
		this.accumulationFundCompanyRatio = accumulationFundCompanyRatio;
		this.accumulationFundCompanyPay = accumulationFundCompanyPay;
		this.accumulationFundPersonPay = accumulationFundPersonPay;
		this.accumulationFundTotal = accumulationFundTotal;
		this.accumulationFundAccount = accumulationFundAccount;
		this.company = company;
		this.manager = manager;
	}
}