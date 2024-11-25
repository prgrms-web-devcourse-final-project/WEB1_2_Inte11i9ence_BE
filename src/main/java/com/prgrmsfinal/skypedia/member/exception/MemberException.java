package com.prgrmsfinal.skypedia.member.exception;

public enum MemberException {
    NOT_FOUND("NOT_FOUND", 404),
    DUPLICATE("DUPLICATE", 409),
    INVALID("INVALID", 400),
    BAD_CREDENTIALS("BAD_CREDENTIALS", 401);

    private MemberTaskException memberTaskException;

    MemberException(String message, int code) {
        memberTaskException = new MemberTaskException(message, code);
    }

    public MemberTaskException get(){
        return memberTaskException;
    }
}
