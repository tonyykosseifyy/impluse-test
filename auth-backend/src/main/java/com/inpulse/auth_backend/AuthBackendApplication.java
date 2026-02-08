package com.inpulse.auth_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AuthBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthBackendApplication.class, args);
	}
}
