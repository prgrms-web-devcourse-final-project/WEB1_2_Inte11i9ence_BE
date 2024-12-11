package com.prgrmsfinal.skypedia.oauth2.service;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.oauth2.dto.TokenResponse;
import com.prgrmsfinal.skypedia.oauth2.entity.RefreshToken;
import com.prgrmsfinal.skypedia.oauth2.exception.TokenError;
import com.prgrmsfinal.skypedia.oauth2.jwt.JwtTokenProvider;
import com.prgrmsfinal.skypedia.oauth2.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public void saveRefreshToken(Member member, String token) {

        LocalDateTime expiryDate = LocalDateTime.now()
                .plusSeconds(jwtTokenProvider.getRefreshTokenValidityInSeconds());

        refreshTokenRepository.findByMemberId(member.getId())
                .ifPresentOrElse(
                        refreshToken -> refreshToken.updateToken(token,expiryDate),
                        () -> refreshTokenRepository.save(new RefreshToken(member,token,expiryDate))
                );
    }

    // 리프레시 토큰으로 새 액세스 토큰 발급
    public TokenResponse refreshAccessToken(String refreshToken) {
        // 리프레시 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw TokenError.INVALID_TOKEN.get();
        }

        // DB에서 리프레시 토큰 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(TokenError.REFRESH_TOKEN_NOT_FOUND::get);

        // 토큰 만료 확인
        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw TokenError.REFRESH_TOKEN_EXPIRED.get();
        }

        // 새 토큰 발급
        Member member = storedToken.getMember();
        String newAccessToken = jwtTokenProvider.createAccessToken(member);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member);

        // 리프레시 토큰 업데이트
        storedToken.updateToken(newRefreshToken,
                LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenValidityInSeconds()));

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
