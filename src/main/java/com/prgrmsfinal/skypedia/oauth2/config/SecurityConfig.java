package com.prgrmsfinal.skypedia.oauth2.config;


import java.util.Collections;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

						// Set-Cookie를 제거하고 Authorization을 추가
						configuration.setExposedHeaders(Collections.singletonList("Authorization"));

						return configuration;
					}
				}))
				.csrf((auth) -> auth.disable()) //csrf disable

				.formLogin((auth) -> auth.disable()) //From 로그인 방식 disable

				.httpBasic((auth) -> auth.disable()) //HTTP Basic 인증 방식 disable

				.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class) //JWT 필터 추가

				.oauth2Login((oauth2) -> oauth2
						.userInfoEndpoint((userInfoEndpointConfig -> userInfoEndpointConfig
								.userService(customOAuth2UserService)))
						.successHandler(customSuccessHandler)) //OAuth2 로그인 설정

				.authorizeHttpRequests((auth) -> auth
						.requestMatchers("/", "/login", "/logout",
								"/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
								"/webjars/**", "/actuator/**", "/oauth2/authorization/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/post/**", "/api/v1/reply/**", "/api/v1/photo/**", "/api/v1/region/**").permitAll()
						.anyRequest()
						.authenticated()) // 경로별 인가 설정

				.sessionManagement((session) -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless로 설정

				.logout(logout -> logout
						.logoutUrl("/logout")
						.invalidateHttpSession(false) // 세션 무효화 비활성화
						.clearAuthentication(true) // SecurityContext 초기화
						.deleteCookies("Authorization")); // JWT 쿠키 삭제

		return http.build();
	}
}