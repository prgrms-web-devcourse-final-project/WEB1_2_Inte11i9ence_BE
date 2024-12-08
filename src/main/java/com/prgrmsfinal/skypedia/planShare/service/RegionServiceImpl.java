package com.prgrmsfinal.skypedia.planShare.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.planShare.dto.RegionDTO;
import com.prgrmsfinal.skypedia.planShare.entity.Region;
import com.prgrmsfinal.skypedia.planShare.exception.PlanError;
import com.prgrmsfinal.skypedia.planShare.repository.RegionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {
	private final RegionRepository regionRepository;

	@Override
	public List<RegionDTO> readAll() {
		List<Region> regions = regionRepository.findAll();
		return regions.stream().map(this::entityToDto).collect(Collectors.toList());
	}

	@Override
	public RegionDTO read(Long id) {
		Optional<Region> region = regionRepository.findById(id);
		return region.map(this::entityToDto).orElseThrow(PlanError.NOT_FOUND::getException);
	}

	@Override
	public RegionDTO register(RegionDTO regionDTO) {
		Region region = dtoToEntity(regionDTO);
		region = regionRepository.save(region);
		return entityToDto(region);
	}

	@Override
	public RegionDTO update(RegionDTO regionDTO) {
		Optional<Region> updateRegion = regionRepository.findById(regionDTO.getId());
		Region region = updateRegion.orElseThrow(PlanError.NOT_FOUND::getException);

		regionRepository.save(region);
		return entityToDto(region);
	}

	@Override
	public void delete(Long id) {
		Region region = regionRepository.findById(id).orElseThrow(PlanError.NOT_FOUND::getException);
		regionRepository.delete(region);
	}

	@Override
	public Optional<Region> findByRegionName(String regionName) {
		return regionRepository.findByRegionName(regionName);
	}

	@Override
	public boolean existsByRegionName(String regionName) {
		return regionRepository.existsByRegionName(regionName);
	}

	private RegionDTO entityToDto(Region region) {
		return RegionDTO.builder()
			.id(region.getId())
			.name(region.getRegionName())
			.build();
	}

	private Region dtoToEntity(RegionDTO regionDTO) {
		return Region.builder()
			.id(regionDTO.getId())
			.regionName(regionDTO.getName())
			.build();
	}
}
