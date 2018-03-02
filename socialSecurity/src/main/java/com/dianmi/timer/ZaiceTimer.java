package com.dianmi.timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.dianmi.service.ZaiceService;

/**
 * created by www 2017/10/24 9:10
 */
@Configuration
@EnableScheduling
public class ZaiceTimer {

	@Autowired
	private ZaiceService zaiceService;

	@Scheduled(cron = "0 0 0 1 * ?")
	public void syncZaice() {
		System.out.println("同步数据......");
		zaiceService.syncPreMonthZaice();
	}

}