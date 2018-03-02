package com.dianmi.model.accumulation;

import lombok.Data;

@Data
public class ZHExportAccumulation {
	private String name;
	private String certificateNum;
	private Double payRadix;
	private Double payRatio;
	private Double unitMonthFee;
	private Double personMonthFee;
	private Double totalFee;
	private String startSalaryMonth;
	private String startPayMonth;
	private String accumulationAccount;
	private String company;
	private String managerName;
}
