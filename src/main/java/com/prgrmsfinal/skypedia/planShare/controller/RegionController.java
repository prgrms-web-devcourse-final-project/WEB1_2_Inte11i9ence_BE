package com.prgrmsfinal.skypedia.planShare.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrmsfinal.skypedia.planShare.dto.RegionDTO;
import com.prgrmsfinal.skypedia.planShare.service.RegionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/region")
@Log4j2
public class RegionController {
	private final RegionService regionService;

	@GetMapping
	public ResponseEntity<List<RegionDTO>> readAll(@Validated RegionDTO regionDTO) {
		return ResponseEntity.ok(regionService.readAll(regionDTO));
	}

	@GetMapping("/{id}")
	public ResponseEntity<RegionDTO> read(@PathVariable("id") @Validated Long id) {
		return ResponseEntity.ok(regionService.read(id));
	}

	@PostMapping
	public ResponseEntity<RegionDTO> create(@RequestBody RegionDTO regionDTO) {
		RegionDTO createdRegion = regionService.register(regionDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdRegion);
	}

	@PutMapping("/{id}")
	public ResponseEntity<RegionDTO> update(@PathVariable Long id, @RequestBody RegionDTO regionDTO) {
		regionDTO.setId(id);
		return ResponseEntity.ok(regionService.update(regionDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable("id") Long id) {
		regionService.delete(id);
		return ResponseEntity.ok(Map.of("message", "삭제 완료"));
	}
}
