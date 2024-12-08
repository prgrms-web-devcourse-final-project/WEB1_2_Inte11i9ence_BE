package com.prgrmsfinal.skypedia.planShare.entity;

import java.time.LocalDateTime;

import com.prgrmsfinal.skypedia.planShare.entity.key.PlanGroupReplyId;
import com.prgrmsfinal.skypedia.reply.entity.Reply;

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
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlanGroupReply {
	@EmbeddedId
	private PlanGroupReplyId id;

	@ManyToOne
	@MapsId("planGroupId")
	private PlanGroup planGroup;

	@ManyToOne
	@MapsId("replyId")
	private Reply reply;

	private LocalDateTime repliedAt;
}
