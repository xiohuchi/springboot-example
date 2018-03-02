package com.dianmi.mapper;

import com.dianmi.model.Qingkuan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QingkuanMapper {

    List<Qingkuan> dianfuPageList(@Param("yearMonth") String yearMonth,
                                  @Param("customerName") String customerName,
                                  @Param("userId") Integer userId,
                                  @Param("roleType") Byte roleType);

    int updateDianfuState(@Param("customerId") Integer customerId,
                          @Param("supplierId") Integer supplierId);

    List<Qingkuan> qingkuanPageList(@Param("yearMonth") String yearMonth,
                                    @Param("supplierName") String supplierName,
                                    @Param("userId") Integer userId,
                                    @Param("roleType") Byte roleType);

//    int updateqingkuanState(@Param("customerId") Integer customerId,
//                            @Param("supplierId") Integer supplierId,
//                            @Param("yearMonth") String yearMonth);


    List<Qingkuan> zhangDanQingKuanDan(@Param("supplierId") Integer supplierId,
                                       @Param("yearMonth") String yearMonth);

    List<Qingkuan> zhangDanDianfuDan(@Param("supplierId") Integer supplierId,
                                     @Param("yearMonth") String yearMonth);
}
