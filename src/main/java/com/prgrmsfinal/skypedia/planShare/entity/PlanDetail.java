package com.prgrmsfinal.skypedia.planShare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "plan_detail",
    indexes = {
            @Index(name = "idx_plan_detail_location", columnList = "latitude, longitude", unique = false)
    }
)
public class PlanDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // 세부 일정 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_group_id", nullable = false)
    private PlanGroup planGroup;        // 세부 일정이 속한 그룹 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_detail_next_id")
    private PlanDetail nextPlanDetail;  // 이전 세부 일정

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_detail_prev_id")
    private PlanDetail prePlanDetail;   // 이후 세부 일정

    @Column(nullable = false, columnDefinition = "TEXT")
    private String location;            // 장소명

    @Column(columnDefinition = "TEXT")
    private String content;             // 장소 설명

    @Column(nullable = false)
    private Double latitude;            // 위도

    @Column(nullable = false)
    private Double longitude;           // 경도

    @Column(nullable = false)
    private String locationImage;       // 장소 이미지

    @Column(nullable = false)
    private LocalDate planDate;         // 장소 방문 날짜

    @Builder.Default
    private Boolean deleted = false;    // 논리 삭제 여부

    @CreatedDate
    private LocalDateTime createdAt;    // 세부 일정 생성 일자

    @LastModifiedDate
    private LocalDateTime updatedAt;    // 세부 일정 수정 일자

    private LocalDateTime deletedAt;    // 세부 일정 삭제 일자

    @PrePersist
    @PreUpdate
    private void validateCoordinates() {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("위도 값은 -90 ~ 90 사이여야 합니다.");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("경도 값은 -180 ~ 180 사이여야 합니다.");
        }
    }
}
