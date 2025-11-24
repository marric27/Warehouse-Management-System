package com.relatech.warehouse_management_system.checkingInfo.controller;

import com.relatech.warehouse_management_system.checkingInfo.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.checkingInfo.service.CheckingInfoService;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
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

    private final CheckingInfoService service;

    @PostMapping
    public ResponseEntity<CheckingInfoDto> create(@Valid @RequestBody CheckingInfoDto dto) {
        log.info("POST /checking-info - creating CheckingInfo: {}", dto);
        CheckingInfoDto created = service.create(dto);
        log.info("Created CheckingInfo with ID: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CheckingInfoDto> update(@PathVariable Long id, @Valid @RequestBody CheckingInfoDto dto) throws ResourceNotFoundException {
        log.info("PUT /checking-info/{} - updating CheckingInfo: {}", id, dto);
        CheckingInfoDto updated = service.update(id, dto);
        log.info("Updated CheckingInfo with ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckingInfoDto> getOne(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("GET /checking-info/{} - fetching CheckingInfo", id);
        CheckingInfoDto dto = service.getById(id);
        log.info("Fetched CheckingInfo: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<CheckingInfoDto>> getAll() {
        log.info("GET /checking-info - fetching all CheckingInfo");
        List<CheckingInfoDto> list = service.getAll();
        log.info("Fetched {} CheckingInfo records", list.size());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("DELETE /checking-info/{} - deleting CheckingInfo", id);
        service.delete(id);
        log.info("Deleted CheckingInfo with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
