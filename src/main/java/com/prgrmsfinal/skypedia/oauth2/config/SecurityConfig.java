package com.prgrmsfinal.skypedia.oauth2.config;


import java.util.Collections;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.prgrmsfinal.skypedia.oauth2.jwt.CustomSuccessHandler;
import com.prgrmsfinal.skypedia.oauth2.jwt.JWTFilter;
import com.prgrmsfinal.skypedia.oauth2.jwt.JWTUtil;
import com.prgrmsfinal.skypedia.oauth2.service.CustomOAuth2UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomSuccessHandler customSuccessHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final JWTUtil jwtUtil;

	@Value("${frontend.url}")
	private String frontendUrl;
	@Value("${backend.url}")
	private String backendUrl;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

				@Override
				public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

					CorsConfiguration configuration = new CorsConfiguration();

					configuration.setAllowedOrigins(Collections.singletonList(frontendUrl));
					configuration.setAllowedMethods(Collections.singletonList("*"));
					configuration.setAllowCredentials(true);
					configuration.setAllowedHeaders(Collections.singletonList("*"));
					configuration.setMaxAge(3600L);

					configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
					configuration.setExposedHeaders(Collections.singletonList("Authorization"));


					return configuration;
				}
			}));
		//csrf disable
		http
			.csrf((auth) -> auth.disable());

		//From 로그인 방식 disable
		http
			.formLogin((auth) -> auth.disable());

		//HTTP Basic 인증 방식 disable
		http
			.httpBasic((auth) -> auth.disable());

		http
			.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

		//oauth2
		http
			.oauth2Login((oauth2) -> oauth2
				.userInfoEndpoint((userInfoEndpointConfig -> userInfoEndpointConfig
					.userService(customOAuth2UserService)))
				.successHandler(customSuccessHandler));

		//경로별 인가 작업
		http
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/", "/login","/logout", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
					"/webjars/**", "/actuator/**","/oauth2/authorization/**")
				.permitAll()
				.anyRequest()
				.authenticated());

		//세션 설정 : STATELESS
		http
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.logout(logout -> logout
			.logoutUrl("/logout")
			.invalidateHttpSession(false)  // 세션 무효화 비활성화 (stateless 방식에선 필요 없음)
			.clearAuthentication(true)  // SecurityContext 초기화
			.deleteCookies("Authorization"));  // JWT가 저장된 쿠키 삭제

		return http.build();
	}
}
