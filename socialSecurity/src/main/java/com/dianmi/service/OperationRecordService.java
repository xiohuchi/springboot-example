package com.dianmi.service;

import com.dianmi.model.OperationRecord;

import java.util.List;

/**
 * created by www
 * 2017/10/21 15:02
 */
public interface OperationRecordService {

    public int insert(OperationRecord operationRecord);

    public List<OperationRecord> userOperationRecord(String reportingPeriod, int userId);
}