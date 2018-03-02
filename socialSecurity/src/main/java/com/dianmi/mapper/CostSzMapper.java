package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.CostSz;

public interface CostSzMapper {
	int deleteByPrimaryKey(Integer csId);

	int insert(CostSz record);

	int insertSelective(CostSz record);

	CostSz selectByPrimaryKey(Integer csId);

	int updateByPrimaryKeySelective(CostSz record);

	int updateByPrimaryKey(CostSz record);

	List<Integer> isCostImport(@Param("supplierId") int supplierId, @Param("yearMonth") String yearMonth,
			@Param("certificateNumber") String certificateNumber);

	int updateCostSz(CostSz costSz);

	int updateGongjijin(CostSz costSz);

	List<CostSz> selectSzhomeAll(@Param(value = "supplierId") Integer supplierId,
			@Param(value = "reportingPeriod") String yearMonth, @Param("pages") Integer currPage,
			@Param("pageSize") Integer pageSize);

	String selectEmployeeName(@Param("supplierId") Integer supplierId, @Param("reportingPeriod") String month,
			@Param("certificateNumber") String certificateNumber);

	List<CostSz> selectSZhomeAlls();

	int updateAccumulation(@Param("unitAccumulation") Double c_total, @Param("personAccumulation") Double p_total,
			@Param("supplierId") Integer supplierId, @Param("reportingPeriod") String yearMonth,
			@Param("certificateNumber") String certificateNum);

	int deleteSZhomeDetails(@Param(value = "supplierId") Integer supplierId,
			@Param(value = "reportingPeriod") String yearMonth);

	List<CostSz> selectSZhomeAllBySupplierId(@Param(value = "supplierId") Integer supplierId,
			@Param(value = "reportingPeriod") String yearMonth);

	List<Map<String, Object>> costGroupBySupplier(String yearMonth);

	List<CostSz> selectCostBySupplier(@Param("yearMonth") String yearMonth, @Param("supplierId") Integer supplierId);
}