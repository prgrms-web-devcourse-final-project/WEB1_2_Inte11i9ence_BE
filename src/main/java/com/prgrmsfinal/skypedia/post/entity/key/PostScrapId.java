package com.prgrmsfinal.skypedia.post.entity.key;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostScrapId implements Serializable {
	private Long postId;

	private Long memberId;
}
