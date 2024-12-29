package com.prgrmsfinal.skypedia.oauth2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRefreshRequest {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;  // 리프레시 토큰 요청 시 필요한 DTO
}