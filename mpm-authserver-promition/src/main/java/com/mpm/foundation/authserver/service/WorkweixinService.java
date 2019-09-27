package com.mpm.foundation.authserver.service;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.ProxyCreator;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.mpm.foundation.authserver.cfg.Constants;
import com.mpm.foundation.authserver.security.bean.Company;
import com.mpm.foundation.authserver.security.bean.WorkWeixinAccessToken;
import com.mpm.foundation.authserver.security.bean.WorkWeixinJspapiTicket;

/**
 * 企业微信服务类
 * 
 * @author zzm
 *
 */
@Component
public class WorkweixinService implements AuthenticationManager {
	@Autowired
	private RedisConnectionFactory connectionFactory;
	@Autowired
	private ClientDetailsService clientDetailsService;
	@Autowired
	private DefaultTokenServices tokenServices;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private UserDetailsService userDetailsService;
	private OAuth2RequestFactory requestFactory;
	private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
	private static final String WORKWEIXINACESS = "qywx_access:";
	private static final String WORKWEIXINTICKET = "qywx_ticket:";

	private String prefix = "";
	// https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRECT
	@Value("${qywx.gettokenUri}")
	private String gettokenUri;
	// https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE
	@Value("${qywx.getuserinfoUri}")
	private String getuserinfoUri;
	@Value("${qywx.getJspapiTicketUri}")
	private String getJspapiTicketUri;

	public static void main(String[] args) {
		WorkweixinService s = new WorkweixinService();
		System.out.println(s.getSignature("gggddsgd", System.currentTimeMillis(), "http://www.baidu.com", "fgddd"));
	}

	// jsapi_ticket=sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl-HhTdfl2fzFy1AOcHKP7qg&noncestr=Wm3WZYTPz0wzccnW&timestamp=1414587457&url=http://mp.weixin.qq.com?params=value
	public String getSignature(String ticket, Long timestamp, String url, String noncestr) {
		StringBuffer sb = new StringBuffer();
		sb.append("jsapi_ticket=" + ticket);
		sb.append("&noncestr=" + noncestr);
		sb.append("&timestamp=" + timestamp);
		sb.append("&url=" + url);
		String sign = DigestUtils.shaHex(sb.toString());
		return sign;
	}

	public String getJspapiTicket(Company company) throws Exception {
		String ticket = getJspapiTicketFromRedis(company.getCorpid());
		if (StringUtils.isBlank(ticket)) {
			String token = getAccessToken(company);
			WorkWeixinJspapiTicket t = getJspapiTicketFromWeixin(token);
			storeJspapiTicket(company.getCorpid(), t.getTicket(), t.getExpiresIn());
			return t.getTicket();
		}
		return ticket;
	}

