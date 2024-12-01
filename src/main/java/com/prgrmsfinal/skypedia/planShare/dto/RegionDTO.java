package com.prgrmsfinal.skypedia.planShare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegionDTO {

	private Long id;

	private String name;
}
