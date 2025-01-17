package com.prgrmsfinal.skypedia.planShare.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(title = "지역 카테고리 DTO", description = "지역 카테고리에 관한 DTO 입니다.")
public class RegionRequestDTO {
	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	public static class Create {
		@Schema(title = "지역 카테고리 id", description = "지역 카테고리의 id 입니다")
		private final Long id;

		@Schema(title = "지역명", description = "지역 카테고리의 이름입니다.", example = "서울")
		private final String regionName;
	}
}
