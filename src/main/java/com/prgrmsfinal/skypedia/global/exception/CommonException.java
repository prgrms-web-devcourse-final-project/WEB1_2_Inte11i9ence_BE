package com.prgrmsfinal.skypedia.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonException extends RuntimeException {
	private HttpStatus code;
	private String message;
}
