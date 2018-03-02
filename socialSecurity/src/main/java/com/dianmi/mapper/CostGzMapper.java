package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.CostDg;
import com.dianmi.model.CostGz;

public interface CostGzMapper {
	int deleteByPrimaryKey(Integer cgId);

	int insert(CostGz record);

	int insertSelective(CostGz record);

	CostGz selectByPrimaryKey(Integer cgId);

	int updateByPrimaryKeySelective(CostGz record);

	int updateByPrimaryKey(CostGz record);

	List selectGZhomeAll(@Param(value = "supplierId") Integer supplierId,
			@Param(value = "reportingPeriod") String yearMonth, @Param("pages") Integer currPage,
			@Param("pageSize") Integer pageSize);

	String selectEmployeeName(@Param(value = "supplierId") Integer supplierId,
			@Param(value = "reportingPeriod") String month,
			@Param(value = "certificateNumber") String certificateNumber);

	void updateAccumulation(@Param(value = "personAccumulationNum") String p_AccumulationNum,
			@Param(value = "unitAccumulation") Double unitFee, @Param(value = "personAccumulation") Double personFee,
			@Param(value = "supplierId") Integer supplierId, @Param(value = "reportingPeriod") String yearMonth,
			@Param(value = "certificateNumber") String certificateNum, @Param(value = "userId") Integer userId);

	int deleteGZhomeDetails(@Param(value = "supplierId") Integer supplierId,
			@Param(value = "reportingPeriod") String yearMonth);

	List<CostGz> selectGZhomeAllBySupplierId(@Param(value = "supplierId") Integer supplierId,
			@Param(value = "reportingPeriod") String yearMonth);

	List<Integer> isCostImport(@Param("yearMonth") String yearMonth,
			@Param("certificateNumber") String certificateNumber);

	int updateCostGz(CostGz costGz);

	int updateAccumulationFund(CostGz costGz);
	
	List<Map<String, Object>> costGroupBySupplier(String yearMonth);
	
	List<CostGz> selectCostBySupplier(@Param("yearMonth")String yearMonth,@Param("supplierId")Integer supplierId);
}