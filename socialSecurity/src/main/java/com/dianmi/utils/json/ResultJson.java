package com.dianmi.utils.json;

/**
 * 返回json格式
 *
 * @param <T>
 */
public class ResultJson {

	private int status;
	private String message;
	private Object data;

	public ResultJson() {
	}

	public ResultJson(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public ResultJson(int status, String message, Object data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
