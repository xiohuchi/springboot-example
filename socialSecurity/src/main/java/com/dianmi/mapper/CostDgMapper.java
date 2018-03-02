package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.CostDg;

public interface CostDgMapper {
	int deleteByPrimaryKey(Integer cdId);

	int insert(CostDg record);

	int insertSelective(CostDg record);

	CostDg selectByPrimaryKey(Integer cdId);

	int updateByPrimaryKeySelective(CostDg record);

	int updateByPrimaryKey(CostDg record);

	List<CostDg> selectCostDgAll(@Param("supplierId") Integer supplierId, @Param("reportingPeriod") String month,
			@Param("pages") Integer currPage, @Param("pageSize") Integer pageSize);

	String selectEmployeeName(@Param("supplierId") Integer supplierId, @Param("reportingPeriod") String month,
			@Param("certificateNumber") String certificateNumber);

	void updateAccumulationFund(CostDg costDg);

	int deleteCostDgDetails(@Param("supplierId") Integer supplierId, @Param("reportingPeriod") String month);

	List<CostDg> selectCostDgAllBySupplierId(@Param("supplierId") Integer supplierId,
			@Param("reportingPeriod") String month);

	List<Integer> isCostImport(@Param("yearMonth") String yearMonth,
			@Param("certificateNumber") String certificateNumber);

	int updateCostDg(CostDg costDg);
	
	List<Map<String, Object>> costGroupBySupplier(String yearMonth);
	
	List<CostDg> selectCostBySupplier(@Param("yearMonth")String yearMonth,@Param("supplierId")Integer supplierId);
}