package com.mpm.foundation.authserver.vo;

public class Json<T> implements java.io.Serializable {

	private boolean success = false;

	private String msg = "";

	private T obj = null;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}

	public static Json buildSuccessJson(String msg) {
		Json json = new Json();
		json.setSuccess(true);
		json.setMsg(msg);
		return json;
	}

	public static Json buildErrorJson(String msg) {
		Json json = new Json();
		json.setSuccess(false);
		json.setMsg(msg);
		return json;
	}
	
	public static Json buildErrorJson(String msg,Object obj) {
		Json json = buildErrorJson(msg);
		json.setObj(obj);
		return json;
	}

}