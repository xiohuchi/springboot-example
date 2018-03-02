package com.dianmi.mapper;

import java.util.List;

import com.dianmi.model.KefuDept;

public interface KefuDeptMapper {
	int deleteByPrimaryKey(Integer kdId);

	int insert(KefuDept record);

	int insertSelective(KefuDept record);

	KefuDept selectByPrimaryKey(Integer kdId);

	int updateByPrimaryKeySelective(KefuDept record);

	int updateByPrimaryKey(KefuDept record);

	/**
	 * 所有客服部门信息
	 * 
	 * @return
	 */
	List<KefuDept> selectDepts();
}