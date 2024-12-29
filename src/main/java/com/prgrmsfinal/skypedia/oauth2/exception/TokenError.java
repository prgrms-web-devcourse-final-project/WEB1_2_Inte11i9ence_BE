package com.prgrmsfinal.skypedia.oauth2.exception;

public enum TokenError {
    ACCESS_TOKEN_NOT_FOUND("NOT_FOUND", 404),
    REFRESH_TOKEN_NOT_FOUND("NOT_FOUND", 404),
    INVALID_TOKEN("INVALID_TOKEN", 401),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED", 401),;

    private TokenException tokenException;

    TokenError(String message, Integer code) {
        this.tokenException = new TokenException(message, code);
    }

    public TokenException get() {
        return tokenException;
    }
}
