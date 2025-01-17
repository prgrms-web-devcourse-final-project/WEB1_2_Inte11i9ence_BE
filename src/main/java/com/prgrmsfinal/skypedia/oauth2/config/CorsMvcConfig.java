package com.prgrmsfinal.skypedia.oauth2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {
    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .exposedHeaders("Authorization") // JSON 응답에서 Authorization 헤더를 expose
                .allowCredentials(true)  // 여전히 CORS가 가능한 경우, 필요 시 true로 설정
                .allowedOrigins(frontendUrl);
    }
}