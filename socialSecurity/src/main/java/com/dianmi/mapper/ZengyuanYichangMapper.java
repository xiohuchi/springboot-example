package com.dianmi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.ZengyuanYichang;

public interface ZengyuanYichangMapper {
	int deleteByPrimaryKey(Integer ycId);

	int insert(ZengyuanYichang record);

	int insertSelective(ZengyuanYichang record);

	ZengyuanYichang selectByPrimaryKey(Integer ycId);

	int updateByPrimaryKeySelective(ZengyuanYichang record);

	int updateByPrimaryKey(ZengyuanYichang record);

	List<ZengyuanYichang> selectAll(@Param("yearMonth") String yearMonth, @Param("userId") int userId,@Param("importDate")String importDate);

	int deleteByCheckCode(String checkCode);

	List<Map<String, Object>> allImportDateTime(@Param("yearMonth") String yearMonth, @Param("userId") Integer userId);
}