package com.mpm.foundation.authserver.security.bean;

/**
 * 企业微信token bean
 * 
 * @author zzm
 *
 */
public class WorkWeixinAccessToken {
	/**
	 * token值
	 */
	private String accessToken;
	/**
	 * 过期时间秒
	 */
	private int expiresIn;

	public WorkWeixinAccessToken() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WorkWeixinAccessToken(String accessToken, int expiresIn) {
		super();
		this.accessToken = accessToken;
		this.expiresIn = expiresIn;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}
}
