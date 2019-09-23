package com.keppy.authserver.oauth.config;

import com.keppy.authserver.oauth.serverice.MyUserDetailsService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MyUserDetailsService userDetailsFitService;

    private static final String[] AUTH_LIST = {
            // -- swagger ui
            "**/swagger-resources/**", "/swagger-ui.html", "/v2/api-docs", "/webjars/**", "/img/**", "/css/**",
            "/js/**", "/kaptcha/**", "/keyword/**", "/valid/**", "/workweixin/**", "/mpm/**" };


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/","/oauth/**","/login","/health", "/css/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and().logout()
                .deleteCookies("JSESSIONID").addLogoutHandler(new LogoutHandler() {

            @Override
            public void logout(HttpServletRequest request, HttpServletResponse response,
                               Authentication authentication) {
                String userid = null;
                String url = request.getParameter("redirect_uri");
                if (authentication != null) {
                    userid = authentication.getName();
                } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                            .getAuthentication().getPrincipal();
                    userid = userDetails.getUsername();
                }
                request.getSession().invalidate();
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(null);
                SecurityContextHolder.clearContext();
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie ck : cookies) {
                        ck.setMaxAge(0);
                        ck.setValue(null);
                        response.addCookie(ck);
                    }
                }
                if (StringUtils.isNotBlank(url)) {
                    try {
                        response.sendRedirect(url);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).permitAll();
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
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        //尝试AuthorizationServerConfig使用这个简单的编码器从您的班级更改您的密码编码器（不会加密密码）。因为没有通过加密将您的客户端密钥保存在InMemory存储中
//        return new PasswordEncoder() {
//            //加密方式
//            @Override
//            public String encode (CharSequence charSequence) {
//                return charSequence.toString();
//            }
//            //密码验证
//            @Override
//            public boolean matches(CharSequence charSequence, String s) {
//                return true;
//            }
//        };
    }

}