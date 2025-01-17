package com.prgrmsfinal.skypedia.planShare.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

import com.prgrmsfinal.skypedia.planShare.dto.RegionRequestDTO;
import com.prgrmsfinal.skypedia.planShare.dto.RegionResponseDTO;

import jakarta.validation.Valid;

public interface RegionService {
	List<RegionResponseDTO.Read> readAll();

	RegionResponseDTO read(String regionName);

	void create(Authentication authentication, @Valid @RequestBody RegionRequestDTO.Create regionRequestDTO);

	RegionRequestDTO.Create update(RegionRequestDTO.Create regionRequestDTO);

	void delete(Long id);
}
