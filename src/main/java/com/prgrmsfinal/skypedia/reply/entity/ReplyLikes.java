package com.prgrmsfinal.skypedia.reply.entity;

import java.time.LocalDateTime;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.reply.entity.key.ReplyLikesId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReplyLikes {
	@EmbeddedId
	private ReplyLikesId id;

	@ManyToOne
	@MapsId("replyId")
	@JoinColumn(name = "reply_id", referencedColumnName = "id")
	private Reply reply;

	@ManyToOne
	@MapsId("memberId")
	@JoinColumn(name = "member_id", referencedColumnName = "id")
	private Member member;

	@Column(insertable = false, updatable = false)
	private LocalDateTime likedAt;
}
