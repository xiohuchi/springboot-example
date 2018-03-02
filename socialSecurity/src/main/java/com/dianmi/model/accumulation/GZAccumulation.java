package com.dianmi.model.accumulation;

import lombok.Data;

@Data
public class GZAccumulation {
	private String unitNumber;
	private String unitName;
	private String fundsSource;		//资金来源,	非财政统发
	private String personNum;
	private String personAccumulationNum;
	private String name;
	private String certificateType;
	private String certificateNum;
	private Double personPayRadix;
	private Double personFee;
	private Double unitFee;
	private Double accumulationBalance;
	private String payStatus;
	
}
