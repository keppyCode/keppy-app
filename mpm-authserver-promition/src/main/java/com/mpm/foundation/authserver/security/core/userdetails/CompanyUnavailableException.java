package com.mpm.foundation.authserver.security.core.userdetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 公司状态不可用异常
 * 
 * @author zzm
 *
 */
public class CompanyUnavailableException extends UsernameNotFoundException {

	public CompanyUnavailableException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

	public CompanyUnavailableException(String msg, Throwable t) {
		super(msg, t);
	}

}
