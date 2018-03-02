package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SupplierCost {
    private Integer scId;

    private Integer userId;

    private Integer supplierId;

    private String reportingPeriod;

    private String employeeName;

    private String certificateNumber;

    private String socialSecurityNumber;

    private String accumulationFundNumber;

    private String city;

    private String residentNature;

    private String socialSecurityBeginTime;

    private String paymentMonth;

    private Double socialSecurityCardinalRadix;

    private Double pensionCompanyRadix;

    private Double pensionCompanyPay;

    private Double pensionPersonRadix;

    private Double pensionPersonPay;

    private Double unemploymentCompanyRadix;

    private Double unemploymentCompanyPay;

    private Double unemploymentPersonRadix;

    private Double unemploymentPersonPay;

    private Double injuryCompanyRadix;

    private Double injuryCompanyPay;

    private Double procreateCompanyRadix;

    private Double procreateCompanyPay;

    private Double medicalTreatmentCompanyRadix;

    private Double medicalTreatmentCompanyPay;

    private Double medicalTreatmentPersonRadix;

    private Double medicalTreatmentPersonPay;

    private Double seriousIllnessCompanyRedix;

    private Double seriousIllnessCompanyPay;

    private Double seriousIllnessPersonRedix;

    private Double seriousIllnessPersonPay;

    private Double accumulationFundCompanyRedix;

    private String accumulationFundCompanyRatio;

    private Double accumulationFundCompanyPay;

    private Double accumulationFundPersonRedix;

    private String accumulationFundPersonRatio;

    private Double accumulationFundPersonPay;

    private Double otherChargesCompany;

    private Double otherChargesPerson;

    private Double disabilityBenefit;

    private Double serviceCharge;

    private Double companyTotal;

    private Double personTotal;

    private Double companyRefund;

    private Double total;

    private String remark;

    private String accountingUnit;

    private String customerName;

    private String supplierName;



    //---------------------
    private Double shebaoheji;
    private Double gongjijinheji;

    public SupplierCost(Integer scId, Integer userId, Integer supplierId, String reportingPeriod, String employeeName, String certificateNumber, String socialSecurityNumber, String accumulationFundNumber, String city, String residentNature, String socialSecurityBeginTime, String paymentMonth, Double socialSecurityCardinalRadix, Double pensionCompanyRadix, Double pensionCompanyPay, Double pensionPersonRadix, Double pensionPersonPay, Double unemploymentCompanyRadix, Double unemploymentCompanyPay, Double unemploymentPersonRadix, Double unemploymentPersonPay, Double injuryCompanyRadix, Double injuryCompanyPay, Double procreateCompanyRadix, Double procreateCompanyPay, Double medicalTreatmentCompanyRadix, Double medicalTreatmentCompanyPay, Double medicalTreatmentPersonRadix, Double medicalTreatmentPersonPay, Double seriousIllnessCompanyRedix, Double seriousIllnessCompanyPay, Double seriousIllnessPersonRedix, Double seriousIllnessPersonPay, Double accumulationFundCompanyRedix, String accumulationFundCompanyRatio, Double accumulationFundCompanyPay, Double accumulationFundPersonRedix, String accumulationFundPersonRatio, Double accumulationFundPersonPay, Double otherChargesCompany, Double otherChargesPerson, Double disabilityBenefit, Double serviceCharge, Double companyTotal, Double personTotal, Double companyRefund, Double total, String remark, String accountingUnit, String customerName, String supplierName) {
        this.scId = scId;
        this.userId = userId;
        this.supplierId = supplierId;
        this.reportingPeriod = reportingPeriod;
        this.employeeName = employeeName;
        this.certificateNumber = certificateNumber;
        this.socialSecurityNumber = socialSecurityNumber;
        this.accumulationFundNumber = accumulationFundNumber;
        this.city = city;
        this.residentNature = residentNature;
        this.socialSecurityBeginTime = socialSecurityBeginTime;
        this.paymentMonth = paymentMonth;
        this.socialSecurityCardinalRadix = socialSecurityCardinalRadix;
        this.pensionCompanyRadix = pensionCompanyRadix;
        this.pensionCompanyPay = pensionCompanyPay;
        this.pensionPersonRadix = pensionPersonRadix;
        this.pensionPersonPay = pensionPersonPay;
        this.unemploymentCompanyRadix = unemploymentCompanyRadix;
        this.unemploymentCompanyPay = unemploymentCompanyPay;
        this.unemploymentPersonRadix = unemploymentPersonRadix;
        this.unemploymentPersonPay = unemploymentPersonPay;
        this.injuryCompanyRadix = injuryCompanyRadix;
        this.injuryCompanyPay = injuryCompanyPay;
        this.procreateCompanyRadix = procreateCompanyRadix;
        this.procreateCompanyPay = procreateCompanyPay;
        this.medicalTreatmentCompanyRadix = medicalTreatmentCompanyRadix;
        this.medicalTreatmentCompanyPay = medicalTreatmentCompanyPay;
        this.medicalTreatmentPersonRadix = medicalTreatmentPersonRadix;
        this.medicalTreatmentPersonPay = medicalTreatmentPersonPay;
        this.seriousIllnessCompanyRedix = seriousIllnessCompanyRedix;
        this.seriousIllnessCompanyPay = seriousIllnessCompanyPay;
        this.seriousIllnessPersonRedix = seriousIllnessPersonRedix;
        this.seriousIllnessPersonPay = seriousIllnessPersonPay;
        this.accumulationFundCompanyRedix = accumulationFundCompanyRedix;
        this.accumulationFundCompanyRatio = accumulationFundCompanyRatio;
        this.accumulationFundCompanyPay = accumulationFundCompanyPay;
        this.accumulationFundPersonRedix = accumulationFundPersonRedix;
        this.accumulationFundPersonRatio = accumulationFundPersonRatio;
        this.accumulationFundPersonPay = accumulationFundPersonPay;
        this.otherChargesCompany = otherChargesCompany;
        this.otherChargesPerson = otherChargesPerson;
        this.disabilityBenefit = disabilityBenefit;
        this.serviceCharge = serviceCharge;
        this.companyTotal = companyTotal;
        this.personTotal = personTotal;
        this.companyRefund = companyRefund;
        this.total = total;
        this.remark = remark;
        this.accountingUnit = accountingUnit;
        this.customerName = customerName;
        this.supplierName = supplierName;
    }

}