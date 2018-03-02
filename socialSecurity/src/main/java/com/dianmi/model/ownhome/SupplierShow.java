package com.dianmi.model.ownhome;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//供应商
@Data
@NoArgsConstructor
public class SupplierShow implements Serializable{
	private String reportingPeriod;
	//private Boolean isTrue;		//是否回款
	private String customerName;
	
	private Double companySocialTotal;
	private Double personSocialTotal;
	private Double socialTotalPay;
	
	private Double disabilityBenefitFee;	
	private Double companyAccumulation;
	private Double personAccumulation;
	private Double accumulationTotalPay;
	
	private Double serviceFee;
	private Double TotalFee;
	

}
