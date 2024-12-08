package com.prgrmsfinal.skypedia.reply.entity;

import java.time.LocalDateTime;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.reply.entity.key.ReplyLikesId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReplyLikes {
	@EmbeddedId
	private ReplyLikesId id;

	@ManyToOne
	@MapsId("replyId")
	private Reply reply;

	@ManyToOne
	@MapsId("memberId")
	private Member member;

	@Column(insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime likedAt;
}
