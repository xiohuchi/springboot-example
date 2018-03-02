package com.dianmi.model.ownhome;

import lombok.Data;
import lombok.NoArgsConstructor;

//东莞自有户工具类
@Data
@NoArgsConstructor
public class DGPerson {
	private Integer userId;
	private Integer customerId;
	private Integer supplierId;
	private String customerName;
	private String certificateNumber; // 客户身份证号
	private String name; // 客户姓名
	private String reportingPeriod; // 所属月份
	private String securityType; // 参保类型
	private Double payRadix = 0.00; // 默认0.00，缴纳基数
	private Double companyPay = 0.00; // 默认0.00，公司缴费
	private Double personPay = 0.00; // 默认0.00，个人缴费
	private Double companySupplementPay = 0.00; // 默认0.00，公司补缴
	private Double pensonSupplementPay = 0.00; // 个人补缴
	private Double supplementInterest = 0.00; // 默认0.00，补缴利息
	private Double payLateFees = 0.00; // 默认0.00，滞纳金
	private Double total = 0.00; // 默认0.00，合计

	public DGPerson(Integer userId, Integer customerId, Integer supplierId,String customerName, String certificateNumber, String name,
			String reportingPeriod, String securityType, Double payRadix, Double companyPay, Double personPay,
			Double companySupplementPay, Double pensonSupplementPay, Double supplementInterest, Double payLateFees,
			Double total) {
		super();
		this.userId = userId;
		this.customerId = customerId;
		this.supplierId = supplierId;
		this.customerName = customerName;
		this.certificateNumber = certificateNumber;
		this.name = name;
		this.reportingPeriod = reportingPeriod;
		this.securityType = securityType;
		this.payRadix = payRadix;
		this.companyPay = companyPay;
		this.personPay = personPay;
		this.companySupplementPay = companySupplementPay;
		this.pensonSupplementPay = pensonSupplementPay;
		this.supplementInterest = supplementInterest;
		this.payLateFees = payLateFees;
		this.total = total;
	}

}