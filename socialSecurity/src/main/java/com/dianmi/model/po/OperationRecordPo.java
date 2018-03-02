package com.dianmi.model.po;

import lombok.Data;

import java.util.Date;

@Data
public class OperationRecordPo {
    private Integer orId;

    private String deptName;

    private String reportingPeriod;

    private Date operationTime;

    private Byte status;

    private String operationDescription;
}