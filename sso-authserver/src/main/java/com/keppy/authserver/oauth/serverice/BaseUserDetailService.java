package com.keppy.authserver.oauth.serverice;

import com.keppy.authserver.oauth.model.SysUserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;

/**
 * # security 登陆认证 MyUserDetailsService
 */
@Service
public class BaseUserDetailService implements UserDetailsService{
    private static final Logger log = LoggerFactory.getLogger(BaseUserDetailService.class);
    /**
     * 认证实现类
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(username);
        SysUserAuthentication user = null;
        if ("admin".equalsIgnoreCase(username)) {
            IntegrationAuthentication auth = IntegrationAuthenticationContext.get();
            User user = mockUser();
            return user;
        }
        return null;
    }

    /**
     * 认证用户封装
     * @return
     */
    private User mockUser() {
        Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("admin"));
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String pwd = passwordEncoder.encode("123456");
        User user = new User("admin",pwd,authorities);
        return user;
    }
}
