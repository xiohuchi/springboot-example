package com.dianmi.model.accumulation;

import lombok.Data;

@Data
public class SZAccumulation {
	private String accumulationNum;
	private String name;
	private String certificateNum;
	private Double payRadix;
	private Double unitPayRatio;
	private Double personPayRatio;
	private Double payMoney;
	private String payMonth;
	private String customerName;
}
