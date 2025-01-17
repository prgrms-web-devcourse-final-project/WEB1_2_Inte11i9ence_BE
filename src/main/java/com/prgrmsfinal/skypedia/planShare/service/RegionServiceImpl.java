package com.prgrmsfinal.skypedia.planShare.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.prgrmsfinal.skypedia.planShare.dto.RegionRequestDTO;
import com.prgrmsfinal.skypedia.planShare.dto.RegionResponseDTO;
import com.prgrmsfinal.skypedia.planShare.entity.Region;
import com.prgrmsfinal.skypedia.planShare.exception.PlanError;
import com.prgrmsfinal.skypedia.planShare.mapper.RegionMapper;
import com.prgrmsfinal.skypedia.planShare.repository.RegionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {
	private final RegionRepository regionRepository;

	@Override
	@ResponseStatus(HttpStatus.OK)
	public List<RegionResponseDTO.Read> readAll() {
		return regionRepository.findAll().stream().map(RegionMapper::entityToDTO).toList();
	}

	@Override
	@ResponseStatus(HttpStatus.OK)
	public RegionResponseDTO read(String regionName) {
		return regionRepository.findByRegionName(regionName);
	}

	@Override
	public void create(Authentication authentication, RegionRequestDTO.Create regionRequestDTO) {
		regionRepository.save(RegionMapper.dtoToEntity(regionRequestDTO));
	}

	@Override
	public RegionRequestDTO.Create update(RegionRequestDTO.Create regionRequestDTO) {
		Optional<Region> updateRegion = regionRepository.findById(regionRequestDTO.getId());
		Region region = updateRegion.orElseThrow(PlanError.NOT_FOUND::getException);

		regionRepository.save(region);
		return null;
	}

	@Override
	public void delete(Long id) {
		Region region = regionRepository.findById(id).orElseThrow(PlanError.NOT_FOUND::getException);
		regionRepository.delete(region);
	}
}
