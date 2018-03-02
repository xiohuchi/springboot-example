package com.dianmi.utils;

/**
 * @Author:create by lzw
 * @Date:2017年12月8日 上午11:24:42
 * @Description:
 */
public class GetBirthDayFromIDCard {

	public static String getBirthday(String idCard) {
		String idCardNumber = idCard.trim();
		int idCardLength = idCardNumber.length();
		String birthday = null;
		if (idCardNumber == null || "".equals(idCardNumber)) {
			return null;
		}
		if (idCardLength == 18) {
			birthday = idCardNumber.substring(6, 10) + "-" + idCardNumber.substring(10, 12) + "-"
					+ idCardNumber.substring(12, 14);
		}
		if (idCardLength == 15) {
			birthday = "19" + idCardNumber.substring(6, 8) + "-" + idCardNumber.substring(8, 10) + "-"
					+ idCardNumber.substring(10, 12);
		}
		return birthday;
	}
}