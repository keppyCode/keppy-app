package com.mpm.foundation.authserver.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author huangyufei
 * @Description 服务端返回数据集
 * @date 2018-04-24  15:44
 */
@ApiModel(value = "返回信息类", description = "返回信息类-->>ResponseData")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseData implements Serializable{

    private static final long serialVersionUID = -8289823303352539870L;

    /**
     * 状态码
     *
     * @see ResponseStatusCode
     */
    @ApiModelProperty(value = "状态码", name = "code")
    private int code;

    /**
     * 消息体
     *
     * @see ResponseStatusCode
     */
    @ApiModelProperty(value = "消息体", name = "message")
    private String message;

    public ResponseData() {
        super();
    }

    public ResponseData(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }


    public void setCode(int code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

}
