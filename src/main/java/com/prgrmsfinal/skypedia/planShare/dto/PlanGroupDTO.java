package com.prgrmsfinal.skypedia.planShare.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PlanGroupDTO {

	private Long id;

	private Long memberId;

	private Long regionId;

	private String title;

	private String groupImage;

	private Long views;

	private Long likes;

	private Boolean deleted;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;
}
