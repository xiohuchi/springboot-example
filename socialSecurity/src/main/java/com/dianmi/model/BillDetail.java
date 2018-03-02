package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BillDetail {
	private Integer bdId;

	private Integer zaiceId;

	private Double customerAccountPensionCompanyPay;

	private Double customerAccountPensionPersonPay;

	private Double customerAccountUnemploymentCompanyPay;

	private Double customerAccountUnemploymentPersonPay;

	private Double customerAccountInjuryCompanyPay;

	private Double customerAccountProcreateCompanyPay;

	private Double customerAccountMedicalTreatmentCompanyPay;

	private Double customerAccountMedicalTreatmentPersonPay;

	private Double customerAccountSeriousIllnessCompanyPay;

	private Double customerAccountSeriousIllnessPersonPay;

	private Double customerAccountAccumulationFundCompanyPay;

	private Double customerAccountAccumulationFundPersonPay;

	private Double customerAccountDisabilityBenefitPay;

	private Double customerAccountOtherChargesCompanyPay;

	private String customerAccountOtherChargesCompanyRemark;

	private Double customerAccountOtherChargesPersonPay;

	private String customerAccountOtherChargesPersonRemark;

	private Double customerAccountServiceCharge;

	private Double customerAccountCompanyTotal;

	private Double customerAccountPersonTotal;

	private Double customerAccountTotal;

	private Double supplierAccountPensionCompanyPay;

	private Double supplierAccountPensionPersonPay;

	private Double supplierAccountUnemploymentCompanyPay;

	private Double supplierAccountUnemploymentPersonPay;

	private Double supplierAccountInjuryCompanyPay;

	private Double supplierAccountProcreateCompanyPay;

	private Double supplierAccountMedicalTreatmentCompanyPay;

	private Double supplierAccountMedicalTreatmentPersonPay;

	private Double supplierAccountSeriousIllnessCompanyPay;

	private Double supplierAccountSeriousIllnessPersonPay;

	private Double supplierAccumulationFundCompanyPay;

	private Double supplierAccumulationFundPersonPay;

	private Double supplierAccountDisabilityBenefitPay;

	private Double supplierAccountOtherChargesCompanyPay;

	private String supplierAccountOtherChargesCompanyRemark;

	private Double supplierAccountOtherChargesPersonPay;

	private String supplierAccountOtherChargesPersonRemark;

	private Double supplierAccountServiceCharge;

	private Double supplierAccountCompanyTotal;

	private Double supplierAccountPersonTotal;

	private Double supplierAccountTotal;

	private Byte isMatching;

	public BillDetail(Integer zaiceId, Double customerAccountPensionCompanyPay, Double customerAccountPensionPersonPay,
			Double customerAccountUnemploymentCompanyPay, Double customerAccountUnemploymentPersonPay,
			Double customerAccountInjuryCompanyPay, Double customerAccountProcreateCompanyPay,
			Double customerAccountMedicalTreatmentCompanyPay, Double customerAccountMedicalTreatmentPersonPay,
			Double customerAccountSeriousIllnessCompanyPay, Double customerAccountSeriousIllnessPersonPay,
			Double customerAccountAccumulationFundCompanyPay, Double customerAccountAccumulationFundPersonPay,
			Double customerAccountDisabilityBenefitPay, Double customerAccountOtherChargesCompanyPay,
			String customerAccountOtherChargesCompanyRemark, Double customerAccountOtherChargesPersonPay,
			String customerAccountOtherChargesPersonRemark, Double customerAccountServiceCharge,
			Double customerAccountCompanyTotal, Double customerAccountPersonTotal, Double customerAccountTotal,
			Double supplierAccountPensionCompanyPay, Double supplierAccountPensionPersonPay,
			Double supplierAccountUnemploymentCompanyPay, Double supplierAccountUnemploymentPersonPay,
			Double supplierAccountInjuryCompanyPay, Double supplierAccountProcreateCompanyPay,
			Double supplierAccountMedicalTreatmentCompanyPay, Double supplierAccountMedicalTreatmentPersonPay,
			Double supplierAccountSeriousIllnessCompanyPay, Double supplierAccountSeriousIllnessPersonPay,
			Double supplierAccumulationFundCompanyPay, Double supplierAccumulationFundPersonPay,
			Double supplierAccountDisabilityBenefitPay, Double supplierAccountOtherChargesCompanyPay,
			String supplierAccountOtherChargesCompanyRemark, Double supplierAccountOtherChargesPersonPay,
			String supplierAccountOtherChargesPersonRemark, Double supplierAccountServiceCharge,
			Double supplierAccountCompanyTotal, Double supplierAccountPersonTotal, Double supplierAccountTotal,
			Byte isMatching) {
		super();
		this.zaiceId = zaiceId;
		this.customerAccountPensionCompanyPay = customerAccountPensionCompanyPay;
		this.customerAccountPensionPersonPay = customerAccountPensionPersonPay;
		this.customerAccountUnemploymentCompanyPay = customerAccountUnemploymentCompanyPay;
		this.customerAccountUnemploymentPersonPay = customerAccountUnemploymentPersonPay;
		this.customerAccountInjuryCompanyPay = customerAccountInjuryCompanyPay;
		this.customerAccountProcreateCompanyPay = customerAccountProcreateCompanyPay;
		this.customerAccountMedicalTreatmentCompanyPay = customerAccountMedicalTreatmentCompanyPay;
		this.customerAccountMedicalTreatmentPersonPay = customerAccountMedicalTreatmentPersonPay;
		this.customerAccountSeriousIllnessCompanyPay = customerAccountSeriousIllnessCompanyPay;
		this.customerAccountSeriousIllnessPersonPay = customerAccountSeriousIllnessPersonPay;
		this.customerAccountAccumulationFundCompanyPay = customerAccountAccumulationFundCompanyPay;
		this.customerAccountAccumulationFundPersonPay = customerAccountAccumulationFundPersonPay;
		this.customerAccountDisabilityBenefitPay = customerAccountDisabilityBenefitPay;
		this.customerAccountOtherChargesCompanyPay = customerAccountOtherChargesCompanyPay;
		this.customerAccountOtherChargesCompanyRemark = customerAccountOtherChargesCompanyRemark;
		this.customerAccountOtherChargesPersonPay = customerAccountOtherChargesPersonPay;
		this.customerAccountOtherChargesPersonRemark = customerAccountOtherChargesPersonRemark;
		this.customerAccountServiceCharge = customerAccountServiceCharge;
		this.customerAccountCompanyTotal = customerAccountCompanyTotal;
		this.customerAccountPersonTotal = customerAccountPersonTotal;
		this.customerAccountTotal = customerAccountTotal;
		this.supplierAccountPensionCompanyPay = supplierAccountPensionCompanyPay;
		this.supplierAccountPensionPersonPay = supplierAccountPensionPersonPay;
		this.supplierAccountUnemploymentCompanyPay = supplierAccountUnemploymentCompanyPay;
		this.supplierAccountUnemploymentPersonPay = supplierAccountUnemploymentPersonPay;
		this.supplierAccountInjuryCompanyPay = supplierAccountInjuryCompanyPay;
		this.supplierAccountProcreateCompanyPay = supplierAccountProcreateCompanyPay;
		this.supplierAccountMedicalTreatmentCompanyPay = supplierAccountMedicalTreatmentCompanyPay;
		this.supplierAccountMedicalTreatmentPersonPay = supplierAccountMedicalTreatmentPersonPay;
		this.supplierAccountSeriousIllnessCompanyPay = supplierAccountSeriousIllnessCompanyPay;
		this.supplierAccountSeriousIllnessPersonPay = supplierAccountSeriousIllnessPersonPay;
		this.supplierAccumulationFundCompanyPay = supplierAccumulationFundCompanyPay;
		this.supplierAccumulationFundPersonPay = supplierAccumulationFundPersonPay;
		this.supplierAccountDisabilityBenefitPay = supplierAccountDisabilityBenefitPay;
		this.supplierAccountOtherChargesCompanyPay = supplierAccountOtherChargesCompanyPay;
		this.supplierAccountOtherChargesCompanyRemark = supplierAccountOtherChargesCompanyRemark;
		this.supplierAccountOtherChargesPersonPay = supplierAccountOtherChargesPersonPay;
		this.supplierAccountOtherChargesPersonRemark = supplierAccountOtherChargesPersonRemark;
		this.supplierAccountServiceCharge = supplierAccountServiceCharge;
		this.supplierAccountCompanyTotal = supplierAccountCompanyTotal;
		this.supplierAccountPersonTotal = supplierAccountPersonTotal;
		this.supplierAccountTotal = supplierAccountTotal;
		this.isMatching = isMatching;
	}

	public BillDetail(Integer bdId, Integer zaiceId, Double customerAccountPensionCompanyPay,
			Double customerAccountPensionPersonPay, Double customerAccountUnemploymentCompanyPay,
			Double customerAccountUnemploymentPersonPay, Double customerAccountInjuryCompanyPay,
			Double customerAccountProcreateCompanyPay, Double customerAccountMedicalTreatmentCompanyPay,
			Double customerAccountMedicalTreatmentPersonPay, Double customerAccountSeriousIllnessCompanyPay,
			Double customerAccountSeriousIllnessPersonPay, Double customerAccountAccumulationFundCompanyPay,
			Double customerAccountAccumulationFundPersonPay, Double customerAccountDisabilityBenefitPay,
			Double customerAccountOtherChargesCompanyPay, String customerAccountOtherChargesCompanyRemark,
			Double customerAccountOtherChargesPersonPay, String customerAccountOtherChargesPersonRemark,
			Double customerAccountServiceCharge, Double customerAccountCompanyTotal, Double customerAccountPersonTotal,
			Double customerAccountTotal, Double supplierAccountPensionCompanyPay,
			Double supplierAccountPensionPersonPay, Double supplierAccountUnemploymentCompanyPay,
			Double supplierAccountUnemploymentPersonPay, Double supplierAccountInjuryCompanyPay,
			Double supplierAccountProcreateCompanyPay, Double supplierAccountMedicalTreatmentCompanyPay,
			Double supplierAccountMedicalTreatmentPersonPay, Double supplierAccountSeriousIllnessCompanyPay,
			Double supplierAccountSeriousIllnessPersonPay, Double supplierAccumulationFundCompanyPay,
			Double supplierAccumulationFundPersonPay, Double supplierAccountDisabilityBenefitPay,
			Double supplierAccountOtherChargesCompanyPay, String supplierAccountOtherChargesCompanyRemark,
			Double supplierAccountOtherChargesPersonPay, String supplierAccountOtherChargesPersonRemark,
			Double supplierAccountServiceCharge, Double supplierAccountCompanyTotal, Double supplierAccountPersonTotal,
			Double supplierAccountTotal, Byte isMatching) {
		this.bdId = bdId;
		this.zaiceId = zaiceId;
		this.customerAccountPensionCompanyPay = customerAccountPensionCompanyPay;
		this.customerAccountPensionPersonPay = customerAccountPensionPersonPay;
		this.customerAccountUnemploymentCompanyPay = customerAccountUnemploymentCompanyPay;
		this.customerAccountUnemploymentPersonPay = customerAccountUnemploymentPersonPay;
		this.customerAccountInjuryCompanyPay = customerAccountInjuryCompanyPay;
		this.customerAccountProcreateCompanyPay = customerAccountProcreateCompanyPay;
		this.customerAccountMedicalTreatmentCompanyPay = customerAccountMedicalTreatmentCompanyPay;
		this.customerAccountMedicalTreatmentPersonPay = customerAccountMedicalTreatmentPersonPay;
		this.customerAccountSeriousIllnessCompanyPay = customerAccountSeriousIllnessCompanyPay;
		this.customerAccountSeriousIllnessPersonPay = customerAccountSeriousIllnessPersonPay;
		this.customerAccountAccumulationFundCompanyPay = customerAccountAccumulationFundCompanyPay;
		this.customerAccountAccumulationFundPersonPay = customerAccountAccumulationFundPersonPay;
		this.customerAccountDisabilityBenefitPay = customerAccountDisabilityBenefitPay;
		this.customerAccountOtherChargesCompanyPay = customerAccountOtherChargesCompanyPay;
		this.customerAccountOtherChargesCompanyRemark = customerAccountOtherChargesCompanyRemark;
		this.customerAccountOtherChargesPersonPay = customerAccountOtherChargesPersonPay;
		this.customerAccountOtherChargesPersonRemark = customerAccountOtherChargesPersonRemark;
		this.customerAccountServiceCharge = customerAccountServiceCharge;
		this.customerAccountCompanyTotal = customerAccountCompanyTotal;
		this.customerAccountPersonTotal = customerAccountPersonTotal;
		this.customerAccountTotal = customerAccountTotal;
		this.supplierAccountPensionCompanyPay = supplierAccountPensionCompanyPay;
		this.supplierAccountPensionPersonPay = supplierAccountPensionPersonPay;
		this.supplierAccountUnemploymentCompanyPay = supplierAccountUnemploymentCompanyPay;
		this.supplierAccountUnemploymentPersonPay = supplierAccountUnemploymentPersonPay;
		this.supplierAccountInjuryCompanyPay = supplierAccountInjuryCompanyPay;
		this.supplierAccountProcreateCompanyPay = supplierAccountProcreateCompanyPay;
		this.supplierAccountMedicalTreatmentCompanyPay = supplierAccountMedicalTreatmentCompanyPay;
		this.supplierAccountMedicalTreatmentPersonPay = supplierAccountMedicalTreatmentPersonPay;
		this.supplierAccountSeriousIllnessCompanyPay = supplierAccountSeriousIllnessCompanyPay;
		this.supplierAccountSeriousIllnessPersonPay = supplierAccountSeriousIllnessPersonPay;
		this.supplierAccumulationFundCompanyPay = supplierAccumulationFundCompanyPay;
		this.supplierAccumulationFundPersonPay = supplierAccumulationFundPersonPay;
		this.supplierAccountDisabilityBenefitPay = supplierAccountDisabilityBenefitPay;
		this.supplierAccountOtherChargesCompanyPay = supplierAccountOtherChargesCompanyPay;
		this.supplierAccountOtherChargesCompanyRemark = supplierAccountOtherChargesCompanyRemark;
		this.supplierAccountOtherChargesPersonPay = supplierAccountOtherChargesPersonPay;
		this.supplierAccountOtherChargesPersonRemark = supplierAccountOtherChargesPersonRemark;
		this.supplierAccountServiceCharge = supplierAccountServiceCharge;
		this.supplierAccountCompanyTotal = supplierAccountCompanyTotal;
		this.supplierAccountPersonTotal = supplierAccountPersonTotal;
		this.supplierAccountTotal = supplierAccountTotal;
		this.isMatching = isMatching;
	}
}