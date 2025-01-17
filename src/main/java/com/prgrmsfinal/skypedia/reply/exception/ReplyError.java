package com.prgrmsfinal.skypedia.reply.exception;

import org.springframework.http.HttpStatus;

import com.prgrmsfinal.skypedia.global.exception.CommonException;

public enum ReplyError {
	UNAUTHORIZED_TOGGLE_LIKES(HttpStatus.UNAUTHORIZED, "좋아요 기능은 회원만 사용 가능합니다."),
	UNAUTHORIZED_MODIFY(HttpStatus.UNAUTHORIZED, "댓글 수정 권한이 없습니다."),
	UNAUTHORIZED_DELETE(HttpStatus.UNAUTHORIZED, "댓글 삭제 권한이 없습니다."),
	UNAUTHORIZED_RESTORE(HttpStatus.UNAUTHORIZED, "댓글 복구 권한이 없습니다."),
	BAD_REQUEST_RESTORE(HttpStatus.UNAUTHORIZED, "댓글이 이미 복구되었거나 삭제되지 않았습니다."),
	NOT_FOUND_RESTORE(HttpStatus.UNAUTHORIZED, "댓글이 영구적으로 삭제되었거나 존재하지 않습니다."),
	NOT_FOUND_REPLY(HttpStatus.NOT_FOUND, "댓글이 삭제되었거나 존재하지 않습니다."),
	NOT_FOUND_REPLIES(HttpStatus.NOT_FOUND, "댓글 목록이 존재하지 않습니다."),
	NOT_FOUND_PARENT_REPLY(HttpStatus.NOT_FOUND, "부모 댓글이 삭제되었거나 존재하지 않습니다.");

	private final CommonException commonException;

	ReplyError(HttpStatus code, String message) {
		commonException = new CommonException(code, message);
	}

	public CommonException getException() {
		return commonException;
	}
}
