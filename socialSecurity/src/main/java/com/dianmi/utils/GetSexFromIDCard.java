package com.dianmi.utils;

/**
 * @Author:create by lzw
 * @Date:2017年12月8日 上午11:06:36
 * @Description:
 */
public class GetSexFromIDCard {

	public static String getSex(String id) {
		String sex = null;
		if (id.length() == 15) {
			sex = returnSexFrom15W(id);
		} else if (id.length() == 18) {
			sex = returnSexFrom18W(id);
		}
		return sex;
	}

	public static String returnSexFrom18W(String id) {
		String sex;
		if (Integer.parseInt(id.substring(16).substring(0, 1)) % 2 == 0) {
			sex = "女";
		} else {
			sex = "男";
		}
		return sex;
	}

	public static String returnSexFrom15W(String id) {
		String usex = id.substring(14, 15);
		String sex;
		if (Integer.parseInt(usex) % 2 == 0) {
			sex = "女";
		} else {
			sex = "男";
		}
		return sex;
	}
}