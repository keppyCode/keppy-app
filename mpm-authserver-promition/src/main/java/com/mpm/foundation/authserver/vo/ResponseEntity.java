package com.mpm.foundation.authserver.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "接口返回结构定义", description = "接口返回结构定义")
public class ResponseEntity<T> implements java.io.Serializable {

	public static final int DEFUALT_SUCCESS_CODE = 200;
	public static final int DEFUALT_ERROR_CODE = 500;
	public static final String DEFUALT_SUCCESS_MESSAGE = "OK！";
	public static final String DEFUALT_ERROR_MESSAGE = "ERROR";
	@ApiModelProperty(value = "返回编码")
	private int code;
	@ApiModelProperty(value = "信息")
	private String message;
	@ApiModelProperty(value = "业务对象")
	private T object;

	public ResponseEntity() {
	}

	public ResponseEntity(int code) {
		this.code = code;
	}

	public ResponseEntity(int code, String message, T object) {
		this.code = code;
		this.message = message;
		this.object = object;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public static ResponseEntity buildDefaultSuccess() {
		ResponseEntity json = new ResponseEntity(DEFUALT_SUCCESS_CODE);
		json.setMessage(DEFUALT_SUCCESS_MESSAGE);
		return json;
	}
	public static ResponseEntity buildDefaultSuccess(Object obj) {
		ResponseEntity json = new ResponseEntity(DEFUALT_SUCCESS_CODE);
		json.setMessage(DEFUALT_SUCCESS_MESSAGE);
		json.setObject(obj);
		return json;
	}

	public static ResponseEntity buildDefaultSuccess(String msg, Object obj) {
		ResponseEntity json = new ResponseEntity(DEFUALT_SUCCESS_CODE);
		json.setMessage(msg);
		json.setObject(obj);
		return json;
	}

	public static ResponseEntity buildDefaultError(String msg) {
		ResponseEntity json = new ResponseEntity();
		json.setCode(DEFUALT_SUCCESS_CODE);
		json.setMessage(msg);
		return json;
	}

	public static ResponseEntity buildCustomeError(int code, String msg) {
		ResponseEntity json = new ResponseEntity();
		json.setCode(code);
		json.setMessage(msg);
		return json;
	}

	public static ResponseEntity buildCustomeError(int code, String msg, Object obj) {
		ResponseEntity json = new ResponseEntity(code,msg,obj);
		return json;
	}


}
