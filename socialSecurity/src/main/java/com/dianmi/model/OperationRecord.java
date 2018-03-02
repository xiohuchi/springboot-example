package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class OperationRecord {
	private Integer orId;

	private Integer userId;

	private Integer deptId;

	private String reportingPeriod;

	private Date operationTime;

	private Byte status;

	private String operationDescription;

	public OperationRecord(Integer userId, Integer deptId, String reportingPeriod, Date operationTime, Byte status,
			String operationDescription) {
		this.userId = userId;
		this.deptId = deptId;
		this.reportingPeriod = reportingPeriod;
		this.operationTime = operationTime;
		this.status = status;
		this.operationDescription = operationDescription;
	}

	public OperationRecord(Integer orId, Integer userId, Integer deptId, String reportingPeriod, Date operationTime,
			Byte status, String operationDescription) {
		this.orId = orId;
		this.userId = userId;
		this.deptId = deptId;
		this.reportingPeriod = reportingPeriod;
		this.operationTime = operationTime;
		this.status = status;
		this.operationDescription = operationDescription;
	}


}