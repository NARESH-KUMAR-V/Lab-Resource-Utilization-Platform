package com.labplatform.lab_platform_backend;

import com.labplatform.lab_platform_backend.service.JwtService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LabPlatformBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabPlatformBackendApplication.class, args);
	}


}