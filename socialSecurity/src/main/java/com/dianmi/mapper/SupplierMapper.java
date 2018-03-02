package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.Supplier;

public interface SupplierMapper {
	int deleteByPrimaryKey(Integer sId);

	int insert(Supplier record);

	int insertSelective(Supplier record);

	Supplier selectByPrimaryKey(Integer sId);

	int updateByPrimaryKeySelective(Supplier record);

	int updateByPrimaryKey(Supplier record);

	List<Integer> selectSupplierByUserId(int userId);

	List<Integer> selectBySupplierMsg(@Param("city") String city, @Param("supplierName") String supplierName);

	List<Supplier> selectSuppliers(@Param("supplierName") String supplierName, @Param("userId") int userId,
			@Param("roleType") int roleType);

	List<Map<String, Object>> selectByCity(String city);

	List<Supplier> pageList(@Param("userId") Integer userId);

	List<Map<String, Object>> ziyouhuSupplier();
	
	String selectBySupplierId(Integer supplierId);
}