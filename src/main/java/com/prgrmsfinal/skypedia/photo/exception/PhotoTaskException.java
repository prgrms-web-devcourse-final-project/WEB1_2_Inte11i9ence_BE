package com.prgrmsfinal.skypedia.photo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PhotoTaskException extends RuntimeException {
	private int code;
	private String message;
}