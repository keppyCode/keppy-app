package com.keppy.authserver.oauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // **代表所有路径
        registry.addMapping("/**")
                // allowOrigin指可以通过的ip，*代表所有，可以使用指定的ip，多个的话可以用逗号分隔，默认为*
                .allowedOrigins("*")
                // 指请求方式 默认为*
                .allowedMethods("GET", "POST", "HEAD", "PUT", "DELETE", "OPTIONS")
                // 支持证书，默认为true
                .allowCredentials(false)
                // 最大过期时间，默认为-1
                .maxAge(3600)
                .allowedHeaders("*");
    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        //自定义的登陆页面
//        registry.addViewController("/login").setViewName("login");
//        //自定义的授权页面
//        registry.addViewController("/oauth/confirm_access").setViewName("oauth");
//
//    }
}
