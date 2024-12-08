package com.prgrmsfinal.skypedia.oauth2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrmsfinal.skypedia.oauth2.dto.TokenResponse;
import com.prgrmsfinal.skypedia.oauth2.jwt.JwtAuthenticationFilter;
import com.prgrmsfinal.skypedia.oauth2.jwt.JwtTokenProvider;
import com.prgrmsfinal.skypedia.oauth2.service.GoogleOAuth2Service;
import com.prgrmsfinal.skypedia.oauth2.service.NaverOAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final NaverOAuth2Service naverOAuth2Service;
	private final GoogleOAuth2Service googleOAuth2Service;
	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exceptionHandling -> exceptionHandling
						.authenticationEntryPoint((request, response, authException) -> {
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.setContentType("application/json");
							response.setCharacterEncoding("UTF-8");
							response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \""
									+ authException.getMessage() + "\"}");
						}))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/login/oauth2/code/**", "/oauth2/authorization/**",
								"/error", "/login", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
								"/webjars/**", "/actuator/**",
								"/api/v1/").permitAll()
						.requestMatchers(HttpMethod.GET,
								"/api/v1/posts","/api/v1/plan-group","/api/v1/notify",
								"/api/v1/post-category","/api/v1/plan-detail**",
								"/api/v1/region**","/api/v1/plan-group**",
								"/api/v1/member/{username}","/api/v1/post**",
								"/api/v1/reply**","/api/v1/photo**").permitAll()
						.anyRequest().authenticated())
				.oauth2Login(oauth2 -> oauth2
						.successHandler((request, response, authentication) -> {
							OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
//							TokenResponse tokenResponse = naverOAuth2Service
//									.authenticateNaverUser(oauth2User.getAttributes());
							TokenResponse tokenResponse;

							String registrationId = request.getRequestURI()
									.contains("google") ? "google" : "naver";

							if ("google".equals(registrationId)) {
								tokenResponse = googleOAuth2Service
										.authenticateGoogleUser(oauth2User.getAttributes());
							} else {
								tokenResponse = naverOAuth2Service
										.authenticateNaverUser(oauth2User.getAttributes());
							}
							String redirectUrl = String.format("http://localhost:5173/oauth/callback?token=%s&refreshToken=%s",
									tokenResponse.getAccessToken(),
									tokenResponse.getRefreshToken());
							response.sendRedirect(redirectUrl);
						}))
				.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
						UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(Arrays.asList("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
