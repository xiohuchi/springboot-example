package com.dianmi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.util.Date;

/**
 * 请款/垫付PO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Qingkuan {

    private Integer id;
    private Integer supplierId; //供应商ID,
    private String supplierName; //供应商名称,
    private Integer userId; //操作人,
    private Byte type; //0:请款,1:垫付,
    private Byte state; //0:待处理,1:已申请垫付,2:已请款
    private String reportingPeriod; //月份,
    private String customerName; //客户名称,
    private Integer customerId; //客户Id,
    private Double mountTotal; //合计,
    private Double gerenshebao; //个人社保,
    private Double gongsishebao; //公司社保,
    private Double shebaoTotal; //社保合计,
    private Double gongsicanbaojin; //公司残保金,
    private Double gerengongjijin; //个人公积金,
    private Double gongsigongjijin; //公司公积金,
    private Double gongjijinTotal; //公积金,
    private Double fuwufeiTotal; //服务费,
    private String remark; //备注,
    private Date paymentDate; //款项回收日期
    private Date createDate; //创建时间

    private String countEmp;
    private String deptName;

}
