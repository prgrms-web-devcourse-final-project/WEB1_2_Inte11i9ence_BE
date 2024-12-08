package com.prgrmsfinal.skypedia.photo.exception;

public enum PhotoException {
	BAD_REQUEST(400, "올바르지 않은 접근 경로"),
	NOT_FOUND(404, "파일없음"),
	CONTENT_ERROR(500, "파일 정보 잘못됨.");

	private PhotoTaskException photoTaskException;

	PhotoException(int code, String message) {
		photoTaskException = new PhotoTaskException(code, message);
	}

	public PhotoTaskException get() {
		return photoTaskException;
	}
}

