package com.dianmi.model.po;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:create by lzw
 * @Date:2017年11月30日 下午2:45:06
 * @Description:
 */
@Data
@NoArgsConstructor
public class ZaiceCustomerMsg {
	private int zaiceId;
	private int customerId;
	private String customerName;

	public ZaiceCustomerMsg(int zaiceId, int customerId, String customerName) {
		super();
		this.zaiceId = zaiceId;
		this.customerId = customerId;
		this.customerName = customerName;
	}

}