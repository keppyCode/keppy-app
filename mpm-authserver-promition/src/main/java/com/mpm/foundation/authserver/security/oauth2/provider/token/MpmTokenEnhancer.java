package com.mpm.foundation.authserver.security.oauth2.provider.token;

import java.util.HashMap;
import java.util.Map;

import com.mpm.foundation.authserver.security.bean.MpmUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;


/**
 * 个性化的token信息拓展
 * 
 * @author zzm
 *
 */
public class MpmTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		if (accessToken instanceof DefaultOAuth2AccessToken) {
			DefaultOAuth2AccessToken token = ((DefaultOAuth2AccessToken) accessToken);
			if (authentication.getUserAuthentication() instanceof UsernamePasswordAuthenticationToken) {
				UsernamePasswordAuthenticationToken userAuthentication = (UsernamePasswordAuthenticationToken) authentication
						.getUserAuthentication();
				if (userAuthentication.getPrincipal() instanceof MpmUser) {
					MpmUser user = (MpmUser) userAuthentication.getPrincipal();
					Map<String, Object> additionalInformation = new HashMap<String, Object>();
					additionalInformation.put("user_id", user.getId());
					if (StringUtils.isNoneBlank(user.getCompanycode())) {
						additionalInformation.put("company_code", user.getCompanycode());
					}
					additionalInformation.put("face", user.getFace());
					additionalInformation.put("user_name", user.getUsername());
					additionalInformation.put("login_name", user.getLoginname());
					additionalInformation.put("login_type", user.getLogintype());
					additionalInformation.put("name", user.getName());
					if (user.getProfile() != null) {
						additionalInformation.put("server_id", user.getProfile().getId());
						additionalInformation.put("server_url", user.getProfile().getUrl());
					}
					if(StringUtils.isNoneBlank(user.getBelongto())) {
						additionalInformation.put("belong_to", user.getBelongto());
					}
					token.setAdditionalInformation(additionalInformation);
				}
			} else if (authentication.getUserAuthentication() instanceof PreAuthenticatedAuthenticationToken) {
				PreAuthenticatedAuthenticationToken userAuthentication = (PreAuthenticatedAuthenticationToken) authentication
						.getUserAuthentication();
				if (userAuthentication.getPrincipal() instanceof MpmUser) {
					MpmUser user = (MpmUser) userAuthentication.getPrincipal();
					Map<String, Object> additionalInformation = new HashMap<String, Object>();
					additionalInformation.put("user_id", user.getId());
					if (StringUtils.isNoneBlank(user.getCompanycode())) {
						additionalInformation.put("company_code", user.getCompanycode());
					}
					additionalInformation.put("face", user.getFace());
					additionalInformation.put("user_name", user.getUsername());
					additionalInformation.put("login_name", user.getLoginname());
					additionalInformation.put("login_type", user.getLogintype());
					additionalInformation.put("name", user.getName());
					if (user.getProfile() != null) {
						additionalInformation.put("server_id", user.getProfile().getId());
						additionalInformation.put("server_url", user.getProfile().getUrl());
					}
					if(StringUtils.isNoneBlank(user.getBelongto())) {
						additionalInformation.put("belong_to", user.getBelongto());
					}
					token.setAdditionalInformation(additionalInformation);
				}
			}

		}
		return accessToken;
	}
}
