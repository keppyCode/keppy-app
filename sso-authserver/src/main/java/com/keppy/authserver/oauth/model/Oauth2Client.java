package com.keppy.authserver.oauth.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * 注册模型
 */
public class Oauth2Client {
    private int id;
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String grantType;
    private String scope;
    List<SysGrantedAuthority> list = new ArrayList<>();
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<SysGrantedAuthority> getList() {
        return list;
    }

    public void setList(List<SysGrantedAuthority> list) {
        this.list = list;
    }
}
