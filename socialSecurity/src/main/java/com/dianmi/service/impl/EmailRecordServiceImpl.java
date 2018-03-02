package com.dianmi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianmi.mapper.EmailRecordMapper;
import com.dianmi.model.EmailRecord;
import com.dianmi.service.EmailRecordService;

@Service
public class EmailRecordServiceImpl implements EmailRecordService {
	@Autowired
	private EmailRecordMapper emailRecordMapper;

	public List<EmailRecord> emailRecords(String yearMonth,int userId) {
		return emailRecordMapper.selectByUserId(yearMonth,userId);
	}

}
