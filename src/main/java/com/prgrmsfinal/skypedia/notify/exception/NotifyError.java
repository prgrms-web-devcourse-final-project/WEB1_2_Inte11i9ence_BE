package com.prgrmsfinal.skypedia.notify.exception;

import org.springframework.http.HttpStatus;

import com.prgrmsfinal.skypedia.global.exception.CommonException;

public enum NotifyError {
	UNAUTHORIZED_READ_ALL(HttpStatus.UNAUTHORIZED, "알림 조회는 회원만 사용 가능합니다."),
	UNAUTHORIZED_CHECK(HttpStatus.UNAUTHORIZED, "알림 읽음 처리는 회원만 사용 가능합니다."),
	NOT_FOUND_NOTIFY_CHECK(HttpStatus.NOT_FOUND, "해당 알림이 존재하지 않거나 읽음 처리되어있습니다."),
	NOT_FOUND_NOTIFY_CHECKS(HttpStatus.NOT_FOUND, "읽지 않은 알림이 존재하지 않습니다."),
	NOT_FOUND_NOTIFIES(HttpStatus.NOT_FOUND, "새로운 알림이 존재하지 않습니다.");

	private final CommonException commonException;

	NotifyError(HttpStatus code, String message) {
		commonException = new CommonException(code, message);
	}

	public CommonException getException() {
		return commonException;
	}
}
