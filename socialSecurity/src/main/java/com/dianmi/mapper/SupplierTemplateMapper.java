package com.dianmi.mapper;

import java.util.List;

import com.dianmi.model.SupplierTemplate;

public interface SupplierTemplateMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(SupplierTemplate record);

	int insertSelective(SupplierTemplate record);

	SupplierTemplate selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(SupplierTemplate record);

	int updateByPrimaryKey(SupplierTemplate record);

	List<SupplierTemplate> selectAll();

	List<SupplierTemplate> selectBySupplierId(int supplierId);
}