	public WorkWeixinJspapiTicket getJspapiTicketFromWeixin(String token) throws Exception {
		CloseableHttpClient httpclient = HttpClients.custom().build();
		HttpUriRequest get = RequestBuilder.get().setUri(new URI(getJspapiTicketUri))
				.addParameter("access_token", token).build();
		CloseableHttpResponse response = httpclient.execute(get);
		try {
			HttpEntity entity = response.getEntity();// 中文乱码
			System.out.println(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				String body = EntityUtils.toString(entity, "utf-8");
				System.out.println(body);
				Map<String, Object> map = JSON.parseObject(body, Map.class);
				int errcode = Integer.parseInt(map.get("errcode").toString());
				String errmsg = map.get("errmsg").toString();
				if (errcode != 0) {
					throw new Exception("获取JspapiTicket信息错误:" + body);
				}
				String ticket = map.get("ticket").toString();
				int expires_in = Integer.parseInt(map.get("expires_in").toString());
				return new WorkWeixinJspapiTicket(ticket, expires_in);
			} else {
				throw new Exception("获取JspapiTicket错误返回:" + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			throw new Exception("获取JspapiTicket失败：" + e.getMessage());
		}
	}

	/**
	 * 根据公司获取企业微信token
	 * 
	 * @param company
	 * @return
	 * @throws Exception
	 */
	public String getAccessToken(Company company) throws Exception {
		String token = getAccessToken(company.getCorpid());
		if (StringUtils.isBlank(token)) {
			WorkWeixinAccessToken wxtoken = getFromWeixin(company);
			storeAccessToken(company.getCorpid(), wxtoken.getAccessToken(), wxtoken.getExpiresIn());
			token = wxtoken.getAccessToken();
		}
		return token;
	}

	/**
	 * 获取企业微信用户id
	 * 
	 * @param token
	 * @param code
	 * @param company
	 * @return
	 * @throws Exception
	 */
	public String getUserInfo(String token, String code, Company company) throws Exception {
		CloseableHttpClient httpclient = HttpClients.custom().build();
		HttpUriRequest get = RequestBuilder.get().setUri(new URI(getuserinfoUri)).addParameter("access_token", token)
				.addParameter("code", code).build();

		CloseableHttpResponse response = httpclient.execute(get);
		try {
			HttpEntity entity = response.getEntity();// 中文乱码
			System.out.println(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				String body = EntityUtils.toString(entity, "utf-8");
				System.out.println(body);
				Map<String, Object> map = JSON.parseObject(body, Map.class);
				int errcode = Integer.parseInt(map.get("errcode").toString());
				String errmsg = map.get("errmsg").toString();
				if (errcode != 0) {
					removeAccessToken(company.getCorpid());
					throw new Exception("获取微信用户信息错误:" + body);
				}
				if (map.get("UserId") != null) {
					String userId = map.get("UserId").toString();
					return userId;
				}
				if (map.get("OpenId") != null) {
					String openId = map.get("OpenId").toString();
					return openId;
				}
				removeAccessToken(company.getCorpid());
				throw new Exception("获取微信用户信息错误不知道取那个字段:" + body);
			} else {
				removeAccessToken(company.getCorpid());
				throw new Exception("获取微信用户信息错误返回:" + response.getStatusLine().getStatusCode());
			}

		} finally {
			response.close();
		}
	}

	/**
	 * 生成单点的token
	 * 
	 * @param tokenRequest
	 * @return
	 */
	public OAuth2AccessToken grant(TokenRequest tokenRequest) {
		String clientId = tokenRequest.getClientId();
		ClientDetails client = clientDetailsService.loadClientByClientId(clientId);
		return getAccessToken(client, tokenRequest);
	}

	/**
	 * 生产单点的 token
	 * 
	 * @param client
	 * @param tokenRequest
	 * @return
	 */
	private OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
		return tokenServices.createAccessToken(getOAuth2Authentication(client, tokenRequest));
	}

	/**
	 * 
	 * @param client
	 * @param tokenRequest
	 * @return
	 */
	private OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());
		String companycode = parameters.get("companycode");
		String userid = parameters.get("userid");

