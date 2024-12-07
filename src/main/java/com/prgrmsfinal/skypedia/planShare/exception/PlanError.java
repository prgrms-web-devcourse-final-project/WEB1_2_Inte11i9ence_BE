package com.prgrmsfinal.skypedia.planShare.exception;

import org.springframework.http.HttpStatus;

import com.prgrmsfinal.skypedia.global.exception.CommonException;

public enum PlanError {
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 접근입니다"),
	NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"),
	NOT_REGISTERED(HttpStatus.UNAUTHORIZED, "게시글을 등록할 수 없습니다"),
	NOT_MODIFIED(HttpStatus.NOT_MODIFIED, "게시글을 수정할 수 없습니다"),
	NOT_REMOVED(HttpStatus.BAD_REQUEST, "게시글을 삭제할 수 없습니다"),
	NOT_FETCHED(HttpStatus.NOT_MODIFIED, "게시글을 조회할 수 없습니다"),
	NOT_FOUND_REPLIES(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),
	UNAUTHORIZED_CREATE(HttpStatus.UNAUTHORIZED, "게시글 작성 권한이 없습니다."),
	UNAUTHORIZED_CREATE_REPLY(HttpStatus.UNAUTHORIZED, "게시글에 댓글 작성 권한이 없습니다."),
	UNAUTHORIZED_MODIFY(HttpStatus.UNAUTHORIZED, "게시글 수정 권한이 없습니다."),
	UNAUTHORIZED_DELETE(HttpStatus.UNAUTHORIZED, "게시글 삭제 권한이 없습니다."),
	UNAUTHORIZED_TOGGLE_LIKES(HttpStatus.UNAUTHORIZED, "좋아요 기능은 회원만 사용 가능합니다."),
	UNAUTHORIZED_TOGGLE_SCRAP(HttpStatus.UNAUTHORIZED, "스크랩 기능은 회원만 사용 가능합니다.");

	private final CommonException commonException;

	PlanError(HttpStatus code, String message) {
		commonException = new CommonException(code, message);
	}

	public CommonException getException() {
		return commonException;
	}
}
