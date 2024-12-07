package com.prgrmsfinal.skypedia.oauth2.Controller;

import com.prgrmsfinal.skypedia.oauth2.dto.TokenResponse;
import com.prgrmsfinal.skypedia.oauth2.service.NaverOAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

//OAuth2 컨트롤러
    @RestController
    @RequestMapping("/login/oauth2/code")
    @RequiredArgsConstructor
    @Slf4j
    public class NaverOAuth2Controller {
        private final NaverOAuth2Service naverOAuth2Service;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @GetMapping("/naver")
    public ResponseEntity<TokenResponse> naverCallback(
            @RequestParam String code,
            @RequestParam String state
    ) throws Exception {
        // 네이버 액세스 토큰 요청
        String naverAccessToken = requestNaverAccessToken(code, state);

        // 네이버 사용자 정보 조회
        Map<String, Object> userAttributes = requestNaverUserInfo(naverAccessToken);

        // 프로젝트 토큰 발급
        TokenResponse tokenResponse = naverOAuth2Service.authenticateNaverUser(userAttributes);

        return ResponseEntity.ok(tokenResponse);
    }

    private String requestNaverAccessToken(String code, String state) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://nid.naver.com/oauth2.0/token",
                request,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    private Map<String, Object> requestNaverUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                request,
                Map.class
        );

        return (Map<String, Object>) response.getBody().get("response");
    }
}