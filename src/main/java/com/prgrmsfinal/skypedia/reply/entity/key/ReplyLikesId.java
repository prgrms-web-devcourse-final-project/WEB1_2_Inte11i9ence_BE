package com.prgrmsfinal.skypedia.reply.entity.key;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class ReplyLikesId implements Serializable {
	private Long replyId;

	private Long memberId;
}
