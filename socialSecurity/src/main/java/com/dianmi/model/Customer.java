package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Customer {
	private Integer cId;

	private Integer deptId;

	private String customerName;

	private String mobilePhone;

	private String email;

	private Byte isAdvance;

	private Double advanceValue;

	private Byte isDisable;

	public Customer(Integer deptId, String customerName, String mobilePhone, String email, Byte isAdvance,
			Double advanceValue, Byte isDisable) {
		super();
		this.deptId = deptId;
		this.customerName = customerName;
		this.mobilePhone = mobilePhone;
		this.email = email;
		this.isAdvance = isAdvance;
		this.advanceValue = advanceValue;
		this.isDisable = isDisable;
	}

	public Customer(Integer cId, Integer deptId, String customerName, String mobilePhone, String email, Byte isAdvance,
			Double advanceValue, Byte isDisable) {
		this.cId = cId;
		this.deptId = deptId;
		this.customerName = customerName;
		this.mobilePhone = mobilePhone;
		this.email = email;
		this.isAdvance = isAdvance;
		this.advanceValue = advanceValue;
		this.isDisable = isDisable;
	}

}