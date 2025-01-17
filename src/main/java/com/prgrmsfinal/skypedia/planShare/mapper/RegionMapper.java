package com.prgrmsfinal.skypedia.planShare.mapper;

import com.prgrmsfinal.skypedia.planShare.dto.RegionRequestDTO;
import com.prgrmsfinal.skypedia.planShare.dto.RegionResponseDTO;
import com.prgrmsfinal.skypedia.planShare.entity.Region;

public class RegionMapper {
	public static RegionResponseDTO.Read entityToDTO(Region region) {
		return new RegionResponseDTO.Read(region.getId(), region.getRegionName());
	}

	public static Region dtoToEntity(RegionRequestDTO.Create regionRequestDTO) {
		return Region.builder()
			.id(regionRequestDTO.getId())
			.regionName(regionRequestDTO.getRegionName())
			.build();
	}
}
