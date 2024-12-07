package com.prgrmsfinal.skypedia.oauth2.service;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.Role;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.oauth2.dto.TokenResponse;
import com.prgrmsfinal.skypedia.oauth2.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuth2Service {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponse authenticateGoogleUser(Map<String, Object> attributes) {
        log.info("Google attributes: {}", attributes);

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String profileImage = (String) attributes.get("picture");
        String oauthId = (String) attributes.get("sub");  // Google의 경우 'sub'가 OAuth ID입니다

        Member member = memberRepository.findByOauthId(oauthId);
        if (member == null) {
            member = Member.builder()
                    .email(email)
                    .name(name)
                    .username(generateRandomUsername())
                    .profileImage(profileImage)
                    .oauthId(oauthId)
                    .role(Role.ROLE_USER)
                    .build();
            memberRepository.save(member);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member);
        String refreshToken = jwtTokenProvider.createRefreshToken(member);

        return new TokenResponse(accessToken, refreshToken);
    }

    private String generateRandomUsername() {
        return "User" + UUID.randomUUID().toString().substring(0, 8);
    }
}