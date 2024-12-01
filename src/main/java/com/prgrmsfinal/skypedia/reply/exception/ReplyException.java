package com.prgrmsfinal.skypedia.reply.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReplyException extends RuntimeException {
	private String message;
	private int code;
}
