package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.SupplierCost;

public interface SupplierCostMapper {
    int deleteByPrimaryKey(Integer scId);

    int insert(SupplierCost record);

    int insertSelective(SupplierCost record);

    SupplierCost selectByPrimaryKey(Integer scId);

    int updateByPrimaryKeySelective(SupplierCost record);

    int updateByPrimaryKey(SupplierCost record);

    List<SupplierCost> selectSupplierCostAll(@Param("supplierId") Integer supplierId,
                                             @Param("reportingPeriod") String yearMonth,
                                             @Param("pages") Integer currPage,
                                             @Param("pageSize") Integer pageSize);

    String selectCity(@Param("supplierId") Integer supplierId,
                      @Param("reportingPeriod") String yearMonth);

    String selectEmployeeName(@Param("city") String city,
                              @Param("supplierId") Integer supplierId,
                              @Param("reportingPeriod") String month,
                              @Param("certificateNumber") String certificateNumber);

    String selectSupplierName(@Param("supplierId") Integer supplierId,
                              @Param("reportingPeriod") String yearMonth);

    int deleteSupplierCostDetails(@Param("supplierId") Integer supplierId,
                                  @Param("reportingPeriod") String yearMonth);

    List<SupplierCost> selectSupplierCostAllBySupplierId(@Param("supplierId") Integer supplierId,
                                                         @Param("reportingPeriod") String yearMonth);

    List<SupplierCost> customerIdBySupplierCost(@Param("customerId") Integer customerId,
                                                @Param("supplierId") Integer supplierId,
                                                @Param("yearMonth") String yearMonth,
                                                @Param("userId") Integer userId,
                                                @Param("roleType") Byte roleType);

    List<SupplierCost> selectCostBySupplier(@Param("yearMonth") String yearMonth, @Param("supplierId") Integer supplierId);

    List<SupplierCost> zhangdanMingxi(@Param("supplierId") Integer supplierId,
                                      @Param("yearMonth") String yearMonth,
                                      @Param("userId") Integer userId,
                                      @Param("roleType") Byte roleType);


    List<SupplierCost> supplierCostPageList(@Param("yearMonth") String yearMonth,
                                            @Param("paramName") String paramName);

    Map<String,String> supplierCostStatistics(@Param("yearMonth") String yearMonth,
                                              @Param("userId") Integer uId,
                                              @Param("roleType") Byte roleType);
}