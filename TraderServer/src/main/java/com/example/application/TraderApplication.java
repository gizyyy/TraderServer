package com.example.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EntityScan("com.example.persistence.*")
@ServletComponentScan
@EnableWebMvc
@EnableJpaRepositories("com.example.persistence.*")
@SpringBootApplication(scanBasePackageClasses = TradeResource.class, scanBasePackages = { "com.example.application",
		"com.example.business", "com.example.business.events", "com.example.persistence.entitiy",
		"com.example.persistence.repository" })
public class TraderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TraderApplication.class, args);

	}

}
