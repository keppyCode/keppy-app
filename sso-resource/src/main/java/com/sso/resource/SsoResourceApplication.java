package com.sso.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@SpringBootApplication
public class SsoResourceApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SsoResourceApplication.class, args);
	}



}
