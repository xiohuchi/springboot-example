package com.dianmi.model.owndetailshow;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//供应商页面详情
@Data
@NoArgsConstructor
public class SupplierParticularsShow  implements Serializable{
	private String name;
	private String certificateNumber;
	private Double personSocialRedix;
	
	private Double pensionSocial;
	private Double unemploymentSocial;
	private Double injurySocial;
	private Double procreateSocial;
	private Double medicaltreatmentSocial;
	
	private Double accumulationRedix;
	private Double disabilityBenefitFee;
	private Double companyAccumulation;
	private Double personAccumulation;
	private Double accumulationTotal;
	
	private Double serviceFee;
	private Double TotalFee;

	
	
}
