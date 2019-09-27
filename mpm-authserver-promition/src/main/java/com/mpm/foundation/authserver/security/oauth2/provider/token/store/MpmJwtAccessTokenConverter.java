package com.mpm.foundation.authserver.security.oauth2.provider.token.store;

import java.util.Map;

import com.mpm.foundation.authserver.security.bean.MpmUser;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.alibaba.fastjson.JSON;
import com.mpm.foundation.authserver.cfg.Constants;

public class MpmJwtAccessTokenConverter extends JwtAccessTokenConverter {
	/**
	 * 生成token
	 */
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		DefaultOAuth2AccessToken defaultOAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
		MpmUser user = (MpmUser) authentication.getPrincipal();
		defaultOAuth2AccessToken.getAdditionalInformation().put(Constants.USER_INFO, user);
		return super.enhance(defaultOAuth2AccessToken, authentication);
	}

	/**
	 * 解析token
	 */
	@Override
	public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
		OAuth2AccessToken oAuth2AccessToken = super.extractAccessToken(value, map);
		convertData(oAuth2AccessToken, oAuth2AccessToken.getAdditionalInformation());
		return super.extractAccessToken(value, map);
	}

	private void convertData(OAuth2AccessToken accessToken, Map<String, ?> map) {
		accessToken.getAdditionalInformation().put(Constants.USER_INFO, convertUserData(map.get(Constants.USER_INFO)));

	}

	private MpmUser convertUserData(Object map) {
		String json = JSON.toJSONString(map);
		MpmUser user = JSON.parseObject(json, MpmUser.class);
		return user;
	}
}
