package com.dianmi.model.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class JianyuanPo {
    private Integer jyId;

    private String customerTag;
    
    private Integer supplierId;

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
}