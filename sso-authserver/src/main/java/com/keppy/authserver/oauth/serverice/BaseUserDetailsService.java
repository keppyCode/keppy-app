package com.keppy.authserver.oauth.serverice;

import com.keppy.authserver.oauth.model.Oauth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用于用户身份认证
 */
@Component
public class BaseUserDetailsService implements UserDetailsService {

    @Autowired
    Oauth2Service oauth2Service;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Oauth2User> users = oauth2Service.getOauth2UserByUsername(username);
        if (users == null || users.size() == 0) {
            throw new UsernameNotFoundException("username无效");
        }
        Oauth2User user = users.get(0);
        //String passwordAfterEncoder = passwordEncoder.encode(user.getPassword());
        return User.withUsername(username).password(user.getPassword()).authorities(user.getList()).build();
    }
}
