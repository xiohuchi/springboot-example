package com.dianmi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dianmi.model.EmailRecord;

public interface EmailRecordMapper {
	int deleteByPrimaryKey(Integer erId);

	int insert(EmailRecord record);

	int insertSelective(EmailRecord record);

	EmailRecord selectByPrimaryKey(Integer erId);

	int updateByPrimaryKeySelective(EmailRecord record);

	int updateByPrimaryKeyWithBLOBs(EmailRecord record);

	int updateByPrimaryKey(EmailRecord record);

	List<EmailRecord> selectByUserId(@Param("reportingPeriod") String reportingPeriod, @Param("userId") int userId);
}