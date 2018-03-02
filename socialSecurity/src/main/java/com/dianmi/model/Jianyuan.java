package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Jianyuan {
	private Integer jyId;

	private Integer userId;

	private Integer deptId;

	private Integer customerId;

	private String clientName;

	private String employeeName;

	private String certificateType;

	private String certificateNumber;

	private String city;

	private String dimissionReason;

	private String dimissionDate;

	private String socialSecurityStopDate;

	private String accumulationFundStopDate;

	private String supplier;

	private String remark;

	private Date importDate;

	private String reportingPeriod;

	private Byte status;

	private Date sendEmailDate;//邮件发送时间
	
	private Byte isExport;

	public Jianyuan(Integer userId, Integer deptId, Integer customerId, String clientName, String employeeName,
			String certificateType, String certificateNumber, String city, String dimissionReason, String dimissionDate,
			String socialSecurityStopDate, String accumulationFundStopDate, String supplier, String remark,
			Date importDate, String reportingPeriod, Byte status) {
		this.userId = userId;
		this.deptId = deptId;
		this.customerId = customerId;
		this.clientName = clientName;
		this.employeeName = employeeName;
		this.certificateType = certificateType;
		this.certificateNumber = certificateNumber;
		this.city = city;
		this.dimissionReason = dimissionReason;
		this.dimissionDate = dimissionDate;
		this.socialSecurityStopDate = socialSecurityStopDate;
		this.accumulationFundStopDate = accumulationFundStopDate;
		this.supplier = supplier;
		this.remark = remark;
		this.importDate = importDate;
		this.reportingPeriod = reportingPeriod;
		this.status = status;
	}

	public Jianyuan(Integer jyId, Integer userId, Integer deptId, Integer customerId, String clientName,
			String employeeName, String certificateType, String certificateNumber, String city, String dimissionReason,
			String dimissionDate, String socialSecurityStopDate, String accumulationFundStopDate, String supplier,
			String remark, Date importDate, String reportingPeriod, Byte status, Date sendEmailDate, Byte isExport) {
		super();
		this.jyId = jyId;
		this.userId = userId;
		this.deptId = deptId;
		this.customerId = customerId;
		this.clientName = clientName;
		this.employeeName = employeeName;
		this.certificateType = certificateType;
		this.certificateNumber = certificateNumber;
		this.city = city;
		this.dimissionReason = dimissionReason;
		this.dimissionDate = dimissionDate;
		this.socialSecurityStopDate = socialSecurityStopDate;
		this.accumulationFundStopDate = accumulationFundStopDate;
		this.supplier = supplier;
		this.remark = remark;
		this.importDate = importDate;
		this.reportingPeriod = reportingPeriod;
		this.status = status;
		this.sendEmailDate = sendEmailDate;
		this.isExport = isExport;
	}

}