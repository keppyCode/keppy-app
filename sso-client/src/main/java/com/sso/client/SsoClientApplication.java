package com.sso.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.InetAddress;


@SpringBootApplication
public class SsoClientApplication {
	private static final Logger log = LoggerFactory.getLogger(SsoClientApplication.class);
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext run = SpringApplication.run(SsoClientApplication.class, args);
		TomcatServletWebServerFactory tomcatServletWebServerFactory = (TomcatServletWebServerFactory) run.getBean("tomcatServletWebServerFactory");
		String host = InetAddress.getLocalHost().getHostAddress();
		int port = tomcatServletWebServerFactory.getPort();
		String contextPath = tomcatServletWebServerFactory.getContextPath();
		System.out.println("############################################################");
		System.out.println("本机访问地址: http://" + host + ":" + port + contextPath + "/");
		System.out.println("本机授权码模式获取token url: http://" + host + ":" + port + contextPath + "/page/getTokenForAuthorizationCode.html");
		System.out.println("############################################################");
	}

	@GetMapping("/user")
	public Authentication getUser(Authentication authentication) {
		log.info("resource: user {}", authentication);
		return authentication;
	}
}
