package com.mpm.foundation.authserver.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author huangyufei
 * @Description 统一返回组装类
 * @date 2018-05-29 16:25
 */
@ApiModel(value = "返回信息组装类", description = "返回信息类-->>BaseViewVo")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDataVo<T> implements Serializable {

	@ApiModelProperty(value = "返回信息类", name = "responseData")
	private ResponseData responseData;

	@ApiModelProperty(value = "返回数据类", name = "object")
	private T object;

	public T getObject() {
		return (T) object;
	}

	public void setObject(T object) {
		this.object = (T) object;
	}

	public ResponseData getResponseData() {
		return responseData;
	}

	public void setResponseData(ResponseData responseData) {
		this.responseData = responseData;
	}

	public ResponseDataVo() {
	}

	public ResponseDataVo(ResponseData responseData, T object) {
		super();
		this.responseData = responseData;
		this.object = object;
	}

	public ResponseDataVo(ResponseData responseData) {
		super();
		this.responseData = responseData;
	}

	/**
	 * 成功: --》》表示请求被服务器正常处理
	 */
	public static ResponseDataVo SUCCESS(Object object) {
		return new ResponseDataVo(
				new ResponseData(ResponseStatusCode.SUCCESS.getCode(), ResponseStatusCode.SUCCESS.getMessage()),
				object);
	}
	
	public static ResponseDataVo CUSTOM_SUCCESS(String msg) {
		return new ResponseDataVo(new ResponseData(ResponseStatusCode.SUCCESS.getCode(), msg), null);
	}

	/**
	 * 成功： --》》表示请求已成功处理，但是没有内容返回
	 */
	public static ResponseDataVo NOT_CONTENT() {
		return new ResponseDataVo(new ResponseData(ResponseStatusCode.NOT_CONTENT.getCode(),
				ResponseStatusCode.NOT_CONTENT.getMessage()));
	}

	/**
	 * 参数条件不满足： --》》客户端发送附带条件的请求时，条件不满足
	 */
	public static ResponseDataVo NOT_MODIFIED() {
		return new ResponseDataVo(new ResponseData(ResponseStatusCode.NOT_MODIFIED.getCode(),
				ResponseStatusCode.NOT_MODIFIED.getMessage()));
	}

	/**
	 * 请求错误： --》》请求报文存在语法错误或参数错误，服务器不能解析
	 */
	public static ResponseDataVo ERROR_PARAMS() {
		return new ResponseDataVo(new ResponseData(ResponseStatusCode.ERROR_PARAMS.getCode(),
				ResponseStatusCode.ERROR_PARAMS.getMessage()));
	}

	/**
	 * 没有权限： --》》验证身份失败
	 */
	public static ResponseDataVo UNAUTHORIZED() {
		return new ResponseDataVo(new ResponseData(ResponseStatusCode.UNAUTHORIZED.getCode(),
				ResponseStatusCode.UNAUTHORIZED.getMessage()));
	}

	/**
	 * 拒绝服务： --》》请求资源成功，服务器拒绝提供资源
	 */
	public static ResponseDataVo serverInternalError() {
		return new ResponseDataVo(
				new ResponseData(ResponseStatusCode.FORBIDDEN.getCode(), ResponseStatusCode.FORBIDDEN.getMessage()));
	}

	/**
	 * 找不到资源 --》》服务器找不到你请求的资源
	 */
	public static ResponseDataVo customerError() {
		return new ResponseDataVo(
				new ResponseData(ResponseStatusCode.NOTFOUNT.getCode(), ResponseStatusCode.NOTFOUNT.getMessage()));
	}

	/**
	 * 服务器异常 --》》可能是Web应用有bug或临时故障
	 */
	public static ResponseDataVo SERVER_ERROR() {
		return new ResponseDataVo(new ResponseData(ResponseStatusCode.SERVER_ERROR.getCode(),
				ResponseStatusCode.SERVER_ERROR.getMessage()));
	}

	public static ResponseDataVo SERVER_ERROR(Object object) {
		return new ResponseDataVo(new ResponseData(ResponseStatusCode.SERVER_ERROR.getCode(),
				ResponseStatusCode.SERVER_ERROR.getMessage()), object);
	}

	public static ResponseDataVo CUSTOM_SERVER_ERROR(String msg) {
		return new ResponseDataVo(new ResponseData(ResponseStatusCode.SERVER_ERROR.getCode(), msg), null);
	}
	
	public static ResponseDataVo CUSTOM_SERVER_ERROR(int code,String msg) {
		return new ResponseDataVo(new ResponseData(code, msg), null);
	}

	/**
	 * 未知异常
	 */
	public static ResponseDataVo UNNOW_ERROR() {
		return new ResponseDataVo(new ResponseData(ResponseStatusCode.UNNOW_ERROR.getCode(),
				ResponseStatusCode.UNNOW_ERROR.getMessage()));
	}

	/**
	 * 用户自定义异常 -->> 使用,参考以下的实现类：
	 * 
	 * @see UserLoginResponseDate
	 */
	public ResponseDataVo DEFINED(int code, String messager) {
		return new ResponseDataVo(new ResponseData(code, messager));
	}

	/**
	 * 用户自定义异常 -->> 使用： target.DEFINEDBYUSER()
	 */
	public static ResponseDataVo DEFINEDBYUSER(int code, String messager) {
		return new ResponseDataVo(new ResponseData(code, messager));
	}

}
