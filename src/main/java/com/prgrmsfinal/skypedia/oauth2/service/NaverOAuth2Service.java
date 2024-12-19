package com.prgrmsfinal.skypedia.oauth2.service;


import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.Role;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.oauth2.dto.TokenResponse;
import com.prgrmsfinal.skypedia.oauth2.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

// OAuth2 인증 서비스
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NaverOAuth2Service {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public TokenResponse authenticateNaverUser(Map<String, Object> attributes) {
        log.info("Naver attributes: {}", attributes);

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        String oauthId = (String) response.get("id");
        String email = (String) response.get("email");
        String name = (String) response.get("name");
        String profileImage = (String) response.get("profile_image");

        if (email == null || name == null || oauthId == null) {
            log.error("Required user info is missing. id: {}, email: {}, name: {}",
                    oauthId, email, name);
            throw new RuntimeException("Failed to get user info from Naver");
        }

        Member member = memberRepository.findByOauthId(oauthId);
        if (member == null) {
            member = Member.builder()
                    .oauthId(oauthId)
                    .email(email)
                    .name(name)
                    .username(generateRandomUsername())
                    .profileImage(profileImage != null ? profileImage : "default_image_url")
                    .role(Role.ROLE_USER)
                    .withdrawn(false)
                    .build();
            member = memberRepository.save(member);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member);
        String refreshToken = jwtTokenProvider.createRefreshToken(member);

        refreshTokenService.saveRefreshToken(member, refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    private Member createNaverMember(String oauthId, String email, String name, String profileImage) {
        Member member = Member.builder()
                .oauthId(oauthId)
                .email(email)
                .name(name)
                .username(generateRandomUsername())
                .role(Role.ROLE_USER)
                .profileImage(profileImage)
                .withdrawn(false)
                .build();

        return memberRepository.save(member);
    }

    private String generateRandomUsername() {
        return "User" + UUID.randomUUID().toString().substring(0, 8);
    }
}