package com.mpm.foundation.authserver.security.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "部署环境")
public class DeployProfile implements Serializable {
	@ApiModelProperty(value = "部署id")
	private String id;
	@ApiModelProperty(value = "部署地址")
	private String url;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public DeployProfile() {
		super();
		// TODO Auto-generated constructor stub
	}

}
