package com.dianmi.utils.validate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhiwei loong
 * @Date 2017年11月22日 下午3:51:28
 * @Description
 */
public class IDCardValidate {
	public static void main(String args[]) {
		System.out.println(validate(""));
	}

	/**
	 * 功能：身份证的有效验证
	 * 
	 * @param IDStr
	 *            身份证号
	 * @return true 有效：false 无效
	 * @throws ParseException
	 */
	public static boolean validate(String IDStr) {
		String[] ValCodeArr = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
		String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
		String Ai = "";
		if (IDStr.length() != 18) {
			return false;
		}
		if (IDStr.length() == 18) {
			Ai = IDStr.substring(0, 17);
		}
		if (isNumeric(Ai) == false) {
			return false;
		}
		String strYear = Ai.substring(6, 10);// 年份
		String strMonth = Ai.substring(10, 12);// 月份
		String strDay = Ai.substring(12, 14);// 日
		if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
			// 身份证生日无效
			return false;
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
					|| (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
				// 身份证生日不在有效范围
				return false;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			// 身份证月份无效
			return false;
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			// 身份证日期无效
			return false;
		}
		Map<String, String> h = GetAreaCode();
		if (h.get(Ai.substring(0, 2)) == null) {
			// 身份证地区编码错误
			return false;
		}
		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];
		Ai = Ai + strVerifyCode;
		if (IDStr.length() == 18) {
			if (Ai.equals(IDStr) == false) {
				// 身份证无效，不是合法的身份证号码
				return false;
			}
		} else {
			return true;
		}
		return true;
	}

	/**
	 * 功能：设置地区编码
	 * 
	 * @return Hashtable 对象
	 */
	private static Map<String, String> GetAreaCode() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("11", "北京");
		map.put("12", "天津");
		map.put("13", "河北");
		map.put("14", "山西");
		map.put("15", "内蒙古");
		map.put("21", "辽宁");
		map.put("22", "吉林");
		map.put("23", "黑龙江");
		map.put("31", "上海");
		map.put("32", "江苏");
		map.put("33", "浙江");
		map.put("34", "安徽");
		map.put("35", "福建");
		map.put("36", "江西");
		map.put("37", "山东");
		map.put("41", "河南");
		map.put("42", "湖北");
		map.put("43", "湖南");
		map.put("44", "广东");
		map.put("45", "广西");
		map.put("46", "海南");
		map.put("50", "重庆");
		map.put("51", "四川");
		map.put("52", "贵州");
		map.put("53", "云南");
		map.put("54", "西藏");
		map.put("61", "陕西");
		map.put("62", "甘肃");
		map.put("63", "青海");
		map.put("64", "宁夏");
		map.put("65", "新疆");
		map.put("71", "台湾");
		map.put("81", "香港");
		map.put("82", "澳门");
		map.put("91", "国外");
		return map;
	}

	/**
	 * 功能：判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 功能：判断字符串是否为日期格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDate(String strDate) {
		Pattern pattern = Pattern.compile(
				"^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}
}