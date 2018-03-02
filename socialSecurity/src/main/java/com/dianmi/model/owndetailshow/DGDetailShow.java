package com.dianmi.model.owndetailshow;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class DGDetailShow implements Serializable{
	private String name;    //客户姓名												3
    private String certificateNumber;  //客户身份证号
    
    private Double pensionRadix;     //默认0.00，养老缴纳基数
    private Double pensionCompanyPay;       //默认0.00，养老公司缴费
    private Double pensionPersonPay;     //默认0.00，养老个人缴费						7
    private Double pensionCompanySupplementPay;     //默认0.00，养老公司补缴			8
    private Double pensionCompanyPersonPay;     //养老个人补缴						9
    private Double pensionSupplementInterest;     //默认0.00，养老补缴利息			10
    private Double pensionLateFees;     //默认0.00，养老滞纳金
	private Double pensionSocialTotal;
	
	private Double unemploymentRadix;     //默认0.00，失业缴纳基数
    private Double unemploymentCompanyPay;     //默认0.00，失业公司缴费
    private Double unemploymentPersonPay;     //默认0.00，失业个人缴费
    private Double unemploymentCompanySupplementPay;     //默认0.00，失业公司补缴
    private Double unemploymentCompanyPersonPay;     //失业个人补缴
    private Double unemploymentSupplementInterest;     //默认0.00，失业补缴利息
    private Double unemploymentLateFees;     //默认0.00，失业滞纳金
    private Double unemploymentSocialTotal;
    
    private Double injuryRadix;     //默认0.00，工伤缴纳基数
    private Double injuryCompanyPay;     //默认0.00，工伤公司缴费
    private Double injuryPersonPay;     //默认0.00，工伤个人缴费
    private Double injuryCompanySupplementPay;     //默认0.00，工伤公司补缴
    private Double injuryCompanyPersonPay;     //工伤个人补缴
    private Double injurySupplementInterest;     //默认0.00，工伤补缴利息
    private Double injuryLateFees;     //默认0.00，工伤滞纳金
    private Double injurySocialTotal;
    
    private Double procreateRadix;     //默认0.00，生育缴纳基数
    private Double procreateCompanyPay;     //默认0.00，生育公司缴费
    private Double procreatePersonPay;     //默认0.00，生育个人缴费
    private Double procreateCompanySupplementPay;     //默认0.00，生育公司补缴
    private Double procreateCompanyPersonPay;     //生育个人补缴
    private Double procreateSupplementInterest;     //默认0.00，生育补缴利息
    private Double procreateLateFees;     //默认0.00，生育滞纳金
    private Double procreateSocialTotal;
    
    private Double seriousIllnessTreatmentRadix;     //默认0.00，大病医疗缴纳基数
    private Double seriousIllnessTreatmentCompanyPay;     //默认0.00，大病医疗公司缴费
    private Double seriousIllnessTreatmentPersonPay;     //默认0.00，大病医疗个人缴费
    private Double seriousIllnessTreatmentCompanySupplementPay;     //默认0.00，大病医疗公司补缴
    private Double seriousIllnessTreatmentCompanyPersonPay;     //大病医疗个人补缴
    private Double seriousIllnessTreatmentSupplementInterest;     //默认0.00，大病医疗补缴利息
    private Double seriousIllnessTreatmentLateFees;     //默认0.00，大病医疗滞纳金
    private Double seriousIllnessSocialTotal;
    
    private Double medicalTreatmentRadix;     //默认0.00，医疗缴纳基数
    private Double medicalTreatmentCompanyPay;     //默认0.00，医疗公司缴费
    private Double medicalTreatmentPersonPay;     //默认0.00，医疗个人缴费
    private Double medicalTreatmentCompanySupplementPay;     //默认0.00，医疗公司补缴
    private Double medicalTreatmentCompanyPersonPay;     //医疗个人补缴
    private Double medicalTreatmentSupplementInterest;     //默认0.00，医疗补缴利息
    private Double medicalTreatmentLateFees;     //默认0.00，医疗滞纳金
    private Double medicalTreatmentSocialTotal;
    
    private Double accumulationRadix;
    private Double accumulationUnit;
    private Double accumulationPerson;
    private Double accumulationTotalFee;
    private Double disabilityBenefit;        //默认0.00，伤残金
	private Double serviceFee;        //默认0.00，服务费
    
    private Double companyTotal;     //默认0.00，公司部分合计
    private Double personTotal;     //默认0.00，个人部分合计
    private Double total;     //默认0.00，合计

	
	
	
}
