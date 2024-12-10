package com.prgrmsfinal.skypedia.post.entity;

import java.time.LocalDateTime;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.post.entity.key.PostScrapId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
public class PostScrap {
	@EmbeddedId
	private PostScrapId id;

	@ManyToOne
	@MapsId("postId")
	private Post post;

	@ManyToOne
	@MapsId("memberId")
	private Member member;

	@Column(insertable = false, updatable = false)
	private LocalDateTime scrapedAt;
}
