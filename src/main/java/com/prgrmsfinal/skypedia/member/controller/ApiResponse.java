package com.prgrmsfinal.skypedia.member.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T result;

    public static <T> ApiResponse<T> success(String message, T result) {
        return new ApiResponse<>(message, result);
    }

    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(message, null);
    }
}
