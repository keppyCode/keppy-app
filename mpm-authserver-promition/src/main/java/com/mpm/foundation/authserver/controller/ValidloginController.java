package com.mpm.foundation.authserver.controller;



import com.mpm.foundation.authserver.cfg.Constants;
import com.mpm.foundation.authserver.security.bean.MpmUser;
import com.mpm.foundation.authserver.utils.I18nUtils;
import com.mpm.foundation.authserver.vo.Json;
import com.mpm.foundation.authserver.vo.ResponseData;
import com.mpm.foundation.authserver.vo.ResponseDataVo;
import com.mpm.foundation.authserver.vo.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mpm.foundation.authserver.security.core.userdetails.CompanyExpiredException;
import com.mpm.foundation.authserver.security.core.userdetails.CompanyNotFoundException;
import com.mpm.foundation.authserver.security.core.userdetails.CompanyUnavailableException;
import com.mpm.foundation.authserver.security.core.userdetails.JdbcUserDetailsServiceImpl;
import com.mpm.foundation.authserver.security.crypto.password.MpmPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/valid")
public class ValidloginController {

    /**
     * 企业模式用户中心跳转地址
     */
    @Value("${mpm.enterpriseUsercenterUrl}")
    private String enterpriseUsercenterUrl;

    /**
     * mpm3.0用户中心跳转地址
     */
    @Value("${mpm.commonUsercenterUrl}")
    private String commonUsercenterUrl;



	@Autowired
	private JdbcUserDetailsServiceImpl userDetailsService;
	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Json valid(@RequestParam("username") String username, @RequestParam("password") String password) {
		UserDetails ud = null;
		try {
			ud = userDetailsService.loadUserByUsername(username);
		} catch (CompanyNotFoundException e) {// 公司没找到
			e.printStackTrace();
			return Json.buildErrorJson("3", getMessage("login.error.company.unfound"));
		} catch (CompanyUnavailableException e) {// 公司不可用/禁用
			e.printStackTrace();
			return Json.buildErrorJson("0", getMessage("login.error.company.unavailable"));
		} catch (CompanyExpiredException e) {// 公司过期
			e.printStackTrace();
			return Json.buildErrorJson("4", getMessage("login.error.company.expired"));
		} catch (UsernameNotFoundException e) {// 用户没找到
			e.printStackTrace();
			return Json.buildErrorJson("1", getMessage("login.error.account.unfound"));
		}

		MpmPasswordEncoder encoder = new MpmPasswordEncoder();
		if (!encoder.matches(password, ud.getPassword())) {
            return Json.buildErrorJson("2", getMessage("login.error.password.mistake"));
		}

		//用户中心跳转地址
        MpmUser mpmUser = (MpmUser)ud;
		if(Constants.BELONGTO_ENTERPRISE==mpmUser.getBelongto()){
            mpmUser.setUsercenterUrl(enterpriseUsercenterUrl);
        }else {
            mpmUser.setUsercenterUrl(commonUsercenterUrl);
        }
        //添加关联手机号
       if(Constants.BELONGTO_ENTERPRISE==mpmUser.getBelongto()&&Constants.ISRELATED!=mpmUser.getIsRelated()){
           return Json.buildErrorJson("11", mpmUser);
       }
       //首次登录密码验证
        if(Constants.ISVALIDATED!=mpmUser.getIsValidated()){
            return Json.buildErrorJson("12", mpmUser);
        }

       return  Json.buildSuccessJson("ok");
	}

	/**
	 * 最新校验接口
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "/standardlogin", method = RequestMethod.POST)
	public ResponseEntity standardValid(@RequestParam("username") String username, @RequestParam("password") String password) {
		UserDetails ud = null;
		try {
			ud = userDetailsService.loadUserByUsername(username);
		} catch (CompanyNotFoundException e) {// 公司没找到
			e.printStackTrace();
			return  ResponseEntity.buildCustomeError(3,getMessage("login.error.company.unfound"));
		} catch (CompanyUnavailableException e) {// 公司不可用/禁用
			e.printStackTrace();
			return  ResponseEntity.buildCustomeError(0,getMessage("login.error.company.unavailable"));
		} catch (CompanyExpiredException e) {// 公司过期
			e.printStackTrace();
			return ResponseEntity.buildCustomeError(4,getMessage("login.error.company.expired"));
		} catch (UsernameNotFoundException e) {// 用户没找到
			e.printStackTrace();
			return ResponseEntity.buildCustomeError(1, getMessage("login.error.account.unfound"));
		}

		MpmPasswordEncoder encoder = new MpmPasswordEncoder();
		if (!encoder.matches(password, ud.getPassword())) {
			return ResponseEntity.buildCustomeError(2, getMessage("login.error.password.mistake"));
		}

		//用户中心跳转地址
		MpmUser mpmUser = (MpmUser)ud;
		if(Constants.BELONGTO_ENTERPRISE==mpmUser.getBelongto()){
			mpmUser.setUsercenterUrl(enterpriseUsercenterUrl);
		}else {
			mpmUser.setUsercenterUrl(commonUsercenterUrl);
		}
		//添加关联手机号
		if(Constants.BELONGTO_ENTERPRISE==mpmUser.getBelongto()&&Constants.ISRELATED!=mpmUser.getIsRelated()){
			return ResponseEntity.buildCustomeError(11,getMessage("login.error.unrelated.mobile"),mpmUser);
		}
		//首次登录密码验证
		if(Constants.ISVALIDATED!=mpmUser.getIsValidated()){
			return ResponseEntity.buildCustomeError(12,getMessage("login.error.first.passsword"),mpmUser);
		}

		return  ResponseEntity.buildDefaultSuccess();
	}

	private String getMessage(String code) {
		return I18nUtils.getMessage(messageSource, code);
	}
}
