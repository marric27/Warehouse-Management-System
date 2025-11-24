package com.relatech.warehouse_management_system.stockUnit.controller;

import com.relatech.warehouse_management_system.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.stockUnit.service.StockUnitService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/stock-units")
@Tag(name = "Stock Unit Management", description = "APIs for managing stock units")
public class StockUnitController {

    private final StockUnitService stockUnitService;

    @PostMapping
    public ResponseEntity<StockUnitDTO> createStockUnit(@RequestBody StockUnitDTO dto) throws DuplicateResourceException, ValidationException {
        log.info("Request to create stock unit with uniqueCode: {}", dto.getUniqueCode());
        StockUnitDTO created = stockUnitService.createStockUnit(dto);
        log.info("Stock unit created with ID: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<StockUnitDTO>> getAllStockUnits() {
        log.info("Request to fetch all stock units");
        List<StockUnitDTO> stockUnits = stockUnitService.getAllStockUnits();
        log.info("Returning stock units: {}", stockUnits);
        return ResponseEntity.ok(stockUnits);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<StockUnitDTO>> getAllStockUnitsPaged (@PageableDefault(page = 0, size = 10) Pageable pageable){
        log.info("Request to fetch all stock units paged: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<StockUnitDTO> stockUnits = stockUnitService.getAllStockUnitsPaged(pageable);
        return ResponseEntity.ok(stockUnits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockUnitDTO> getStockUnitById(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Request to fetch stock unit by ID {}", id);
        StockUnitDTO stockUnit = stockUnitService.getStockUnitById(id);
        log.info("Returning slot: {}", stockUnit);
        return ResponseEntity.ok(stockUnit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockUnitDTO> updateStockUnit(@PathVariable Long id, @RequestBody StockUnitDTO dto) throws ResourceNotFoundException, ValidationException {
        log.info("Request to update stock unit with ID: {}", id);
        StockUnitDTO updated = stockUnitService.updateStockUnit(id, dto);
        log.info("stock unit updated: {} (ID: {})", updated, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockUnit(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Request to delete stock unit with ID: {}", id);
        stockUnitService.deleteStockUnit(id);
        log.info("Stock unit with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/stock-unit/{stockUnitId}/assign/{productId}
    @PatchMapping("/{stockUnitId}/assign/{productId}")
    public ResponseEntity<StockUnitDTO> assignProductToStockUnit(
            @PathVariable Long stockUnitId,
            @PathVariable Long productId
    ) throws ResourceNotFoundException {
        log.info("Received PATCH request for assign product with ID {} to stock unit with ID {} ", productId, stockUnitId);
        StockUnitDTO stockUnitDTO = stockUnitService.assignProductToStockUnit(stockUnitId, productId);
        log.info("Successfully assigned product: {} to stock unit: {}", productId, stockUnitId);
        return ResponseEntity.ok(stockUnitDTO);
    }

    // PATCH /api/stock-units/{stockUnitId}/remove-product
    @PatchMapping("/{stockUnitId}/remove-product")
    public ResponseEntity<StockUnitDTO> removeProductFromStockUnit(@PathVariable Long stockUnitId) throws ResourceNotFoundException {
        log.info("Received PATCH request for remove product from stock unit with ID {} ", stockUnitId);
        StockUnitDTO stockUnitDTO = stockUnitService.removeProductFromStockUnit(stockUnitId);
        log.info("Successfully removed product from stock unit: {}", stockUnitId);
        return ResponseEntity.ok(stockUnitDTO);
    }


}