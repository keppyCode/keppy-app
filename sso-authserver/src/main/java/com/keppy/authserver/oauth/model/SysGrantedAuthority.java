package com.keppy.authserver.oauth.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class SysGrantedAuthority implements GrantedAuthority {

    /**
     * 权限
     */
     private String authority;
    @Override
    public String getAuthority() {
        return "ADMIN";
    }
}
