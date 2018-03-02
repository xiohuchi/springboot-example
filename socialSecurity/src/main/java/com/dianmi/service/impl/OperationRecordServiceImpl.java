package com.dianmi.service.impl;

import com.dianmi.mapper.OperationRecordMapper;
import com.dianmi.model.OperationRecord;
import com.dianmi.service.OperationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * created by www
 * 2017/10/21 15:00
 */
@Service
public class OperationRecordServiceImpl implements OperationRecordService {

    @Autowired
    private OperationRecordMapper operationRecordMapper;

    public int insert(OperationRecord operationRecord) {
        return operationRecordMapper.insert(operationRecord);
    }

    public List<OperationRecord> userOperationRecord(String reportingPeriod, int userId) {
        return operationRecordMapper.userOperationRecord(reportingPeriod, userId);
    }
}
