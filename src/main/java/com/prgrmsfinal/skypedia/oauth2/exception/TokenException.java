package com.prgrmsfinal.skypedia.oauth2.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenException extends RuntimeException {
    private String message;
    private int code;

}