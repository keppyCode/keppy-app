package com.keppy.authserver.oauth.config;

import com.keppy.authserver.oauth.serverice.BaseUserDetailsService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    BaseUserDetailsService baseUserDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    MyPasswordEncoder myPasswordEncoder;

    private static final String[] AUTH_LIST = {
            // -- swagger ui
            "**/swagger-resources/**", "/swagger-ui.html", "/v2/api-docs", "/webjars/**", "/img/**", "/css/**",
            "/js/**", "/kaptcha/**", "/keyword/**", "/valid/**", "/workweixin/**", "/mpm/**" };





    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()//禁用了 csrf 功能
                .authorizeRequests().antMatchers("/oauth/token","/oauth/authorize").permitAll()
                //.anyRequest().//其他没有限定的请求，允许访问
                .and().anonymous()//对于没有配置权限的其他请求允许匿名访问
                .and().formLogin()//使用 spring security 默认登录页面
                .and().httpBasic();//启用http 基础验证
//                .and().logout()
//                .deleteCookies("JSESSIONID").addLogoutHandler(new LogoutHandler() {
//
//            @Override
//            public void logout(HttpServletRequest request, HttpServletResponse response,
//                               Authentication authentication) {
//                String userid = null;
//                String url = request.getParameter("redirect_uri");
//                if (authentication != null) {
//                    userid = authentication.getName();
//                } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
//                    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
//                            .getAuthentication().getPrincipal();
//                    userid = userDetails.getUsername();
//                }
//                request.getSession().invalidate();
//                SecurityContext context = SecurityContextHolder.getContext();
//                context.setAuthentication(null);
//                SecurityContextHolder.clearContext();
//                Cookie[] cookies = request.getCookies();
//                if (cookies != null) {
//                    for (Cookie ck : cookies) {
//                        ck.setMaxAge(0);
//                        ck.setValue(null);
//                        response.addCookie(ck);
//                    }
//                }
//                if (StringUtils.isNotBlank(url)) {
//                    try {
//                        response.sendRedirect(url);
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).permitAll();
    }



    /**
     * 用户验证
     * spring security
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(baseUserDetailsService).passwordEncoder(myPasswordEncoder);
        ;
    }



}