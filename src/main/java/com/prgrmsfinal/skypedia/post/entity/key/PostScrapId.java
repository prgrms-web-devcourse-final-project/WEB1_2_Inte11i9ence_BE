package com.prgrmsfinal.skypedia.post.entity.key;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class PostScrapId implements Serializable {
	private Long postId;

	private Long memberId;
}
