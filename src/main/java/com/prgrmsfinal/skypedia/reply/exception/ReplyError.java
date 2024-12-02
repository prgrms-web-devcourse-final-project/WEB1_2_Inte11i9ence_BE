package com.prgrmsfinal.skypedia.reply.exception;

public enum ReplyError {
	BAD_REQUEST("잘못된 접근입니다", 400),
	NOT_REGISTERED("댓글을 등록할 수 없습니다", 400),
	NOT_MODIFIED("댓글을 수정할 수 없습니다", 400),
	NOT_REMOVED("댓글을 삭제할 수 없습니다", 400);

	private ReplyException replyException;

	ReplyError(String message, int code) {
		replyException = new ReplyException(message, code);
	}

	public ReplyException get() {
		return replyException;
	}
}
