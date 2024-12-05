package com.prgrmsfinal.skypedia.oauth2.jwt;

import com.prgrmsfinal.skypedia.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {   //로그인 성공시 토큰 쿠키에 저장하고 리다이렉팅하는 클래스

    @Value("${frontend.url}")
    private String frontendUrl;

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String oauthId = customUserDetails.getOauthId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(oauthId, role, 60*60*60L);

        response.addCookie(createCookie("Authorization", token));

        // 현재 도메인 로그로 출력
        String domain = request.getServerName();
        logger.info("Current domain: " + domain);

        // 쿠키에 저장된 토큰 값 로그로 출력
        logger.info("Stored token in cookie: " + token);
        response.sendRedirect(frontendUrl);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        cookie.setAttribute("SameSite","None");
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(false);

        return cookie;
    }
}

