package com.prgrmsfinal.skypedia.planShare.service;

import java.util.List;
import java.util.Optional;

import com.prgrmsfinal.skypedia.planShare.dto.RegionDTO;
import com.prgrmsfinal.skypedia.planShare.entity.Region;

public interface RegionService {
	List<RegionDTO> readAll();

	RegionDTO read(Long id);

	RegionDTO register(RegionDTO regionDTO);

	RegionDTO update(RegionDTO regionDTO);

	void delete(Long id);

	Optional<Region> findByRegionName(String regionName);

	boolean existsByRegionName(String regionName);
}
