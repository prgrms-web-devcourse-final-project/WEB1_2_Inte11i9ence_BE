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

import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailDTO;
import com.prgrmsfinal.skypedia.planShare.service.PlanDetailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plan-detail")
@Log4j2
public class PlanDetailController {
	private final PlanDetailService planDetailService;

	@GetMapping
	public ResponseEntity<List<PlanDetailDTO>> readAll(@Valid PlanDetailDTO planDetailDTO) {
		return ResponseEntity.ok(planDetailService.readAll(planDetailDTO));
	}

	@GetMapping("/{id}")
	public ResponseEntity<PlanDetailDTO> read(@PathVariable("id") Long id) {
		return ResponseEntity.ok(planDetailService.read(id));
	}

	@PostMapping
	public ResponseEntity<PlanDetailDTO> create(@Validated @RequestBody PlanDetailDTO planDetailDTO) {
		PlanDetailDTO createdPlanDetail = planDetailService.register(planDetailDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdPlanDetail);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PlanDetailDTO> update(@PathVariable("id") Long id, @RequestBody PlanDetailDTO planDetailDTO) {
		return ResponseEntity.ok(planDetailService.update(planDetailDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable("id") Long id) {
		planDetailService.delete(id);
		return ResponseEntity.ok(Map.of("message", "삭제 완료"));
	}

}
