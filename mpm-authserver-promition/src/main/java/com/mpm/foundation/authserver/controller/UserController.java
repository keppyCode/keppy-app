package com.mpm.foundation.authserver.controller;

import java.security.Principal;

import com.mpm.foundation.authserver.security.bean.MpmUser;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

@RestController
public class UserController {

	@RequestMapping(value = "/api/user", method = RequestMethod.GET)
	public String me(Principal principal) {
		if (principal instanceof OAuth2Authentication) {
			OAuth2Authentication oauth = (OAuth2Authentication) principal;
			String token = "";
			if (oauth.getDetails() instanceof OAuth2AuthenticationDetails) {
				OAuth2AuthenticationDetails detail = (OAuth2AuthenticationDetails) oauth.getDetails();
				token = detail.getTokenValue();
			}
			if (oauth.getUserAuthentication().getPrincipal() instanceof MpmUser) {
				return JSON.toJSONString(oauth.getUserAuthentication().getPrincipal());
			}
		}
		return JSON.toJSONString(principal);
	}
}
