package com.multiplos.cuentas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfigCors {
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				.allowedMethods("GET","POST","PUT")
				.allowedHeaders("*")
				.allowedOrigins("http://localhost:8080/",
								"http://localhost:4200/",
								"https://multiplofront.azurewebsites.net",
								"https://multiplolenders.com",
								"https://multiplolendersqa.azurewebsites.net",
								"https://api.contifico.com"
								);
			}
		};
	}

}
