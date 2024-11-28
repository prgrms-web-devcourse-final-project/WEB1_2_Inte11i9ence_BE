package com.prgrmsfinal.skypedia.member.exception;

public enum MemberError {
    NOT_FOUND("NOT_FOUND", 404),
    DUPLICATE("DUPLICATE", 409),
    INVALID("INVALID", 400),
    BAD_CREDENTIALS("BAD_CREDENTIALS", 401);

    private MemberException memberException;

    MemberError(String message, int code) {
        memberException = new MemberException(message, code);
    }

    public MemberException get(){
        return memberException;
    }
}
