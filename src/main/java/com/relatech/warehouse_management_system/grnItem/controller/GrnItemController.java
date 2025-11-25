package com.relatech.warehouse_management_system.grnItem.controller;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;
import com.relatech.warehouse_management_system.grnItem.entity.GrnItem;
import com.relatech.warehouse_management_system.grnItem.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.grnItem.service.GrnItemService;
import com.relatech.warehouse_management_system.util.State;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/grn-items")
@Tag(name = "Grn items Management", description = "APIs for managing grn items")
public class GrnItemController {

    @Autowired
    private GrnItemService grnItemService;

    @PostMapping
    public ResponseEntity<GrnItemDto> createGrnItem(@Valid @RequestBody GrnItemDto grnItemDto) {
        log.info("Received POST request to create GRN Item: {}", grnItemDto);
        GrnItemDto created = grnItemService.createGrnItem(grnItemDto);
        log.info("GRN Item created with ID: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<GrnItemDto>> getAllGrnItems() {
        log.info("Received GET request for all GRN Items");
        List<GrnItemDto> items = grnItemService.getAllGrnItems();
        log.info("Returning {} GRN Items", items.size());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrnItemDto> getGrnItemById(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Received GET request for GRN Item with ID: {}", id);
        GrnItemDto item = grnItemService.getGrnItemById(id);
        log.info("Returning GRN Item: {}", item);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrnItemDto> updateGrnItem(
            @PathVariable Long id,
            @Valid @RequestBody GrnItemDto grnItemDto
    ) throws ResourceNotFoundException {

        log.info("Received PUT request to update GRN Item with ID: {}", id);
        GrnItemDto updated = grnItemService.updateGrnItem(id, grnItemDto);
        log.info("GRN Item updated: {} (ID: {})", updated, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrnItem(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Received DELETE request for GRN Item with ID: {}", id);
        grnItemService.deleteGrnItem(id);
        log.info("GRN Item with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{grnItemId}/checking-info")
    public ResponseEntity<GrnItemDto> addCheckingInfoToGrnItem(@PathVariable Long grnItemId, @RequestBody List<Long> checkingInfoIds) throws ResourceNotFoundException {
        log.info("Received PATCH request for assign checking-info {} to grn item with ID {} ", checkingInfoIds, grnItemId);
        GrnItemDto result = grnItemService.addCheckinginfoToGrnitem(grnItemId, checkingInfoIds);
        log.info("Successfully assigned checking-info: {} to grn item: {}", checkingInfoIds, grnItemId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<GrnItemDto> updateState(@PathVariable Long id, @RequestParam State state) throws ResourceNotFoundException {
        GrnItem updatedGrnItemState = grnItemService.updateGrnItemState(id, state);
        return ResponseEntity.ok(GrnItemMapper.toDto(updatedGrnItemState));
    }
}
