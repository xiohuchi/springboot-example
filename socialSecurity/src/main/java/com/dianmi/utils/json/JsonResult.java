package com.dianmi.utils.json;

import com.alibaba.fastjson.JSONObject;

/**
 * created by www 2017/9/28 14:52
 */
public class JsonResult {
	@SuppressWarnings("serial")
	public static String result(RestEnum statusEnum) {
		JSONObject jsonObject = new JSONObject() {
			{
				put("status", statusEnum.getStatus());
				put("message", statusEnum.getMessage());
			}
		};
		return jsonObject.toString();
	}

	@SuppressWarnings("serial")
	public static String result(Integer status, String message, Object result) {
		JSONObject jsonObject = new JSONObject() {
			{
				put("status", status);
				put("message", message);
				put("data", result);
			}
		};
		return jsonObject.toString();
	}

	@SuppressWarnings("serial")
	public static String result(RestEnum statusEnum, Object result) {
		JSONObject jsonObject = new JSONObject() {
			{
				put("status", statusEnum.getStatus());
				put("message", statusEnum.getMessage());
				put("data", result);
			}
		};
		return jsonObject.toString();
	}
}