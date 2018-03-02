package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.CostDg;
import com.dianmi.model.CostZh;

public interface CostZhMapper {
	int deleteByPrimaryKey(Integer czId);

	int insert(CostZh record);

	int insertSelective(CostZh record);

	CostZh selectByPrimaryKey(Integer czId);

	int updateByPrimaryKeySelective(CostZh record);

	int updateByPrimaryKey(CostZh record);

	List selectZHhomeAll(@Param(value = "supplierId") Integer supplierId,
			@Param(value = "reportingPeriod") String yearMonth, @Param("pages") Integer currPage,
			@Param("pageSize") Integer pageSize);

	String selectEmployeeName(@Param(value = "supplierId") Integer supplierId,
			@Param(value = "reportingPeriod") String month,
			@Param(value = "certificateNumber") String certificateNumber);

	int updateAccumulationFund(CostZh costZh);

	int deleteZHhomeDetails(@Param(value = "supplierId") Integer supplierId,
			@Param(value = "reportingPeriod") String yearMonth);

	List<CostZh> selectZHhomeAllBysupplierId(@Param(value = "supplierId") Integer supplierId,
			@Param(value = "reportingPeriod") String yearMonth);

	List<Integer> isCostImport(@Param("yearMonth") String yearMonth,
			@Param("certificateNumber") String certificateNumber);

	int updateCostZh(CostZh costZh);

	List<Map<String, Object>> costGroupBySupplier(String yearMonth);
	
	List<CostZh> selectCostBySupplier(@Param("yearMonth")String yearMonth,@Param("supplierId")Integer supplierId);
}