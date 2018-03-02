package com.dianmi.model.accumulation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DGAccumulation {
    private String name;
    private String certificateType;
    private String certificateNum;
    private Double payRadix;
    private Double oldPayRadix;
    private Double oldPayRatio;
    private Double personRatio;
    private Double companyRatio;
    private Double companyFee;
    private Double personFee;
    private Double totalFee;
    private String personNumber;
    private String customer;
    private String managerName;
}
