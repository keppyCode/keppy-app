package com.keppy.authserver.oauth.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证用户
 */
public class Oauth2User {

    private int id;
    private String username;
    private String password;

    List<SysGrantedAuthority> list = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<SysGrantedAuthority> getList() {
        list.add(new SysGrantedAuthority());
        return list;
    }

    public void setList(List<SysGrantedAuthority> list) {
        this.list = list;
    }
}
