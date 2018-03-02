package com.dianmi.utils.json;

/**
 * 封装json返回
 */
public class ResultUtil {

	public static ResultJson success(RestEnum restEnum) {
		ResultJson resultJson = new ResultJson(restEnum.getStatus(), restEnum.getMessage());
		return resultJson;
	}

	public static ResultJson success(RestEnum restEnum, Object object) {
		ResultJson resultJson = new ResultJson(restEnum.getStatus(), restEnum.getMessage(), object);
		return resultJson;
	}

	public static ResultJson error(RestEnum restEnum) {
		ResultJson resultJson = new ResultJson(restEnum.getStatus(), restEnum.getMessage());
		return resultJson;
	}

	public static ResultJson error(RestEnum restEnum, Object object) {
		ResultJson resultJson = new ResultJson(restEnum.getStatus(), restEnum.getMessage(), object);
		return resultJson;
	}

}