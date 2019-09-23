package com.keppy.authserver.oauth.model;

import org.springframework.security.core.GrantedAuthority;

public class SysGrantedAuthority implements GrantedAuthority {

    /**
     * 权限
     */
     private String authority;
    @Override
    public String getAuthority() {
        return null;
    }
}
