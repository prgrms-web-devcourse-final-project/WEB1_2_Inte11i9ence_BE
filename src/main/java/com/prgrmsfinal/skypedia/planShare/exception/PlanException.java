package com.prgrmsfinal.skypedia.planShare.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlanException extends RuntimeException {
	private String message;
	private int code;
}
