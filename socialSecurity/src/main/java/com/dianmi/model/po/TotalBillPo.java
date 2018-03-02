package com.dianmi.model.po;

import lombok.Data;

@Data
public class TotalBillPo {
	private Integer supplierId;
	private Integer customerId;
	private String supplierName;
	private String customerName;
	private String reportingPeriod;
	private String dept;
	private Double gongsiShebaoTotal;
	private Double gerenShebaoTotal;
	private Double shebaoTotal;
	private Double canbaojinTotal;
	private Double gongsiGongjijinTotal;
	private Double gerenGongjijinTotal;
	private Double gongjijinTotal;
	private Double fuwufeiTotal;
	private Double accountTotal;

}
