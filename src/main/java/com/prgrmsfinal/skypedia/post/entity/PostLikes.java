package com.prgrmsfinal.skypedia.post.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.post.entity.key.PostLikesId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLikes {
	@EmbeddedId
	private PostLikesId id;

	@ManyToOne
	@MapsId("postId")
	private Post post;

	@ManyToOne
	@MapsId("memberId")
	private Member member;

	@LastModifiedDate
	private LocalDateTime likedAt;
}
