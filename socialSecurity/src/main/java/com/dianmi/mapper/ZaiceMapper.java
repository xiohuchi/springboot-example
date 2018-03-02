package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import com.dianmi.model.owndetailshow.ZaiceDataShow;
import org.apache.ibatis.annotations.Param;

import com.dianmi.model.Zaice;
import com.dianmi.model.po.ZaicePo;

public interface ZaiceMapper {
    int deleteByPrimaryKey(Integer zcId);

    int insert(Zaice record);

    int insertSelective(Zaice record);

    Zaice selectByPrimaryKey(Integer zcId);

    int updateByPrimaryKeySelective(Zaice record);

    int updateByPrimaryKey(Zaice record);

    List<Zaice> selectBySocialMsg(@Param("userId") int userId, @Param("operationRecordId") int operationRecordId);

    List<ZaicePo> zaicePaging(@Param("type") int type,
                              @Param("reportingPeriod") String reportingPeriod,
                              @Param("userId") Integer userId,
                              @Param("paramName") String paramName,
                              @Param("supplierId") Integer supplierId,
                              @Param("customerId") Integer customerId,
                              @Param("cityName") String cityName);

    List<Zaice> selectByEmployeeMsg(@Param("city") String city, @Param("reportingPeriod") String reportingPeriod,
                                    @Param("certificateNumber") String certificateNumber);

    int updateDeleteFlag(@Param("zcId") Integer zcId);

    // 根据在册ID修改status
    void updateStatusByZaiceId(@Param("zaiceId") Integer zaiceId, @Param("status") Byte isMatchs);

    Map<String, Object> selectZaiceMsg(@Param("yearMonth") String yearMonth, @Param("city") String city,
                                       @Param("certificateNumber") String certificateNumber);

    String selectSupplierBySupplierId(@Param("city") String city, @Param("supplierId") Integer supplierId);

    String selectHouseType(@Param("city") String cITY, @Param("reportingPeriod") String yearMonth,
                           @Param("certificateNumber") String certificateNumber);

    Integer getSupplierIdBySupplier(@Param("city") String city, @Param("supplier") String supplier,
                                    @Param("certificateNumber") String certificateNumber);

    String findAccountingUnit(@Param("city") String city, @Param("supplierId") Integer supplierId,
                              @Param("reportingPeriod") String yearMonth, @Param("userId") Integer userId);

    void updateAddSocialStatus(@Param("yearMonth") String yearMonth, @Param("city") String city,
                               @Param("certificateNumber") String certificateNumber);

    List<Zaice> getMubiaoZaice(String reportingPeriod);

    List<Map<String, Object>> selectCustomerId(@Param("yearMonth") String yearMonth, @Param("city") String city,
                                               @Param("certificateNumber") String certificateNumber);

    Zaice findAccumulationInfos(@Param("city") String city, @Param("reportingPeriod") String yearMonth,
                                @Param("certificateNumber") String certificateNumber);

    String findCertificateType(@Param("employeeName") String name, @Param("certificateNumber") String certificateNum,
                               @Param("supplierId") Integer supplierId);

    String findAccumulationFundBeginTime(@Param("city") String city, @Param("supplierId") Integer supplierId,
                                         @Param("reportingPeriod") String yearMonth, @Param("certificateNumber") String certificateNumber);

    List<Integer> isZaiceExists(@Param("yearMonth") String yearMonth, @Param("city") String city,
                                @Param("certificateNumber") String certificateNumber);

    void updateDeleteFlagStatus(@Param("deleteFlag") int deleteFlag, @Param("zcId") int zcId);

    List<Map<String, Object>> selectAllSupplier(@Param("yearMonth") String yearMonth, @Param("userId") int userId,
                                                @Param("roleType") int roleType);

    /**
     * @param yearMonth
     * @param userId
     * @param roleType
     * @return 当月目标在册总人数
     */
    int realityZaiceAmount(@Param("userId") Integer userId, @Param("roleType") Integer roleType);

    /**
     * @param yearMonth
     * @param supplierId
     * @return
     */
    List<Zaice> getZengyuanBySupplierId(@Param("yearMonth") String yearMonth, @Param("supplierId") int supplierId);

    /**
     * @param yearMonth
     * @param supplierId
     * @return
     */
    List<Zaice> getJianyuanBySupplierId(@Param("yearMonth") String yearMonth, @Param("supplierId") int supplierId);

    //--------------- dyx


    List<Map<String, String>> supplierSpinner(@Param("paramName") String paramName,
                                              @Param("userId") Integer userId,
                                              @Param("roleType") Byte roleType);

    List<Map<String, String>> citySpinner(@Param("paramName") String paramName,
                                          @Param("userId") Integer userId,
                                          @Param("roleType") Byte roleType);

    /**
     * 查询社保在册
     *
     * @param customerName 客户名称
     * @param yearMonth    月份
     * @param status       增减员状态
     * @param zaiceflay    在侧状态
     * @param userId
     * @param roleType
     * @return
     */
    List<ZaiceDataShow> baseZaicePageList(@Param("customerName") String customerName,
                                          @Param("yearMonth") String yearMonth,
                                          @Param("status") Integer status,
                                          @Param("zaiceflay") Integer zaiceflay,
                                          @Param("userId") Integer userId,
                                          @Param("roleType") Byte roleType);

    Map<String,String> zaiceStatistics(@Param("yearMonth") String yearMonth,
                                       @Param("userId") Integer userId,
                                       @Param("roleType") Byte roleType);


    //--------------- dyx

}