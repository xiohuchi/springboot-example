package com.dianmi.mapper;

import com.dianmi.model.Qingkuan;
import com.dianmi.model.ZhangdanMingxi;
import com.dianmi.model.po.ZhangDanMingxiPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ZhangdanMingxiMapper {
    int deleteByPrimaryKey(Integer zdId);

    int insert(ZhangdanMingxi record);

    int insertSelective(ZhangdanMingxi record);

    ZhangdanMingxi selectByPrimaryKey(Integer zdId);

    int updateByPrimaryKeySelective(ZhangdanMingxi record);

    int updateByPrimaryKey(ZhangdanMingxi record);

    List<ZhangDanMingxiPo> supplierZhangdanMingxi(@Param("yearMonth") String yearMonth,
                                                  @Param("supplierId") int supplierId);

    List<ZhangDanMingxiPo> customerZhangdanMingxi(@Param("yearMonth") String yearMonth,
                                                  @Param("customerId") int customerId);

    int updateZhangdanByZaiceId(ZhangdanMingxi zhangdanMingxi);

    int updateByEmployeeInfo(ZhangdanMingxi zhangdanMingxi);

    int updateGongjijin(@Param("gongjijinGongsi") double gongjijinGongsi, @Param("gongjijinGeren") double gongjijinGeren, @Param("zaiceId") int zaiceId);

}