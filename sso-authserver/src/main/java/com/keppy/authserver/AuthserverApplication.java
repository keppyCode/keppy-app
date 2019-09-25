package com.keppy.authserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@SpringBootApplication
public class AuthserverApplication {
	private static final Logger log = LoggerFactory.getLogger(AuthserverApplication.class);
	public static void main(String[] args) throws UnknownHostException {
		ConfigurableApplicationContext run = SpringApplication.run(AuthserverApplication.class, args);
		TomcatServletWebServerFactory tomcatServletWebServerFactory = (TomcatServletWebServerFactory) run.getBean("tomcatServletWebServerFactory");
		String host = InetAddress.getLocalHost().getHostAddress();
		int port = tomcatServletWebServerFactory.getPort();
		String contextPath = tomcatServletWebServerFactory.getContextPath();
		System.out.println("############################################################");
		System.out.println("本机访问地址: http://" + host + ":" + port + contextPath + "/");
		System.out.println("############################################################");
	}
	@GetMapping("/user")
	public Authentication getUser(Authentication authentication) {
		log.info("resource: user {}", authentication);
		return authentication;
	}
	@GetMapping("/")
	public Authentication index(Authentication authentication) {
		log.info("resource: user {}", authentication);
		return authentication;
	}



}
