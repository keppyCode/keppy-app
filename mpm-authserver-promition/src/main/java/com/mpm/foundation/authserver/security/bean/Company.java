package com.mpm.foundation.authserver.security.bean;

import java.io.Serializable;

/**
 * 公司微信配置信息
 * 
 * @author zzm
 *
 */
public class Company implements Serializable {
	private String code;
	private String corpid;
	private String secret;

	public Company() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCorpid() {
		return corpid;
	}

	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
