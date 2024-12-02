package com.prgrmsfinal.skypedia.reply.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Reply {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;            // 댓글 ID

	@Column(length = 500, nullable = false)
	private String content;     // 댓글 내용

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_reply_id")
	private Reply parentReply;  // 대댓글용 자기 참조

	// @OneToMany(mappedBy = "parentReply", fetch = FetchType.LAZY)
	// private List<Reply> childrenReply = new ArrayList<>();

	@Builder.Default
	private Long likes = 0L;

	@Builder.Default
	private boolean deleted = false;

	@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;
}
