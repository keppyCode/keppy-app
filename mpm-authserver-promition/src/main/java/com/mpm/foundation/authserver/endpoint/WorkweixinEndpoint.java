package com.mpm.foundation.authserver.endpoint;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.mpm.foundation.authserver.vo.ErrorVo;
import com.mpm.foundation.authserver.vo.ResponseDataVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mpm.foundation.authserver.security.bean.Company;
import com.mpm.foundation.authserver.service.CompanyService;
import com.mpm.foundation.authserver.service.WorkweixinService;

@FrameworkEndpoint
public class WorkweixinEndpoint {
	private String credentialsCharset = "UTF-8";

	@Autowired
	private CompanyService companyService;
	@Autowired
	private WorkweixinService workweixinService;

	@RequestMapping(value = "/workweixin/userId", method = RequestMethod.POST)
	public ResponseEntity workweixinUserId(HttpServletRequest request, String corpid, String companycode, String code)
			throws HttpRequestMethodNotSupportedException {
		try {
			ClientDetails authenticatedClient = obtainClient(request);
			Company company = companyService.getCompany(companycode, corpid);

			Assert.notNull(code, "code为空");
			String wxaccesstoken = workweixinService.getAccessToken(company);
			String userId = workweixinService.getUserInfo(wxaccesstoken, code, company);
			Map<String, String> map = new HashMap<>();
			map.put("userId", userId);
			return getResponse(map);
		} catch (Exception e) {
			e.printStackTrace();
			return getError(e.getClass().getSimpleName(), e.getMessage());
		}
	}

	@RequestMapping(value = "/workweixin/token", method = RequestMethod.POST)
	public ResponseEntity postAccessToken(HttpServletRequest request, String corpid, String companycode, String code,
			String userId) throws HttpRequestMethodNotSupportedException {

		try {
			ClientDetails authenticatedClient = obtainClient(request);
			// Assert.notNull(companycode, "companycode为空");
			Company company = companyService.getCompany(companycode, corpid);
			if (StringUtils.isBlank(userId)) {// 用户id没传，通过code去获取token然后获取用户id
				Assert.notNull(code, "code为空");
				String wxaccesstoken = workweixinService.getAccessToken(company);
				userId = workweixinService.getUserInfo(wxaccesstoken, code, company);
			}
			Map<String, String> requestParameters = new HashMap<>();
			requestParameters.put("companycode", company.getCode());
			requestParameters.put("userid", userId);
			TokenRequest tokenRequest = workweixinService.getOAuth2RequestFactory()
					.createTokenRequest(requestParameters, authenticatedClient);
			OAuth2AccessToken token = workweixinService.grant(tokenRequest);
			return getResponse(token);
		} catch (Exception e) {
			e.printStackTrace();
			return getError(e.getClass().getSimpleName(), e.getMessage());
		}

	}

	
	@RequestMapping(value = "/mpm/workweixinsignature", method = RequestMethod.POST)
	@ResponseBody
	public ResponseDataVo<Map<String, Object>> mpmworkweixinsignature(HttpServletRequest request, String corpid,
																	  String companycode, String noncestr, Long timestamp, String url)
			throws HttpRequestMethodNotSupportedException {
		try {
			Assert.notNull(noncestr,"noncestr为空");
			Assert.notNull(timestamp,"timestamp为空");
			Assert.notNull(url,"url为空");
			Company company = companyService.getCompany(companycode, corpid);
			String ticket = workweixinService.getJspapiTicket(company);
			String signature = workweixinService.getSignature(ticket,timestamp,url,noncestr);
			Map<String, Object> map = new HashMap<>();
			map.put("signature", signature);
			return ResponseDataVo.SUCCESS(map);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDataVo.CUSTOM_SERVER_ERROR(e.getMessage());
		}
	}

	@RequestMapping(value = "/mpm/workweixinuserid", method = RequestMethod.POST)
	public ResponseDataVo<Map<String, Object>> mpmworkweixinUserId(HttpServletRequest request, String corpid,
			String companycode, String code) throws HttpRequestMethodNotSupportedException {
		try {
			ClientDetails authenticatedClient = obtainClient(request);
			Company company = companyService.getCompany(companycode, corpid);

			Assert.notNull(code, "code为空");
			String wxaccesstoken = workweixinService.getAccessToken(company);
			String userId = workweixinService.getUserInfo(wxaccesstoken, code, company);
			Map<String, Object> map = new HashMap<>();
			map.put("userId", userId);
			return ResponseDataVo.SUCCESS(map);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDataVo.CUSTOM_SERVER_ERROR(e.getMessage());
		}
	}

	@RequestMapping(value = "/mpm/workweixintoken", method = RequestMethod.POST)
	@ResponseBody
	public ResponseDataVo<OAuth2AccessToken> postworkweixinToken(HttpServletRequest request, String corpid,
			String companycode, String code, String userId) throws HttpRequestMethodNotSupportedException {

		try {
			ClientDetails authenticatedClient = obtainClient(request);
			// Assert.notNull(companycode, "companycode为空");
			Company company = companyService.getCompany(companycode, corpid);
			if (StringUtils.isBlank(userId)) {// 用户id没传，通过code去获取token然后获取用户id
				Assert.notNull(code, "code为空");
				String wxaccesstoken = workweixinService.getAccessToken(company);
				userId = workweixinService.getUserInfo(wxaccesstoken, code, company);
			}
			Map<String, String> requestParameters = new HashMap<>();
			requestParameters.put("companycode", company.getCode());
			requestParameters.put("userid", userId);
			TokenRequest tokenRequest = workweixinService.getOAuth2RequestFactory()
					.createTokenRequest(requestParameters, authenticatedClient);
			OAuth2AccessToken token = workweixinService.grant(tokenRequest);
			return ResponseDataVo.SUCCESS(token);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDataVo.CUSTOM_SERVER_ERROR(e.getMessage());
		}

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

	private ResponseEntity<OAuth2AccessToken> getResponse(OAuth2AccessToken accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		return new ResponseEntity<OAuth2AccessToken>(accessToken, headers, HttpStatus.OK);
	}

	private ResponseEntity<Map<String, Object>> getResponse(Map m) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		return new ResponseEntity<Map<String, Object>>(m, headers, HttpStatus.OK);
	}

	private ResponseEntity<ErrorVo> getError(String error, String description) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		ErrorVo vo = new ErrorVo(error, description);
		return new ResponseEntity<ErrorVo>(vo, headers, HttpStatus.OK);
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
}
