package com.prgrmsfinal.skypedia.global.dto;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ErrorResponseDTO {
	@Getter
	@AllArgsConstructor
	@Builder
	public static class Valid {
		private final HttpStatus code;

		private final String message;

		private final Map<String, String> details;
	}
}
