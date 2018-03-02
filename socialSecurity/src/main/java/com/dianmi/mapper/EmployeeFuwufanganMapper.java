package com.dianmi.mapper;

import com.dianmi.model.EmployeeFuwufangan;

public interface EmployeeFuwufanganMapper {
	int deleteByPrimaryKey(Integer efId);

	int insert(EmployeeFuwufangan record);

	int insertSelective(EmployeeFuwufangan record);

	EmployeeFuwufangan selectByPrimaryKey(Integer efId);

	int updateByPrimaryKeySelective(EmployeeFuwufangan record);

	int updateByPrimaryKey(EmployeeFuwufangan record);

	int updateByEmployeeInfo(EmployeeFuwufangan record);
}