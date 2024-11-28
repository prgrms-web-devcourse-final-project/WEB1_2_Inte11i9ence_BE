package com.prgrmsfinal.skypedia.post.exception;

import com.prgrmsfinal.skypedia.global.exception.CommonException;
import org.springframework.http.HttpStatus;

public enum PostError {
    CANNOT_FOUND_CATEGORY(HttpStatus.BAD_REQUEST.value(), "해당 카테고리는 존재하지 않습니다."),
    CANNOT_FOUND_POST(HttpStatus.NOT_FOUND.value(), "게시글이 삭제되었거나 존재하지 않습니다."),
    CANNOT_FOUND_POSTS(HttpStatus.NO_CONTENT.value(), "게시글 목록이 존재하지 않습니다."),
    BAD_REQUEST_POST_MODIFY(HttpStatus.BAD_REQUEST.value(), "게시글 수정 권한이 없습니다."),
    BAD_REQUEST_POST_DELETE(HttpStatus.BAD_REQUEST.value(), "게시글 삭제 권한이 없습니다."),
    BAD_REQUEST_POST_RESTORE(HttpStatus.BAD_REQUEST.value(), "게시글 복구 권한이 없습니다."),
    INVALID_SORT_ORDER(HttpStatus.BAD_REQUEST.value(), "잘못된 정렬 조건입니다.");

    private CommonException commonException;

    PostError(int code, String message) {
        commonException = new CommonException(code, message);
    }

    public CommonException getException() {
        return commonException;
    }
}