		Authentication userAuth = new UsernamePasswordAuthenticationToken(
				Constants.LOGIN_TYPE_WORKWEIXIN + ":" + companycode + ":" + userid, null);
		((AbstractAuthenticationToken) userAuth).setDetails(parameters);
		// 校验
		try {
			userAuth = authenticate(userAuth);
		} catch (AccountStatusException ase) {
			// covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
			throw new InvalidGrantException(ase.getMessage());
		} catch (BadCredentialsException e) {
			// If the username/password are wrong the spec says we should send 400/invalid
			// grant
			throw new InvalidGrantException(e.getMessage());
		}
		if (userAuth == null || !userAuth.isAuthenticated()) {
			throw new InvalidGrantException("Could not authenticate user: " + userid);
		}
		OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
		return new OAuth2Authentication(storedOAuth2Request, userAuth);
	}

	/**
	 * 请求企业微信获取token
	 * 
	 * @param company
	 * @return
	 * @throws Exception
	 */
	private WorkWeixinAccessToken getFromWeixin(Company company) throws Exception {
		CloseableHttpClient httpclient = HttpClients.custom().build();
		HttpUriRequest get = RequestBuilder.get().setUri(new URI(gettokenUri))
				.addParameter("corpid", company.getCorpid()).addParameter("corpsecret", company.getSecret()).build();

		CloseableHttpResponse response = httpclient.execute(get);
		try {
			HttpEntity entity = response.getEntity();// 中文乱码
			System.out.println(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				String body = EntityUtils.toString(entity, "utf-8");
				System.out.println(body);
				Map<String, Object> map = JSON.parseObject(body, Map.class);
				int errcode = Integer.parseInt(map.get("errcode").toString());
				String errmsg = map.get("errmsg").toString();
				Assert.isTrue(errcode == 0, gettokenUri + "[corpid:" + company.getCorpid() + "][corpsecret:"
						+ company.getSecret() + "]获取企业微信accsstoken错误返回:" + body);
				String access_token = map.get("access_token").toString();
				int expires_in = Integer.parseInt(map.get("expires_in").toString());
				return new WorkWeixinAccessToken(access_token, expires_in);
			} else {
				throw new Exception("获取企业微信accsstoken错误返回:" + response.getStatusLine().getStatusCode());
			}

		} finally {
			response.close();
		}
	}

	/**
	 * 从redis获取暂存的企业微信ticket
	 * 
	 * @param key
	 * @return
	 */
	private String getJspapiTicketFromRedis(String key) {
		byte[] serializedKey = serializeKey(WORKWEIXINTICKET + key);
		byte[] bytes = null;
		RedisConnection conn = getConnection();
		try {
			bytes = conn.get(serializedKey);
		} finally {
			conn.close();
		}
		String token = deserializeString(bytes);
		return token;
	}

	/**
	 * 企业微信ticket暂存到redis
	 * 
	 * @param key
	 * @param ticket
	 * @param seconds
	 */
	private void storeJspapiTicket(String key, String ticket, int seconds) {
		byte[] serializedticket = serialize(ticket);
		byte[] accessKey = serializeKey(WORKWEIXINTICKET + key);
		RedisConnection conn = getConnection();
		try {
			conn.openPipeline();
			conn.set(accessKey, serializedticket);
			conn.expire(accessKey, seconds);
			conn.closePipeline();
		} finally {
			conn.close();
		}
	}

	/**
	 * 从redis获取暂存的企业微信token
	 * 
	 * @param key
	 * @return
	 */
	private String getAccessToken(String key) {
		byte[] serializedKey = serializeKey(WORKWEIXINACESS + key);
		byte[] bytes = null;
		RedisConnection conn = getConnection();
		try {
			bytes = conn.get(serializedKey);
		} finally {
			conn.close();
		}
		String token = deserializeString(bytes);
		return token;
	}

	/**
	 * 企业微信token暂存到redis
	 * 
	 * @param key
	 * @param token
	 * @param seconds
	 */
	private void storeAccessToken(String key, String token, int seconds) {
		byte[] serializedAccessToken = serialize(token);
		byte[] accessKey = serializeKey(WORKWEIXINACESS + key);
		RedisConnection conn = getConnection();
		try {
			conn.openPipeline();
			conn.set(accessKey, serializedAccessToken);
			conn.expire(accessKey, seconds);
			conn.closePipeline();
		} finally {
			conn.close();
		}
	}

	/**
	 * 移除redis的企业微信token
	 * 
	 * @param corpid
	 */
	public void removeAccessToken(String corpid) {
		byte[] accessKey = serializeKey(WORKWEIXINACESS + corpid);
		RedisConnection conn = getConnection();
		try {
			conn.openPipeline();
			conn.get(accessKey);
			conn.del(accessKey);
		} finally {
			conn.close();
		}
	}

	/**
	 * 获取redis connection
	 * 
	 * @return
	 */
	private RedisConnection getConnection() {
		return connectionFactory.getConnection();
	}

	/**
	 * 序列化key
	 * 
	 * @param object
	 * @return
	 */
	private byte[] serializeKey(String object) {
		return serialize(prefix + object);
	}

	/**
	 * 序列化字符串
	 * 
	 * @param string
	 * @return
	 */
	private byte[] serialize(String string) {
		return serializationStrategy.serialize(string);
	}

	/**
	 * 反序列化为字符串
	 * 
	 * @param bytes
	 * @return
	 */
	private String deserializeString(byte[] bytes) {
		return serializationStrategy.deserializeString(bytes);
	}

	/**
	 * 获取oauth client
	 * 
	 * @param clientId
	 * @return
	 */
	public ClientDetails loadClientByClientId(String clientId) {
		return clientDetailsService.loadClientByClientId(clientId);
	}

	private OAuth2RequestFactory getRequestFactory() {
		if (requestFactory != null) {
			return requestFactory;
		}
		requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
		return requestFactory;
	}

	public OAuth2RequestFactory getOAuth2RequestFactory() {
		return ProxyCreator.getProxy(OAuth2RequestFactory.class, new ObjectFactory<OAuth2RequestFactory>() {
			@Override
			public OAuth2RequestFactory getObject() throws BeansException {
				return getRequestFactory();
			}
		});
	}

	/**
	 * 微信登录认证
	 * 
	 * @param authentication
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
		String name = authentication.getName();
		UserDetails detail = userDetailsService.loadUserByUsername(name);
		return createSuccessAuthentication(detail, authentication, detail);
	}

	/**
	 * 封装返回的Authentication
	 * 
	 * @param principal
	 * @param authentication
	 * @param user
	 * @return
	 */
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
			UserDetails user) {
		// Ensure we return the original credentials the user supplied,
		// so subsequent attempts are successful even with encoded passwords.
		// Also ensure we return the original getDetails(), so that future
		// authentication events after cache expiry contain the details
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal,
				authentication.getCredentials(), authoritiesMapper.mapAuthorities(user.getAuthorities()));
		result.setDetails(authentication.getDetails());

		return result;
	}

}
