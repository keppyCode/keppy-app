package com.mpm.foundation.authserver.endpoint;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.mpm.foundation.authserver.vo.ResponseDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.OAuth2RequestValidator;

import org.springframework.security.oauth2.provider.TokenRequest;

import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mpm.foundation.authserver.service.MpmTokenService;
import com.mpm.foundation.authserver.service.WorkweixinService;

@RestController
public class MpmTokenEndpoint {
	private String credentialsCharset = "UTF-8";
	@Autowired
	private MpmTokenService mpmTokenService;
	@Autowired
	private WorkweixinService workweixinService;
	@Autowired
	private ResourceServerTokenServices resourceServerTokenServices;
	@Autowired
	private ClientDetailsService clientDetailsService;

	private AccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
	private OAuth2RequestValidator oAuth2RequestValidator = new DefaultOAuth2RequestValidator();
	private OAuth2RequestFactory requestFactory;

	/**
	 * 移除用户id对应的所有token(慎用) 需要 Authorization Basic
	 * 
	 * @param request
	 * @param userId
	 * @return
	 * @throws HttpRequestMethodNotSupportedException
	 */
	@RequestMapping(value = "/mpm/cleartoken", method = RequestMethod.GET)
	public ResponseDataVo<String> clearAccessToken(HttpServletRequest request, String userId)
			throws HttpRequestMethodNotSupportedException {

		try {
			clearTokenInteral(request, userId);
			return ResponseDataVo.CUSTOM_SUCCESS("ok");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDataVo.CUSTOM_SERVER_ERROR(e.getMessage());
		}

	}

	/**
	 * 移除用户id对应的所有token(慎用) 需要 Authorization Basic
	 * 
	 * @param request
	 * @param userId
	 * @return
	 * @throws HttpRequestMethodNotSupportedException
	 */
	@RequestMapping(value = "/mpm/clear_token", method = RequestMethod.GET)
	public ResponseDataVo<String> clearToken(HttpServletRequest request, String userId)
			throws HttpRequestMethodNotSupportedException {

		try {
			clearTokenInteral(request, userId);
			return ResponseDataVo.CUSTOM_SUCCESS("ok");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDataVo.CUSTOM_SERVER_ERROR(e.getMessage());
		}

	}

	@RequestMapping(value = "/mpm/check_token")
	public ResponseDataVo<Map<String, ?>> checkToken(@RequestParam("token") String value, HttpServletRequest request) {
		try {
			ClientDetails authenticatedClient = obtainClient(request);
			OAuth2AccessToken token = resourceServerTokenServices.readAccessToken(value);
			if (token == null) {
				throw new InvalidTokenException("Token was not recognised");
			}
			if (token.isExpired()) {
				throw new InvalidTokenException("Token has expired");
			}
			OAuth2Authentication authentication = resourceServerTokenServices.loadAuthentication(token.getValue());
			Map<String, ?> response = accessTokenConverter.convertAccessToken(token, authentication);
			return ResponseDataVo.SUCCESS(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDataVo.CUSTOM_SERVER_ERROR(e.getMessage());
		}
	}

	@RequestMapping(value = "/mpm/token", method = RequestMethod.POST)
	public ResponseDataVo<OAuth2AccessToken> postAccessToken(HttpServletRequest request,
			@RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
		try {
			ClientDetails authenticatedClient = obtainClient(request);
			TokenRequest tokenRequest = getOAuth2RequestFactory().createTokenRequest(parameters, authenticatedClient);
			if (authenticatedClient != null) {
				oAuth2RequestValidator.validateScope(tokenRequest, authenticatedClient);
			}
			if (!org.springframework.util.StringUtils.hasText(tokenRequest.getGrantType())) {
				throw new InvalidRequestException("Missing grant type");
			}
			if (tokenRequest.getGrantType().equals("implicit")) {
				throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
			}
			if (isAuthCodeRequest(parameters)) {
				// The scope was requested or determined during the authorization step
				if (!tokenRequest.getScope().isEmpty()) {
					// logger.debug("Clearing scope of incoming token request");
					tokenRequest.setScope(Collections.<String>emptySet());
				}
			}

			if (isRefreshTokenRequest(parameters)) {
				// A refresh token has its own default scopes, so we should ignore any added by
				// the factory here.
				tokenRequest.setScope(OAuth2Utils.parseParameterList(parameters.get(OAuth2Utils.SCOPE)));
			}
			OAuth2AccessToken token = mpmTokenService.grant(tokenRequest.getGrantType(), tokenRequest);
			if (token == null) {
				throw new UnsupportedGrantTypeException("Unsupported grant type: " + tokenRequest.getGrantType());
			}
			return ResponseDataVo.SUCCESS(token);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDataVo.CUSTOM_SERVER_ERROR(e.getMessage());
		}
	}

	private void clearTokenInteral(HttpServletRequest request, String userId) throws IOException {
		Assert.notNull(userId, "userId不为空");
		ClientDetails authenticatedClient = obtainClient(request);
		mpmTokenService.clearToken(userId);
	}

	/**
	 * 获取oauthclient
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private ClientDetails obtainClient(HttpServletRequest request) throws IOException {
		String header = request.getHeader("Authorization");
		Assert.notNull(header, "没有Authorization header");
		Assert.isTrue(header.startsWith("Basic "), "Authorization Basic没有");
		String[] tokens = extractAndDecodeHeader(header, request);
		Assert.isTrue(tokens.length == 2, "Authorization Basic不合法");
		String clientId = tokens[0];
		ClientDetails client = workweixinService.loadClientByClientId(clientId);
		Assert.notNull(client, "找不到clientId");
		Assert.isTrue(client.getClientSecret().equals(tokens[1]), "ClientSecret不对");
		return client;
	}

	/**
	 * Decodes the header into a username and password.
	 *
	 * @throws BadCredentialsException if the Basic header is not present or is not
	 *                                 valid Base64
	 */
	private String[] extractAndDecodeHeader(String header, HttpServletRequest request) throws IOException {

		byte[] base64Token = header.substring(6).getBytes("UTF-8");
		byte[] decoded;
		try {
			decoded = Base64.decode(base64Token);
		} catch (IllegalArgumentException e) {
			throw new BadCredentialsException("Failed to decode basic authentication token");
		}

		String token = new String(decoded, credentialsCharset);

		int delim = token.indexOf(":");

		if (delim == -1) {
			throw new BadCredentialsException("Invalid basic authentication token");
		}
		return new String[] { token.substring(0, delim), token.substring(delim + 1) };
	}

	private OAuth2RequestFactory getOAuth2RequestFactory() {
		if (requestFactory != null) {
			return requestFactory;
		}
		requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
		return requestFactory;
	}

	/**
	 * @param principal the currently authentication principal
	 * @return a client id if there is one in the principal
	 */
	protected String getClientId(Principal principal) {
		Authentication client = (Authentication) principal;
		if (!client.isAuthenticated()) {
			throw new InsufficientAuthenticationException("The client is not authenticated.");
		}
		String clientId = client.getName();
		if (client instanceof OAuth2Authentication) {
			// Might be a client and user combined authentication
			clientId = ((OAuth2Authentication) client).getOAuth2Request().getClientId();
		}
		return clientId;
	}

	private boolean isRefreshTokenRequest(Map<String, String> parameters) {
		return "refresh_token".equals(parameters.get("grant_type")) && parameters.get("refresh_token") != null;
	}

	private boolean isAuthCodeRequest(Map<String, String> parameters) {
		return "authorization_code".equals(parameters.get("grant_type")) && parameters.get("code") != null;
	}
}
