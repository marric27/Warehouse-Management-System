package com.relatech.warehouse_management_system.checkingInfo.controller;

import com.relatech.warehouse_management_system.checkingInfo.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.checkingInfo.service.CheckingInfoService;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.util.State;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checking-info")
@RequiredArgsConstructor
@Tag(name = "Checking info", description = "APIs for checking-info")
@Slf4j
public class CheckingInfoController {

    private final CheckingInfoService checkingInfoService;

    @PostMapping
    public ResponseEntity<CheckingInfoDto> create(@Valid @RequestBody CheckingInfoDto dto) {
        log.info("POST /checking-info - creating CheckingInfo: {}", dto);
        CheckingInfoDto created = checkingInfoService.create(dto);
        log.info("Created CheckingInfo with ID: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CheckingInfoDto> update(@PathVariable Long id, @Valid @RequestBody CheckingInfoDto dto) throws ResourceNotFoundException {
        log.info("PUT /checking-info/{} - updating CheckingInfo: {}", id, dto);
        CheckingInfoDto updated = checkingInfoService.update(id, dto);
        log.info("Updated CheckingInfo with ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckingInfoDto> getOne(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("GET /checking-info/{} - fetching CheckingInfo", id);
        CheckingInfoDto dto = checkingInfoService.getById(id);
        log.info("Fetched CheckingInfo: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<CheckingInfoDto>> getAll() {
        log.info("GET /checking-info - fetching all CheckingInfo");
        List<CheckingInfoDto> list = checkingInfoService.getAll();
        log.info("Fetched {} CheckingInfo records", list.size());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("DELETE /checking-info/{} - deleting CheckingInfo", id);
        checkingInfoService.delete(id);
        log.info("Deleted CheckingInfo with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/set-stock-unit/{stockUnitId}")
    public ResponseEntity<CheckingInfo> setStockUnit(@PathVariable Long id, @PathVariable Long stockUnitId) throws ResourceNotFoundException {
        CheckingInfo updated = checkingInfoService.setStockUnit(id, stockUnitId);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<CheckingInfo> updateState(@PathVariable Long id, @RequestParam State state) {
        CheckingInfo updatedCI = checkingInfoService.updateCheckingInfoState(id, state);
        return ResponseEntity.ok(updatedCI);
    }
}
