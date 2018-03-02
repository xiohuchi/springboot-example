package com.dianmi.model.accumulation;

import lombok.Data;

@Data
public class GZExportAccumulation {
	private String name;
	private String certificateNum;
	private Double payRadix;
	private Double unitRatio;
	private Double unitMonthFee;
	private Double personRatio;
	private Double personMonthFee;
	private Double totalFee;
	private String personNumber;
	private String company;
	private String managerName;
	
}
