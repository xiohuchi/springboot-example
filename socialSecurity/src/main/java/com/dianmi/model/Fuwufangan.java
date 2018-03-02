package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Fuwufangan {
    private Integer fwId;

    private Byte type;

    private String cityId;

    private Integer supplierId;

    private Integer customerId;

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

    private Double injuryCompanyPay;

    private Double accumulationFundCompanyPay;

    private Double accumulationFundPersonPay;

    private Double otherCompanyPay;

    private Double otherPersonPay;

    private Double serviceCharge;

    private Double disabilityGuaranteeFund;

    private String version;

    public Fuwufangan(Integer fwId, Byte type, String cityId, Integer supplierId, Integer customerId, String productName, String householdProperty, String socialSecurityType, String implementDate, Double socialSecurityMinBase, Double pensionCompanyPay, Double pensionPersonPay, Double medicalCompanyPay, Double medicalPersonPay, Double unemploymentCompanyPay, Double unemploymentPersonPay, Double seriousIllnessCompanyPay, Double seriousIllnessPersonPay, Double procreateCompanyPay, Double injuryCompanyPay, Double accumulationFundCompanyPay, Double accumulationFundPersonPay, Double otherCompanyPay, Double otherPersonPay, Double serviceCharge, Double disabilityGuaranteeFund, String version) {
        this.fwId = fwId;
        this.type = type;
        this.cityId = cityId;
        this.supplierId = supplierId;
        this.customerId = customerId;
        this.productName = productName;
        this.householdProperty = householdProperty;
        this.socialSecurityType = socialSecurityType;
        this.implementDate = implementDate;
        this.socialSecurityMinBase = socialSecurityMinBase;
        this.pensionCompanyPay = pensionCompanyPay;
        this.pensionPersonPay = pensionPersonPay;
        this.medicalCompanyPay = medicalCompanyPay;
        this.medicalPersonPay = medicalPersonPay;
        this.unemploymentCompanyPay = unemploymentCompanyPay;
        this.unemploymentPersonPay = unemploymentPersonPay;
        this.seriousIllnessCompanyPay = seriousIllnessCompanyPay;
        this.seriousIllnessPersonPay = seriousIllnessPersonPay;
        this.procreateCompanyPay = procreateCompanyPay;
        this.injuryCompanyPay = injuryCompanyPay;
        this.accumulationFundCompanyPay = accumulationFundCompanyPay;
        this.accumulationFundPersonPay = accumulationFundPersonPay;
        this.otherCompanyPay = otherCompanyPay;
        this.otherPersonPay = otherPersonPay;
        this.serviceCharge = serviceCharge;
        this.disabilityGuaranteeFund = disabilityGuaranteeFund;
        this.version = version;
    }
}