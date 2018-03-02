package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Caozuoyichang {
    private Integer czId;

    private Integer userId;

    private String city;

    private Integer customerId;

    private Date createDate;

    private String name;

    private String certificateNumber;

    private String reportingPeriod;

    private String reason;

    public Caozuoyichang(Integer userId, String city, Integer customerId, Date createDate, String name, String
            certificateNumber, String reportingPeriod, String reason) {
        this.userId = userId;
        this.city = city;
        this.customerId = customerId;
        this.createDate = createDate;
        this.name = name;
        this.certificateNumber = certificateNumber;
        this.reportingPeriod = reportingPeriod;
        this.reason = reason;
    }

    public Caozuoyichang(Integer czId, Integer userId, String city, Integer customerId, Date createDate, String name, String certificateNumber, String reportingPeriod, String reason) {
        this.czId = czId;
        this.userId = userId;
        this.city = city;
        this.customerId = customerId;
        this.createDate = createDate;
        this.name = name;
        this.certificateNumber = certificateNumber;
        this.reportingPeriod = reportingPeriod;
        this.reason = reason;
    }
}