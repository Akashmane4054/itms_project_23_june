package com.itms.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = { "com.itms.**", "com.itms.core.**", "com.itms.product.**" })
@EnableJpaRepositories(basePackages = "com.itms.product.repository")
@EntityScan(basePackages = "com.itms.product.domain")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.itms.core.feignclients")
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}