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
	public List<RegionDTO> readAll(RegionDTO regionDTO) {
		List<Region> regions = regionRepository.findAll();
		return regions.stream().map(this::entityToDto).collect(Collectors.toList());
	}

	@Override
	public RegionDTO read(Long id) {
		Optional<Region> region = regionRepository.findById(id);
		return region.map(this::entityToDto).orElseThrow(PlanError.NOT_FOUND::get);
	}

	@Override
	public RegionDTO register(RegionDTO regionDTO) {
		try {
			Region region = dtoToEntity(regionDTO);
			region = regionRepository.save(region);
			return entityToDto(region);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw PlanError.NOT_REGISTERED.get();
		}
	}

	@Override
	public RegionDTO update(RegionDTO regionDTO) {
		Optional<Region> updateRegion = regionRepository.findById(regionDTO.getId());
		Region region = updateRegion.orElseThrow(PlanError.NOT_FOUND::get);

		try {
			region.setName(regionDTO.getName());

			regionRepository.save(region);

			return entityToDto(region);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw PlanError.NOT_MODIFIED.get();
		}
	}

	@Override
	public void delete(Long id) {
		Region region = regionRepository.findById(id).orElseThrow(PlanError.NOT_FOUND::get);
		regionRepository.delete(region);
	}

	private RegionDTO entityToDto(Region region) {
		return RegionDTO.builder()
			.id(region.getId())
			.name(region.getName())
			.build();
	}

	private Region dtoToEntity(RegionDTO regionDTO) {
		return Region.builder()
			.id(regionDTO.getId())
			.name(regionDTO.getName())
			.build();
	}
}
