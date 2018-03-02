package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class JianyuanYichang {
    private Integer ycId;

    private Integer userId;
    
    private Integer deptId;

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

    private String checkCode;

    private String yichangReason;

    public JianyuanYichang(Integer userId, Integer deptId,String clientName, String employeeName, String certificateType,
			String certificateNumber, String city, String dimissionReason, String dimissionDate,
			String socialSecurityStopDate, String accumulationFundStopDate, String supplier, String remark,
			Date importDate, String reportingPeriod, String checkCode, String yichangReason) {
		super();
		this.userId = userId;
		this.deptId = deptId;
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
		this.checkCode = checkCode;
		this.yichangReason = yichangReason;
	}

	public JianyuanYichang(Integer ycId, Integer userId,Integer deptId, String clientName, String employeeName, String certificateType, String certificateNumber, String city, String dimissionReason, String dimissionDate, String socialSecurityStopDate, String accumulationFundStopDate, String supplier, String remark, Date importDate, String reportingPeriod, String checkCode, String yichangReason) {
        this.ycId = ycId;
        this.userId = userId;
        this.deptId = deptId;
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
        this.checkCode = checkCode;
        this.yichangReason = yichangReason;
    }
}