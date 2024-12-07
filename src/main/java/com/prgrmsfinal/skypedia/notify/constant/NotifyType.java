package com.prgrmsfinal.skypedia.notify.constant;

public enum NotifyType {
	REPLY("댓글"), CHAT("채팅"), NOTICE("공지");

	private final String name;

	NotifyType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
