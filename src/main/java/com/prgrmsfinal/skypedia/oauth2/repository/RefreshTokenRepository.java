package com.prgrmsfinal.skypedia.oauth2.repository;

import com.prgrmsfinal.skypedia.oauth2.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);    // 토큰 문자열로 조회
    Optional<RefreshToken> findByMemberId(Long memberId); // 회원 ID로 조회
}