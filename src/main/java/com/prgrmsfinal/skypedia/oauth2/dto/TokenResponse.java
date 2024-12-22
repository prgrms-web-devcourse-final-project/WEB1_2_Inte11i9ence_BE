package com.prgrmsfinal.skypedia.oauth2.dto;

import lombok.*;

// JWT 토큰 응답 DTO
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}