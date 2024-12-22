package com.prgrmsfinal.skypedia.post.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.prgrmsfinal.skypedia.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private PostCategory category;

	private String title;

	private String content;

	private String hashtags;

	@Builder.Default
	private Long views = 0L;

	@Builder.Default
	private Long likes = 0L;

	private Float rating;

	@Builder.Default
	private boolean deleted = false;

	@Column(insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;

	public void modify(String title, String content, List<String> hashtags) {
		this.title = title;
		this.content = content;
		this.hashtags = String.join(",", hashtags);
	}

	public void delete() {
		this.deleted = true;

		this.deletedAt = LocalDateTime.now();
	}

	public void restore() {
		this.deleted = false;

		this.deletedAt = null;
	}
}
