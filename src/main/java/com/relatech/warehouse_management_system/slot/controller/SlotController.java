package com.relatech.warehouse_management_system.slot.controller;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import com.relatech.warehouse_management_system.slot.service.SlotService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/slots")
@Tag(name = "Slot Management", description = "APIs for managing slots")
public class SlotController {

    @Autowired
    private SlotService slotService;

    @GetMapping
    public ResponseEntity<List<SlotDTO>> getAllSlots() {
        log.info("Received GET request for all slots");
        List<SlotDTO> slots = slotService.getAllSlots();
        log.info("Returning slots: {}", slots);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SlotDTO> getSlotById(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Received GET request for slot with ID: {}", id);
        SlotDTO slot = slotService.getSlotById(id);
        log.info("Returning slot: {}", slot);
        return ResponseEntity.ok(slot);
    }

    @PostMapping
    public ResponseEntity<SlotDTO> createSlot(@Valid @RequestBody SlotDTO slotDTO) {
        log.info("Received POST request to create slot : {}", slotDTO);
        SlotDTO createdSlot = slotService.createSlot(slotDTO);
        log.info("Slot created with ID: {}", createdSlot);
        return ResponseEntity.ok(createdSlot);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SlotDTO> updateSlot(@PathVariable Long id, @Valid @RequestBody SlotDTO slotDTO) throws Exception {
        log.info("Received PUT request to update slot with ID: {}", id);
        SlotDTO updatedSlot = slotService.updateSlot(id, slotDTO);
        log.info("Slot updated: {} (ID: {})", updatedSlot, id);
        return ResponseEntity.ok(updatedSlot);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Received DELETE request for slot with ID: {}", id);
        slotService.deleteSlot(id);
        log.info("Slot with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/slots/{slotId}/assign/{stockUnitId}
    @PatchMapping("/{slotId}/assign/{stockUnitId}")
    public ResponseEntity<SlotDTO> assignStockUnitToSlot(@PathVariable Long slotId, @PathVariable Long stockUnitId) throws ResourceNotFoundException {
        log.info("Received PATCH request for assign stock unit with ID {} to slot with ID {} ", stockUnitId, slotId);
        SlotDTO slotDTO = slotService.assignStockUnitToSlot(slotId, stockUnitId);
        log.info("Successfully assigned stock unit: {} to slot: {}", stockUnitId, slotId);
        return ResponseEntity.ok(slotDTO);
    }

    // PATCH /api/stock-units/{slotId}/remove-stock-unit
    @PatchMapping("/{slotId}/remove-stock-unit/{stockUnitId}")
    public ResponseEntity<SlotDTO> removeStockUnitFromSlot(@PathVariable Long slotId, @PathVariable Long stockUnitId) throws ResourceNotFoundException {
        log.info("Received PATCH request for remove stock unit from slot with ID {} ", slotId);
        SlotDTO slotDTO = slotService.removeStockUnitFromSlot(slotId, stockUnitId);
        log.info("Successfully removed stock unit from slot with ID: {}", slotId);
        return ResponseEntity.ok(slotDTO);
    }




}