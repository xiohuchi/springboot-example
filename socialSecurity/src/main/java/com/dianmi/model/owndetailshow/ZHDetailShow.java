package com.dianmi.model.owndetailshow;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ZHDetailShow  implements Serializable{
	private String name;        //客户姓名
    private String certificateNumber;       //客户身份证号
    private String socialSecurityNumber;        //社保/医保号
    
    private Double pensionPersonPay;        //默认0.00，养老个人缴费
    private Double pensionCompanyPay;        //默认0.00，养老公司缴费
    private Double pensionRadix;        //默认0.00，养老缴纳基数
    
    private Double cityUnemploymentPersonPay=0.00;        //默认0.00，失业保险个人缴费
    private Double cityUnemploymentCompanyPay=0.00;       //默认0.00，失业保险公司缴费
    private Double cityUnemploymentRadix=0.00;       //默认0.00，失业保险缴纳基数
    
    private Double farmersUnemploymentPersonPay=0.00;       //默认0.00，失业保险个人缴费
    private Double farmersUnemploymentCompanyPay=0.00;       //默认0.00，失业保险公司缴费
    private Double farmersUnemploymentRadix=0.00;       //默认0.00，失业保险缴纳基数
    
    private Double injuryPersonPay;        //工伤个人缴费
    private Double injuryCompanyPay;        //默认0.00，工伤企业缴费
    private Double injuryCompanyRadix;        //默认0.00，工伤保险企业缴费基数
    
    private Double procreatePersonPay;        //默认0.00，生育个人缴费
    private Double procreateCompanyPay;        //默认0.00，生育企业缴费
    private Double procreateCompanyRadix;        //默认0.00，生育保险企业缴费基数
    
    private Double medicalTreatmentPersonPay;        //默认0.00，医疗个人缴费
    private Double medicalTreatmentCompanyPay;        //默认0.00，医疗公司缴费
    private Double medicalTreatmentRadix;        //默认0.00，医疗缴费基数
    
    private Double seriousIllnessPersonPay;        //默认0.00，大病个人缴费
    private Double seriousIllnessCompanyPay;        //默认0.00，大病企业缴费
    private Double seriousIllnessRedix;        //大病缴费基数
    
    private Double accumulationRadix;        //默认0.00，公积金缴费基数
	private Double accumulationCompanyTotal;        //默认0.00，公积金公司总费
	private Double accumulationPersonTotal;        //默认0.00，公积金个人总费
	private Double accumulationTotalFee;        //默认0.00，公积金总费
	private Double disabilityBenefit;        //默认0.00，伤残金
	private Double serviceFee;        //默认0.00，服务费
	private Double companyTotal;        //默认0.00，公司部分合计
    private Double personTotal;        //默认0.00，个人部分合计
    private Double total;        //默认0.00，合计
    
    
}
