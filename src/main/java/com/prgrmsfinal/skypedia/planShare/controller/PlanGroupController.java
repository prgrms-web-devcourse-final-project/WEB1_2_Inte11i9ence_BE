package com.prgrmsfinal.skypedia.planShare.controller;

import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupDTO;
import com.prgrmsfinal.skypedia.planShare.service.PlanGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plan-group")
@Log4j2
public class PlanGroupController {
    private final PlanGroupService planGroupService;

    @GetMapping
    public ResponseEntity<List<PlanGroupDTO>> readAll(@Validated PlanGroupDTO planGroupDTO) {
        return ResponseEntity.ok(planGroupService.readAll(planGroupDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanGroupDTO> read(@PathVariable("id") @Validated Long id) {
        return ResponseEntity.ok(planGroupService.read(id));
    }

    @PostMapping
    public ResponseEntity<PlanGroupDTO> create(@RequestBody PlanGroupDTO planGroupDTO) {
        return ResponseEntity.ok(planGroupService.register(planGroupDTO));
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
