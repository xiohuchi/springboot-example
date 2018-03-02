package com.dianmi.model.po;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FenzuZhangdanPo {
	private String reportingPeriod;
	private Integer supplierId;
	private String supplierName;
	private Integer customerId;
	private String customerName;
	private Double yuqiShebaoGongsiHeji;
	private Double yuqiShebaoGerenHeji;
	private Double yuqiShebaoHeji;
	private Double yuqiCanbaojinGongsi;
	private Double yuqiGongjijinGongsi;
	private Double yuqiGongjijinGeren;
	private Double yuqiGongjijinHeji;
	private Double yuqiFuwufei;
	private Double yuqiHeji;
	private Double shijiShebaoGongsiHeji;
	private Double shijiShebaoGerenHeji;
	private Double shijiShebaoHeji;
	private Double shijiCanbaojinGongsi;
	private Double shijiGongjijinGongsi;
	private Double shijiGongjijinGeren;
	private Double shijiGongjijinHeji;
	private Double shijiFuwufei;
	private Double shijiHeji;

}