package com.neofoc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.neofoc.app", "com.neofoc.app.controller", "com.neofoc.springboot", "com.neofoc.app.modules.labotron"})
public class LabotronApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SpringApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(LabotronApplication.class, args);
	}

}
