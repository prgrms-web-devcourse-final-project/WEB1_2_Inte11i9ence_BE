package com.prgrmsfinal.skypedia.post.entity;

import java.time.LocalDateTime;

import com.prgrmsfinal.skypedia.global.entity.BaseTime;
import com.prgrmsfinal.skypedia.member.entity.Member;

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
public class Post extends BaseTime {
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

	@Builder.Default
	private Long views = 0L;

	@Builder.Default
	private Long likes = 0L;

	private Float rating;

	@Builder.Default
	private boolean deleted = false;

	private LocalDateTime deletedAt;

	public void modify(String title, String content) {
		this.title = title;

		this.content = content;
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
