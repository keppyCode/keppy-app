package com.qunyi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("com.qunyi.modules.dao")
public class SsoServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsoServerApplication.class, args);
	}
}
