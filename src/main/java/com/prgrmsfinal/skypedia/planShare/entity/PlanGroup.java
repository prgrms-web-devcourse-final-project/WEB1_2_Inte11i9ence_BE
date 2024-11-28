package com.prgrmsfinal.skypedia.planShare.entity;

import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "plan_group")
public class PlanGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // 일정 그룹 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;              // 해당 게시물의 회원 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;              // 일정의 지역 ID

    @OneToMany(mappedBy = "planGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlanDetail> planDetails = new LinkedList<>();  // 세부 일정

    @OneToMany(mappedBy = "planGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlanGroupReply> planGroupReplies;  // 일정 공유 게시글의 댓글

    @Column(length = 20, nullable = false, columnDefinition = "TEXT")
    private String title;               // 일정 제목

    @Column(nullable = false)
    private String groupImage;          // 일정 대표 이미지

    @Column(nullable = false)
    private Long views = 0L;             // 조회수

    @Builder.Default
    private Long likes = 0L;             // 좋아요 수

    @Builder.Default
    private Boolean deleted = false;     // 논리 삭제 여부

    @CreatedDate
    private LocalDateTime createdAt;    // 게시물 생성 일자

    @LastModifiedDate
    private LocalDateTime updatedAt;    // 게시물 수정 일자

    private LocalDateTime deletedAt;    // 게시물 삭제 일자
}
