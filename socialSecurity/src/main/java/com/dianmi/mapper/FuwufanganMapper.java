package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.Fuwufangan;
import com.dianmi.model.po.FuwufanganPo;

public interface FuwufanganMapper {
	int deleteByPrimaryKey(Integer fwId);

	int insert(Fuwufangan record);

	int insertSelective(Fuwufangan record);

	Fuwufangan selectByPrimaryKey(Integer fwId);

	int updateByPrimaryKeySelective(Fuwufangan record);

	int updateByPrimaryKey(Fuwufangan record);

	List<Fuwufangan> selectBySocialMsg(@Param("city") String city, @Param("customerId") int customerId,
			@Param("householdProperty") String householdProperty,
			@Param("socialSecurityType") String socialSecurityType);

	List<Integer> selectByCustomerMsg(@Param("city") String city, @Param("customerId") int customerId,
			@Param("supplierId") int supplierId);

	List<FuwufanganPo> selectAll(String city);

	List<Map<String, Object>> getSupplier(int userId);
}