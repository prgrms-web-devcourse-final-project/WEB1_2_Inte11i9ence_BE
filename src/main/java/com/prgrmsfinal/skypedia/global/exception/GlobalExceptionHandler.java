package com.prgrmsfinal.skypedia.global.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.prgrmsfinal.skypedia.global.dto.ErrorResponseDTO;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(CommonException.class)
	public ResponseEntity<Map<String, Object>> handleCommonException(CommonException e) {
		return ResponseEntity.status(e.getCode()).body(Map.of("status", e.getCode(), "message", e.getMessage()));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponseDTO.Valid handleConstraintViolationException(ConstraintViolationException e) {
		Map<String, String> details = new HashMap<>();

		e.getConstraintViolations().forEach(violation -> {
			String fieldName = violation.getPropertyPath().toString();
			String message = violation.getMessage();
			details.put(fieldName, message);
		});

		return new ErrorResponseDTO.Valid(HttpStatus.BAD_REQUEST, "잘못된 요청 파라미터가 감지되었습니다.", details);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponseDTO.Valid handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		Map<String, String> details = new HashMap<>();

		e.getBindingResult().getAllErrors().forEach(error -> {
			String field = ((FieldError)error).getField();
			String message = error.getDefaultMessage();
			details.put(field, message);
		});

		return new ErrorResponseDTO.Valid(HttpStatus.BAD_REQUEST, "잘못된 요청 데이터가 감지되었습니다.", details);
	}

	@ExceptionHandler(TypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponseDTO.Valid handleTypeMismatchException(TypeMismatchException e) {
		return new ErrorResponseDTO.Valid(HttpStatus.BAD_REQUEST, "필드 타입에 위배되는 값을 감지했습니다.",
			Map.of("field", e.getPropertyName(), "message", "필드 타입에 맞게 값을 수정해주세요."));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
		return Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR, "message", "서버 내부에서 에러가 발생했습니다. 관라자에게 문의하세요.");
	}
}
