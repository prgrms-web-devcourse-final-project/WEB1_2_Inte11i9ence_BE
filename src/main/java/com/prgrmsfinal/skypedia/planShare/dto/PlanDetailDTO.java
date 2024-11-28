package com.prgrmsfinal.skypedia.planShare.dto;

import com.prgrmsfinal.skypedia.planShare.entity.PlanGroup;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PlanDetailDTO {

    private Long id;

    private Long planGroupId;

    private Long prePlanDetailId;

    private Long nextPlanDetailId;

    private String location;

    private String content;

    @DecimalMax(value = "90.0")
    @DecimalMin(value = "-90.0")
    private Double latitude;

    @DecimalMax(value = "90.0")
    @DecimalMin(value = "-90.0")
    private Double longitude;

    private String locationImage;

    private LocalDate planDate;

    private Boolean deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
