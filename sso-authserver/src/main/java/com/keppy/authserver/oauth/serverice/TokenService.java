package com.keppy.authserver.oauth.serverice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class TokenService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private TokenStore tokenStore;

	public void setTokenGranter(TokenGranter tokenGranter) {
		this.tokenGranter = tokenGranter;
	}

	private TokenGranter tokenGranter;

	private String client_sql = "select client_id from oauth_client_details";
	private String third_party_sql = "select CONCAT(oauth_provider,':',company_code,':',oauth_id) from uc_third_party_oauth_authenticate where user_id=?";
	private String enterprise_sql = "select CONCAT('enterprise:',t.company_code,':',login_name) from uc_enterprise_authenticate t where t.user_id =?";

	public void clearToken(String userId) {
		List<String> clients = getClientIds();
		List<String> unames = getUsernames(userId);
		List<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
		for (String clientId : clients) {
			for (String userName : unames) {
				Collection<OAuth2AccessToken> ts = tokenStore.findTokensByClientIdAndUserName(clientId, userName);
				if (!ts.isEmpty()) {
					tokens.addAll(ts);
				}
			}
		}

		for (OAuth2AccessToken t : tokens) {
			tokenStore.removeAccessToken(t);
		}

	}

	public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
		return tokenGranter.grant(grantType, tokenRequest);
	}

	public List<String> getClientIds() {
		List<String> clients = jdbcTemplate.queryForList(client_sql, String.class);
		return clients;
	}

	public List<String> getUsernames(String userId) {
		List<String> eunames = jdbcTemplate.queryForList(enterprise_sql, String.class, userId);
		List<String> tunames = jdbcTemplate.queryForList(third_party_sql, String.class, userId);
		eunames.addAll(tunames);
		return eunames;
	}

}
