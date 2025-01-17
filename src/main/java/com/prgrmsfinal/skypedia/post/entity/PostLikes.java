package com.prgrmsfinal.skypedia.post.entity;

import java.time.LocalDateTime;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.post.entity.key.PostLikesId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostLikes {
	@EmbeddedId
	private PostLikesId id;

	@ManyToOne
	@MapsId("postId")
	@JoinColumn(name = "post_id", referencedColumnName = "id")
	private Post post;

	@ManyToOne
	@MapsId("memberId")
	@JoinColumn(name = "member_id", referencedColumnName = "id")
	private Member member;

	@Column(insertable = false, updatable = false)
	private LocalDateTime likedAt;
}
