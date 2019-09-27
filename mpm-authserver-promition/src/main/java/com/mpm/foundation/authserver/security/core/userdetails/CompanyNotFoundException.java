package com.mpm.foundation.authserver.security.core.userdetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 公司不存在异常
 * 
 * @author zzm
 *
 */
public class CompanyNotFoundException extends UsernameNotFoundException {

	public CompanyNotFoundException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

	public CompanyNotFoundException(String msg, Throwable t) {
		super(msg, t);
	}

}
