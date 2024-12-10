package com.prgrmsfinal.skypedia.reply.entity.key;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyLikesId implements Serializable {
	private Long replyId;

	private Long memberId;
}
