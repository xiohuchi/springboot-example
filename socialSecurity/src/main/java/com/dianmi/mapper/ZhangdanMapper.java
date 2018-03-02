package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.Zhangdan;
import com.dianmi.model.po.FenzuZhangdanPo;
import com.dianmi.model.po.ZongZhangdanPo;

public interface ZhangdanMapper {
	int deleteByPrimaryKey(Integer zId);

	int insert(Zhangdan record);

	int insertSelective(Zhangdan record);

	Zhangdan selectByPrimaryKey(Integer zId);

	int updateByPrimaryKeySelective(Zhangdan record);

	int updateByPrimaryKey(Zhangdan record);

	List<ZongZhangdanPo> supplierZhangdan(@Param("yearMonth") String yearMonth, @Param("userId") int userId,
			@Param("supplierName") String supplierName, @Param("roleType") int roleType);

	List<ZongZhangdanPo> customerZhangdan(@Param("yearMonth") String yearMonth, @Param("userId") int userId,
			@Param("customerName") String customerName, @Param("roleType") int roleType);

	List<FenzuZhangdanPo> groupByCustomer(@Param("yearMonth") String yearMonth, @Param("supplierId") int supplierId);

	List<FenzuZhangdanPo> groupBySupplier(@Param("yearMonth") String yearMonth, @Param("customerId") int customerId);

	List<Map<String, Object>> zhangdanBidui();
}