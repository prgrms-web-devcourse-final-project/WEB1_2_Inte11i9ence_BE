package com.prgrmsfinal.skypedia.oauth2.Controller;

import com.prgrmsfinal.skypedia.oauth2.dto.TokenRefreshRequest;
import com.prgrmsfinal.skypedia.oauth2.dto.TokenResponse;
import com.prgrmsfinal.skypedia.oauth2.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RefreshTokenService refreshTokenService;

    // 토큰 갱신 엔드포인트
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestBody @Valid TokenRefreshRequest request) {
        return ResponseEntity.ok(
                refreshTokenService.refreshAccessToken(request.getRefreshToken())
        );
    }
}