package com.prgrmsfinal.skypedia.reply.entity;

import java.time.LocalDateTime;

import com.prgrmsfinal.skypedia.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Reply {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_reply_id")
	private Reply parentReply;

	private String content;

	@Builder.Default
	private Long likes = 0L;

	@Builder.Default
	private boolean deleted = false;

	@Column(insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;

	public void changeContent(String content) {
		this.content = content;
	}

	public void delete() {
		deleted = true;
		deletedAt = LocalDateTime.now();
	}

	public void restore() {
		deleted = false;
		deletedAt = null;
	}
}
