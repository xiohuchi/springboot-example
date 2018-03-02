package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.JianyuanYichang;

public interface JianyuanYichangMapper {
	int deleteByPrimaryKey(Integer ycId);

	int insert(JianyuanYichang record);

	int insertSelective(JianyuanYichang record);

	JianyuanYichang selectByPrimaryKey(Integer ycId);

	int updateByPrimaryKeySelective(JianyuanYichang record);

	int updateByPrimaryKey(JianyuanYichang record);

	List<JianyuanYichang> selectAll(@Param("yearMonth") String yearMonth, @Param("userId") int userId,
			@Param("importDate") String importDate);

	int deleteByCheckCode(String checkCode);

	List<Map<String, Object>> allImportDateTime(@Param("yearMonth") String yearMonth, @Param("userId") Integer userId);
}