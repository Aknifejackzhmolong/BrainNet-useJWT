package com.brainsci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableJpaAuditing(auditorAwareRef = "auditorAware") //开启审计功能
@EnableAsync
public class BrainSciApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrainSciApplication.class, args);
	}
}
