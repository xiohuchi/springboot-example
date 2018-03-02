package com.dianmi.model.owndetailshow;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//深圳详情展示
@Data
@NoArgsConstructor
public class SZDetailShow  implements Serializable{
	private String name;
	private String certificateNumber;	
	private Double personTotal=0.00;        //默认0.00，个人部分合计	
	private Double companyTotal=0.00;        //默认0.00，公司部分合计
	
	
	private Double pensionPersonTotal=0.00;        //默认0.00，养老个人总费
	private Double pensionCompanyTotal=0.00;        //默认0.00，养老公司总费
	private Double pensionRadix=0.00;        //默认0.00，养老缴纳基数
	
	private Double unemploymentPersonTotal=0.00;        //默认0.00，失业保险个人总费
	private Double unemploymentCompanyTotal=0.00;        //默认0.00，失业保险公司总费
	private Double unemploymentRadix=0.00;        //默认0.00，失业保险缴纳基数
	
	private Double injuryPersonTotal=0.00;        //工伤个人总费
	private Double injuryCompanyTotal=0.00;        //默认0.00，工伤企业总费
	private Double injuryCompanyRadix=0.00;        //默认0.00，工伤保险企业缴费基数
	
	private Double procreatePersonTotal=0.00;        //默认0.00，生育个人总费
	private Double procreateCompanyTotal=0.00;        //默认0.00，生育企业总费
	private Double procreateCompanyRadix=0.00;        //默认0.00，生育保险企业缴费基数
	
	private Double medicalTreatmentPersonTotal=0.00;        //默认0.00，医疗个人总费
	private Double medicalTreatmentCompanyTotal=0.00;        //默认0.00，医疗公司总费
	private Double medicalTreatmentRadix=0.00;        //默认0.00，医疗缴费基数
	
	private Double seriousIllnessPersonTotal=0.00;        //默认0.00，大病医疗个人总费
	private Double seriousIllnessCompanyTotal=0.00;        //默认0.00，大病医疗公司总费
	private Double seriousIllnessRadix=0.00;        //默认0.00，大病医疗缴费基数
	
	private Double accumulationRadix=0.00;        //默认0.00，公积金缴费基数
	private Double accumulationCompanyTotal=0.00;        //默认0.00，公积金公司总费
	private Double accumulationPersonTotal=0.00;        //默认0.00，公积金个人总费
	private Double accumulationTotalFee=0.00;        //默认0.00，公积金总费
	
	private Double total=0.00;        //默认0.00，合计
	private Double disabilityBenefit=0.00;        //默认0.00，伤残金
	private Double serviceFee=0.00;        //默认0.00，服务费

}
