package com.prgrmsfinal.skypedia.post.entity.key;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class PostReplyId implements Serializable {
	private Long postId;

	private Long replyId;
}
