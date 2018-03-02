package com.dianmi.mapper;

import com.dianmi.model.OperationRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OperationRecordMapper {
    int deleteByPrimaryKey(Integer orId);

    int insert(OperationRecord record);

    int insertSelective(OperationRecord record);

    OperationRecord selectByPrimaryKey(Integer orId);

    int updateByPrimaryKeySelective(OperationRecord record);

    int updateByPrimaryKey(OperationRecord record);
    
    //操作者当月操作记录信息
    List<OperationRecord> userOperationRecord(@Param("reportingPeriod")String reportingPeriod, @Param("userId")int userId);

    int updateStatus(@Param("operationRecordId")int operationRecordId);
}