package com.prgrmsfinal.skypedia.planShare.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupDTO;
import com.prgrmsfinal.skypedia.planShare.service.PlanGroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plan-group")
public class PlanGroupController {
	private final PlanGroupService planGroupService;

	@GetMapping
	public ResponseEntity<List<PlanGroupDTO>> readAll(@Valid PlanGroupDTO planGroupDTO) {
		return ResponseEntity.ok(planGroupService.readAll(planGroupDTO));
	}

	@GetMapping("/{id}")
	public ResponseEntity<PlanGroupDTO> read(@PathVariable("id") @Valid Long id) {
		return ResponseEntity.ok(planGroupService.read(id));
	}

	@PostMapping
	public ResponseEntity<PlanGroupDTO> create(@RequestBody PlanGroupDTO planGroupDTO) {
		PlanGroupDTO createdPlanGroup = planGroupService.register(planGroupDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdPlanGroup);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PlanGroupDTO> update(@PathVariable("id") Long id, @RequestBody PlanGroupDTO planGroupDTO) {
		return ResponseEntity.ok(planGroupService.update(planGroupDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable("id") Long id) {
		planGroupService.delete(id);
		return ResponseEntity.ok(Map.of("message", "삭제 완료"));
	}
}
