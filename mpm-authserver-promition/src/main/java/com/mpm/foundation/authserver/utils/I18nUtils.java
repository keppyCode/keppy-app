package com.mpm.foundation.authserver.utils;

import org.springframework.context.MessageSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class I18nUtils {
	public static String getMessage(MessageSource messageSource, String code) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		Locale locale = RequestContextUtils.getLocale(request);
		String msg = null;
		try {
			msg = messageSource.getMessage(code, null, locale);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return msg;
	}
	
	
	public static String getMessage(MessageSource messageSource, String code,Object[] args) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		Locale locale = RequestContextUtils.getLocale(request);
		String msg = null;
		try {
			msg = messageSource.getMessage(code, args, locale);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return msg;
	}
}
