package com.dianmi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class EmailRecord {
    private Integer erId;

    private Integer userId;		//操作者ID

    private String emailTo; 	//接收邮件者账号

    private String emailCc;		//抄送邮件者账号

    private Date sendDate;		//邮件发送时间

    private String emailTopic;

    private String reportingPeriod;

    private String emailContext;

    
    
    public EmailRecord(Integer userId, String emailTo, String emailCc, Date sendDate, String emailTopic,
			String reportingPeriod, String emailContext) {
		super();
		this.userId = userId;
		this.emailTo = emailTo;
		this.emailCc = emailCc;
		this.sendDate = sendDate;
		this.emailTopic = emailTopic;
		this.reportingPeriod = reportingPeriod;
		this.emailContext = emailContext;
	}

	public EmailRecord(Integer erId, Integer userId, String emailTo, String emailCc, Date sendDate, String emailTopic, String reportingPeriod, String emailContext) {
        this.erId = erId;
        this.userId = userId;
        this.emailTo = emailTo;
        this.emailCc = emailCc;
        this.sendDate = sendDate;
        this.emailTopic = emailTopic;
        this.reportingPeriod = reportingPeriod;
        this.emailContext = emailContext;
    }


}