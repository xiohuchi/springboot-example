package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KefuDept {
    private Integer kdId;

    private String deptName;

	public KefuDept(Integer kdId, String deptName) {
		super();
		this.kdId = kdId;
		this.deptName = deptName;
	}
}