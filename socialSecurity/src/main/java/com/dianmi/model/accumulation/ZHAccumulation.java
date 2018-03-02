package com.dianmi.model.accumulation;

import lombok.Data;

@Data
public class ZHAccumulation {
	private String personNumber;
	private String name;
	private String sex;
	private String certificateType;
	private String certificatNum;
	private String payType;
	private Double salaryRadix;
	private String accountStatus;
	private String openAccount;
	private Double monthFee;
	private Double unitMonthFee;
	private Double personMonthFee;
}
