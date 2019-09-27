package com.mpm.foundation.authserver.security.bean;

/**
 * 企业微信token bean
 * 
 * @author zzm
 *
 */
public class WorkWeixinJspapiTicket {
	/**
	 * ticket值
	 */
	private String ticket;
	/**
	 * 过期时间秒
	 */
	private int expiresIn;

	public WorkWeixinJspapiTicket() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WorkWeixinJspapiTicket(String ticket, int expiresIn) {
		super();
		this.ticket = ticket;
		this.expiresIn = expiresIn;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}
}
