package com.relatech.warehouse_management_system.stockUnit.controller;

import com.relatech.warehouse_management_system.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.stockUnit.service.StockUnitService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/api/v1/stock-units")
public class StockUnitController {

    private final StockUnitService stockUnitService;

    @PostMapping
    public ResponseEntity<StockUnitDTO> createStockUnit(@RequestBody StockUnitDTO dto) throws DuplicateResourceException, ValidationException {
        log.info("Request to create stock unit with uniqueCode: {}", dto.getUniqueCode());
        StockUnitDTO created = stockUnitService.createStockUnit(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<StockUnitDTO>> getAllStockUnits(){
     log.info("Request to fetch all stock units");
     List<StockUnitDTO> stockUnits = stockUnitService.getAllStockUnits();
     return ResponseEntity.ok(stockUnits);
    }

    @PutMapping
    public ResponseEntity<StockUnitDTO> updateStockUnit (@PathVariable Long id, @RequestBody StockUnitDTO dto) throws ResourceNotFoundException, ValidationException {
      log.info("Request to update stock unit with ID: {}", id);
      StockUnitDTO updated = stockUnitService.updateStockUnit(id,dto);
      return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockUnit(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Request to delete stock unit with ID: {}",id);
        stockUnitService.deleteStockUnit(id);
        return ResponseEntity.noContent().build();
        }
    }