package com.prgrmsfinal.skypedia.global.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(CommonException.class)
	public ResponseEntity<Map<String, Object>> handleCommonException(CommonException e) {
		return ResponseEntity.status(e.getCode()).body(Map.of("status", e.getCode(), "message", e.getMessage()));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", HttpStatus.BAD_REQUEST
			, "message", "잘못된 요청 데이터가 발견되었습니다."
			, "details", e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList()));
	}
}
