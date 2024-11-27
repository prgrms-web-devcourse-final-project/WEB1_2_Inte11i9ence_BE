package com.prgrmsfinal.skypedia.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberException extends RuntimeException {
    private String message;
    private int code;

}
