package com.dianmi.model.ownhome;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//珠海
@Data
@NoArgsConstructor
public class ZHShow  implements Serializable{
	private String reportingPeriod;
	private String customerName;
	private String dept;
	private Double pensionSocial=0.00;
	private Double injurySocial=0.00;
	private Double peasantryUnemploymentSocial=0.00;
	private Double cityUnemploymentSocial=0.00;
	private Double medicaltreatmentSocial=0.00;
	private Double seriousIllnessSocial=0.00;
	private Double procreateSocial=0.00;
	private Double personSocialTotal=0.00;
	private Double companySocialTotal=0.00;
	private Double socialTotalPay=0.00;
	private Double disabilityBenefitFee=0.00;
	private Double companyAccumulation=0.00;
	private Double personAccumulation=0.00;
	private Double accumulationTotalPay=0.00;
	private Double serviceFee=0.00;
	private Double TotalFee=0.00;

}
