package com.sso.client;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationFilter {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigurationFilter.class);
    @Value("${services.auth}")
    private String authService;

//    @Bean
//    public FilterRegistrationBean jwtFilter() {
//        logger.info("##############。。。。。authService："+authService);
//        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(new JwtFilter());
//        registrationBean.setInitParameters(Collections.singletonMap("services.auth", authService));
//        registrationBean.addUrlPatterns("/*");
//
//        return registrationBean;
//    }
}