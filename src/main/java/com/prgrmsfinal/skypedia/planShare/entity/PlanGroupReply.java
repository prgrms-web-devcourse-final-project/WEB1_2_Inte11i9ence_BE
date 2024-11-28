package com.prgrmsfinal.skypedia.planShare.entity;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.reply.entity.Reply;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanGroupReply {

    @EmbeddedId
    private PlanGroupReplyId id;

    @ManyToOne
    @MapsId("planGroupId")
    private PlanGroup planGroup;

    @ManyToOne
    @MapsId("memberId")
    private Member member;
}
