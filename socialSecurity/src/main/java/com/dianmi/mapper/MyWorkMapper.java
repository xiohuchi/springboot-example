package com.dianmi.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface MyWorkMapper {

	Map<String, Object> addAndDelStaff(@Param("userId") Integer userId, @Param("roleType") Byte roleType,
			@Param("yearMonth") String yearMonth);

	Map<String, Object> zaiceMsg(@Param("yearMonth") String yearMonth, @Param("userId") Integer userId,
			@Param("roleType") Byte roleType);

}
