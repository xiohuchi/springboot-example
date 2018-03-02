package com.dianmi.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * created by www 2017/10/21 14:11
 */
public class DateTimeUtil {
	/**
	 * @param
	 * @return
	 * @description 返回String类型日期时间
	 */
	public static String currentDateTimeStr() {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return localDateTime.format(dateTimeFormatter);
	}

	/**
	 * @return
	 */
	public static String currentDateStr() {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return localDateTime.format(dateTimeFormatter);
	}

}
