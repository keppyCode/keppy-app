package com.mpm.foundation.authserver.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mpm.foundation.authserver.security.bean.MpmEmdmUser;
import com.mpm.foundation.authserver.security.bean.MpmUser;
import com.mpm.foundation.authserver.vo.ResponseDataVo;
import com.mpm.foundation.authserver.vo.ResponseStatusCode;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class SSOUtils {
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER = "Bearer";
	public static final String BASIC = "Basic";
	public static final String GRANT_TYPE_CODE = "authorization_code";
	public static final String GRANT_TYPE_PASSWORD = "password";
	public static final String GRANT_TYPE_CLIENT = "client_credentials";
	public static final String GRANT_TYPE_IMPLICIT = "implicit";
	public static final String GRANT_TYPE_REFRESH = "refresh_token";

	private static Map<String, MpmUser> map = new HashMap<>();
	private static Map<String, MpmEmdmUser> emdmmap = new HashMap<>();

	public static void cleanCache() {
		map.clear();
		emdmmap.clear();
	}

	public static String getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			OAuth2Authentication auth = (OAuth2Authentication) authentication;
			return auth.getPrincipal().toString();
		} else {
			return null;
		}
	}

	private static String encodeBasicAuthorizationHeader(String clientId, String clientSecret) {
		String str = clientId + ":" + clientSecret;
		return BASIC + " " + Base64.encodeBase64String(str.getBytes());
	}

	/**
	 * �ͻ���ģʽ��ȡtoken
	 * 
	 * @param url          oauth2 token endpoint
	 * @param clientId
	 * @param clientSecret
	 * @return
	 * @throws Exception
	 */
	public static String getClientCredentialsToken(String url, String clientId, String clientSecret) throws Exception {
		String basic = encodeBasicAuthorizationHeader(clientId, clientSecret);

		CloseableHttpClient httpclient = HttpClients.custom().build();
		HttpUriRequest post = RequestBuilder.post().setUri(new URI(url)).addParameter("grant_type", GRANT_TYPE_CLIENT)
				.addHeader(AUTHORIZATION_HEADER, basic).build();

		CloseableHttpResponse response = httpclient.execute(post);
		try {
			HttpEntity entity = response.getEntity();// ��������
			System.out.println(response.getStatusLine().getStatusCode());
			String body = EntityUtils.toString(entity, "utf-8");
			System.out.println(body);
			if (response.getStatusLine().getStatusCode() == 200) {
				Map map = JSON.parseObject(body, Map.class);
				if (map.get("access_token") != null) {
					String token = map.get("access_token").toString();
					return token;
				}
			}

		} finally {
			response.close();
		}
		return null;
	}

	public static String getToken() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			OAuth2Authentication auth = (OAuth2Authentication) authentication;
			if (auth.getDetails() instanceof OAuth2AuthenticationDetails) {
				OAuth2AuthenticationDetails detail = (OAuth2AuthenticationDetails) auth.getDetails();
				return detail.getTokenValue();
			}
		}
		return null;
	}

	/**
	 * 通过token获取用户中心数据
	 * 
	 * @param url   oauth2 check token endpoint
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public static MpmUser getCurrentMpmUser(String url, String token, String clientId, String clientSecret)
			throws Exception {
		MpmUser user = map.get(token);
		if (user == null) {
			user = getCurrentMpmUserInteral(url, token, clientId, clientSecret);
			if (user != null) {
				map.put(token, user);
			}
		}
		return user;
	}

	private static MpmUser getCurrentMpmUserInteral(String url, String token, String clientId, String clientSecret)
			throws Exception {
		String basic = encodeBasicAuthorizationHeader(clientId, clientSecret);
		HttpUriRequest get = RequestBuilder.get().setUri(new URI(url)).addParameter("token", token)
				.addHeader(AUTHORIZATION_HEADER, basic).build();
		CloseableHttpClient httpclient = HttpClients.custom().build();
		CloseableHttpResponse response = httpclient.execute(get);
		try {
			HttpEntity entity = response.getEntity();
			System.out.println(response.getStatusLine().getStatusCode());
			String body = EntityUtils.toString(entity, "utf-8");
			System.out.println(body);
			if (response.getStatusLine().getStatusCode() == 200) {
				MpmUser user = JSON.parseObject(body, MpmUser.class);
				return user;
			}

		} finally {
			response.close();
		}
		return null;
	}

	/**
	 * 通过token获取企业用户信息
	 * 
	 * @param url          oauth2 check token endpoint
	 * @param emdmendpoint 通过token 获取企业数据接口地址
	 * @param token
	 * @param clientId
	 * @param clientSecret
	 * @return
	 * @throws Exception
	 */
	public static MpmEmdmUser getCurrentMpmEmdmUser(String url, String emdmendpoint, String token, String clientId,
			String clientSecret, OAuth2RestOperations restTemplate) throws Exception {
		MpmEmdmUser euser = emdmmap.get(token);
		if (euser == null) {
			MpmUser user = getCurrentMpmUser(url, token, clientId, clientSecret);
			euser = new MpmEmdmUser();
			BeanUtils.copyProperties(user, euser);
			String emdmurl = emdmendpoint + "?companyCode={1}&userId={2}";
			String body = restTemplate
					.getForEntity(emdmurl, String.class, new Object[] { user.getCompanycode(), user.getId() })
					.getBody();
			ResponseDataVo<Map<String, Object>> vo = JSON.parseObject(body,
					new TypeReference<ResponseDataVo<Map<String, Object>>>() {
					});
			System.out.println(body);
			Assert.isTrue(ResponseStatusCode.SUCCESS.getCode() == vo.getResponseData().getCode(),
					"调用" + emdmendpoint + "异常:" + body);
			Map<String, Object> map = vo.getObject();
			Assert.notNull(map, token + "调用" + emdmendpoint + "异常:" + body);
			Assert.isTrue(!map.isEmpty(), token + "调用" + emdmendpoint + "异常:" + body);
			String id = map.get("id").toString();
			Assert.isTrue(euser.getId().equals(id), "从" + url + "和" + emdmendpoint + "获取的用户id不一致token:" + token);
			euser.setCompanyId(getStringValue(map, "companyId"));
			euser.setEmname(getStringValue(map, "username"));
			euser.setDepartmentId(getStringValue(map, "departmentId"));
			euser.setDepartmentName(getStringValue(map, "departmentName"));
			euser.setEmployeeId(getStringValue(map, "employeeId"));
			euser.setEmpNum(getStringValue(map, "empNum"));
			emdmmap.put(token, euser);
		}
		return euser;
	}

	public static String getStringValue(Map<String, Object> map, String key) {
		Object v = map.get(key);
		if (v == null) {
			return null;
		} else {
			return v.toString();
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main1(String[] args) {
		String str = "{\r\n" + "  \"responseData\": {\r\n" + "    \"code\": 200,\r\n"
				+ "    \"message\": \"success\"\r\n" + "  },\r\n" + "  \"object\": {\r\n"
				+ "    \"companyCode\": \"qysm\",\r\n" + "    \"departmentName\": \"技术开发部\",\r\n"
				+ "    \"companyId\": \"3d73e19f6f5146078fc3604bd7eb2481\",\r\n" + "    \"empNum\": \"5022\",\r\n"
				+ "    \"departmentId\": \"f537f39f25ef4286a403862c0d532b2a\",\r\n" + "    \"loginName\": \"5022\",\r\n"
				+ "    \"employeeId\": \"2c9080a664c6ae1c0164ca11c48c0068\",\r\n"
				+ "    \"id\": \"2c9080a664c6ae1c0164ca11c4e60069\",\r\n" + "    \"username\": \"张中民\"\r\n" + "  }\r\n"
				+ "}";
		ResponseDataVo<Map<String, Object>> vo = JSON.parseObject(str,
				new TypeReference<ResponseDataVo<Map<String, Object>>>() {
				});
		System.out.println(vo.getResponseData().getCode());
	}

	public static void main(String[] args) throws Exception {

//		System.out.println(
		SSOUtils.getClientCredentialsToken("http://auth-dev.jifenzhi.info/oauth/token", "odms",
				"R1LrTkhvE1SMJGCwNY326MFsEJfVaSDZ");
		// a74d22e0-48f8-4d63-abd3-a9baca902b48
//		MpmUser user = SSOUtils.getCurrentMpmUser("https://auth.test.jifenzhi.com/oauth/check_token",
//				"04b8f22d-a95a-4d60-bfc1-63c23759d8d1", "sso-client", "mpm");
//		System.out.println(user.getUsername());90b7fcb8-a34a-49c2-9984-10e9b458c7d2
	}

}
