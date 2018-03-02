package com.dianmi.service;

import java.util.List;

import com.dianmi.model.EmailRecord;

public interface EmailRecordService {

	public List<EmailRecord> emailRecords(String yearMonth,int userId);

}
