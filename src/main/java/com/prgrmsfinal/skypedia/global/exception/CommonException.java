package com.prgrmsfinal.skypedia.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonException extends RuntimeException {
    private int code;
    private String message;
}
