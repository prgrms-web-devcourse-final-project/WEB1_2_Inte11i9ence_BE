package com.prgrmsfinal.skypedia.reply.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "parentReply", fetch = FetchType.LAZY)
    private List<Reply> childrenReply = new ArrayList<>();

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
