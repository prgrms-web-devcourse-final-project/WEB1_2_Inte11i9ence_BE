package com.prgrmsfinal.skypedia.post.entity;

import java.time.LocalDateTime;

import com.prgrmsfinal.skypedia.post.entity.key.PostReplyId;
import com.prgrmsfinal.skypedia.reply.entity.Reply;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostReply {
	@EmbeddedId
	private PostReplyId id;

	@ManyToOne
	@MapsId("postId")
	private Post post;

	@ManyToOne
	@MapsId("replyId")
	private Reply reply;

	@Column(insertable = false, updatable = false)
	private LocalDateTime repliedAt;
}