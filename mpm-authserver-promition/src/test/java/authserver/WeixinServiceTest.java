package authserver;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.mpm.foundation.authserver.Application;
import com.mpm.foundation.authserver.service.WorkweixinService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class WeixinServiceTest {
	@Autowired
	private WorkweixinService service;

	@Test
	public void test() throws Exception {
		ClientDetails client = service.loadClientByClientId("sso-client");
		Map<String, String> requestParameters = new HashMap<>();
		requestParameters.put("companycode", "alphaeta");
		requestParameters.put("userid", "13926042805");
		//TokenRequest tokenRequest = service.getOAuth2RequestFactory().createTokenRequest(requestParameters, client);
		//OAuth2AccessToken token = service.grant(tokenRequest);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");

		//System.out.println(JSON.toJSONString(new ResponseEntity<OAuth2AccessToken>(token, headers, HttpStatus.OK)));
	}

}
