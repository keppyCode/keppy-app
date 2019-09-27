package com.mpm.foundation.authserver.security.core.userdetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 公司过期异常
 * 
 * @author zzm
 *
 */
public class CompanyExpiredException extends UsernameNotFoundException {

	public CompanyExpiredException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

	public CompanyExpiredException(String msg, Throwable t) {
		super(msg, t);
	}

}
