package com.prgrmsfinal.skypedia.planShare.controller;

import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailDTO;
import com.prgrmsfinal.skypedia.planShare.service.PlanDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plan-detail")
@Log4j2
public class PlanDetailController {
    private final PlanDetailService planDetailService;

    @GetMapping
    public ResponseEntity<List<PlanDetailDTO>> readAll(@Validated PlanDetailDTO planDetailDTO) {
        return ResponseEntity.ok(planDetailService.readAll(planDetailDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDetailDTO> read(@PathVariable("id") Long id) {
        return ResponseEntity.ok(planDetailService.read(id));
    }

    @PostMapping
    public ResponseEntity<PlanDetailDTO> create(@RequestBody PlanDetailDTO planDetailDTO) {
        return ResponseEntity.ok(planDetailService.register(planDetailDTO));
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
