package com.relatech.warehouse_management_system.GRN.controller;

import com.relatech.warehouse_management_system.GRN.dto.GrnDTO;
import com.relatech.warehouse_management_system.GRN.entity.GRN;
import com.relatech.warehouse_management_system.GRN.mapper.GrnMapper;
import com.relatech.warehouse_management_system.GRN.service.GrnService;
import com.relatech.warehouse_management_system.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.exception.GrnWithItemsException;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Goods Receipt Notes.
 */
@RestController
@RequestMapping("/api/grn")
@Tag(name = "GRN Management", description = "APIs for managing Goods Receipt Notes")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GrnController {

    private final GrnService grnService;
    private final GrnMapper grnMapper;

    @PostMapping
    public ResponseEntity<GrnDTO> createGRN(@Valid @RequestBody GrnDTO dto) throws DuplicateResourceException {
        log.info("Request to create GRN for supplier: {}", dto.getSupplier());
        GrnDTO created = grnService.createGRN(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<Page<GrnDTO>> getAllGRNsPaged(
            @ParameterObject
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        log.info("Request to fetch paginated GRN list: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<GrnDTO> grnPage = grnService.getAllGRNsPaged(pageable);
        return ResponseEntity.ok(grnPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrnDTO> getGRNById(@PathVariable String id) throws ResourceNotFoundException {
        log.info("Request to fetch GRN with id: {}", id);
        GrnDTO dto = grnService.getGRNById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<Page<GrnItemDto>> getGRNItems(
            @PathVariable String id,
            @ParameterObject
            @PageableDefault(size = 50) Pageable pageable
    ) throws ResourceNotFoundException {
        log.info("Request to fetch items for GRN: {}. Page size: {}", id, pageable.getPageSize());
        Page<GrnItemDto> itemsPage = grnService.findItemsByGrnId(id, pageable);
        return ResponseEntity.ok(itemsPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrnDTO> updateGRN(@PathVariable String id, @Valid @RequestBody GrnDTO dto) throws ResourceNotFoundException {
        log.info("Request to update GRN with id: {}", id);
        GrnDTO updated = grnService.updateGRN(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<GrnDTO> updateGRNStatus(
            @PathVariable String id,
            @RequestParam String status
    ) throws ResourceNotFoundException {
        log.info("Request to update status for GRN {} to {}", id, status);
        GrnDTO entity = grnService.updateStatus(id, status);
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGRN(@PathVariable String id) throws ResourceNotFoundException, GrnWithItemsException {
        log.info("Request to delete GRN with id: {}", id);
        grnService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<GrnDTO>> searchGrns(@RequestParam(name = "term", required = false) String term) {
        log.info("Request to search GRNs with term: {}", term);
        List<GrnDTO> results = grnService.searchGrns(term);
        return ResponseEntity.ok(results);
    }
}
