package com.keppy.authserver.oauth.serverice;

import com.keppy.authserver.oauth.model.Oauth2Client;
import com.keppy.authserver.oauth.model.SysGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 注册客户端认证
 */
@Component
public class BaseClientDetailService implements ClientDetailsService {

    @Autowired
    Oauth2Service oauth2Service;
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

            List<Oauth2Client> clients1 = oauth2Service.getOauth2ClientByClientId(clientId);
            if (clients1 == null || clients1.size() == 0) {
                throw new ClientRegistrationException("clientId无效");
            }
            Oauth2Client client = clients1.get(0);
            //String clientSecretAfterEncoder = passwordEncoder.encode(client.getClientSecret());
            BaseClientDetails clientDetails = new BaseClientDetails();
            clientDetails.setClientId(client.getClientId());
            clientDetails.setClientSecret(client.getClientSecret());
            clientDetails.setRegisteredRedirectUri(new HashSet<>(Arrays.asList(client.getRedirectUrl().split(","))));
            clientDetails.setAuthorizedGrantTypes(Arrays.asList(client.getGrantType().split(",")));
            //设置应用范围
            clientDetails.setScope(Arrays.asList(client.getScope().split(",")));
            //设置应用授权范围自动确认
            clientDetails.isAutoApprove(client.getScope());
            clientDetails.setAuthorities(client.getList());
            return clientDetails;
    }
}
