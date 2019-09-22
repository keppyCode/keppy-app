package com.keppy.authserver.oauth.config;

import com.keppy.authserver.oauth.serverice.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MyUserDetailsService userDetailsFitService;
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .anyRequest().authenticated().antMatchers("/oauth/**").permitAll()
            .and()
            .formLogin()
            .and()
            .httpBasic();
        http.headers().cacheControl();
    }



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsFitService).passwordEncoder(passwordEncoder());
        auth.parentAuthenticationManager(authenticationManagerBean());
    }

    /**
     * 密码加密凡方式与验证
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        //return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        //尝试AuthorizationServerConfig使用这个简单的编码器从您的班级更改您的密码编码器（不会加密密码）。因为没有通过加密将您的客户端密钥保存在InMemory存储中
        return new PasswordEncoder() {
            //加密方式
            public String encode (CharSequence charSequence) {
                return charSequence.toString();
            }
            //密码验证
            public boolean matches(CharSequence charSequence, String s) {
                return true;
            }
        };
    }

}