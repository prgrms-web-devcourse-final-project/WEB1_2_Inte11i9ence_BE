package com.prgrmsfinal.skypedia.post.exception;

import org.springframework.http.HttpStatus;

import com.prgrmsfinal.skypedia.global.exception.CommonException;

public enum PostError {
	NOT_FOUND_CATEGORY(HttpStatus.BAD_REQUEST, "해당 카테고리는 존재하지 않습니다."),
	NOT_FOUND_POST(HttpStatus.NOT_FOUND, "게시글이 삭제되었거나 존재하지 않습니다."),
	NOT_FOUND_POSTS(HttpStatus.NOT_FOUND, "게시글 목록이 존재하지 않습니다."),
	NOT_FOUND_PERMANENT(HttpStatus.NOT_FOUND, "게시글이 영구적으로 삭제되었거나, 존재하지 않습니다."),
	NOT_FOUND_REPLIES(HttpStatus.NOT_FOUND, "댓글 목록이 존재하지 않습니다."),
	NOT_FOUND_USERNAME(HttpStatus.NOT_FOUND, "해당 회원이 존재하지 않습니다."),
	UNAUTHORIZED_CREATE(HttpStatus.UNAUTHORIZED, "게시글 작성 권한이 없습니다."),
	UNAUTHORIZED_CREATE_REPLY(HttpStatus.UNAUTHORIZED, "게시글에 댓글 작성 권한이 없습니다."),
	UNAUTHORIZED_CREATE_CATEGORY(HttpStatus.UNAUTHORIZED, "게시글 카테고리 생성 권한이 없습니다."),
	UNAUTHORIZED_MODIFY(HttpStatus.UNAUTHORIZED, "게시글 수정 권한이 없습니다."),
	UNAUTHORIZED_DELETE(HttpStatus.UNAUTHORIZED, "게시글 삭제 권한이 없습니다."),
	UNAUTHORIZED_TOGGLE_LIKES(HttpStatus.UNAUTHORIZED, "좋아요 기능은 회원만 사용 가능합니다."),
	UNAUTHORIZED_TOGGLE_SCRAP(HttpStatus.UNAUTHORIZED, "스크랩 기능은 회원만 사용 가능합니다."),
	UNAUTHORIZED_RESTORE(HttpStatus.UNAUTHORIZED, "게시글 복구 권한이 없습니다."),
	UNAUTHORIZED_READ_SCRAPS(HttpStatus.UNAUTHORIZED, "스크랩 게시글 목록 조회 권한이 없습니다."),
	BAD_REQUEST_RESTORE(HttpStatus.BAD_REQUEST, "게시글이 이미 복구되었거나, 삭제되지 않았습니다."),
	BAD_REQUEST_SEARCH_OPTION(HttpStatus.BAD_REQUEST, "검색 조건이 잘못 되었거나 비어있습니다."),
	BAD_REQUEST_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "검색 키워드는 2글자 이상이어야 합니다."),
	BAD_REQUEST_SORT_ORDER(HttpStatus.BAD_REQUEST, "잘못된 정렬 조건입니다."),
	BAD_REQUEST_TOGGLE_SCRAP(HttpStatus.BAD_REQUEST, "본인 게시글을 스크랩할 수 없습니다.");

	private final CommonException commonException;

	PostError(HttpStatus code, String message) {
		commonException = new CommonException(code, message);
	}

	public CommonException getException() {
		return commonException;
	}
}
