package com.prgrmsfinal.skypedia.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@OpenAPIDefinition
@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI().info(new Info()
			.title("Skypedia API 문서")
			.version("0.0.1")
			.description("프로젝트 Skypedia의 REST API를 설명하는 문서입니다."));
	}
}
