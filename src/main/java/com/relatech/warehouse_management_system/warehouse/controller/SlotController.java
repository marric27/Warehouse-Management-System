package com.relatech.warehouse_management_system.warehouse.controller;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.exception.UpdateEntityException;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDTO;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/slot")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Slot Management", description = "Complete crud for Slot")
public class SlotController {
    
    private final SlotService slotService;

    @GetMapping
    @Operation(summary = "List all slots", description = "Returns all warehouse slots")
    @ApiResponse(responseCode = "200", description = "Slots retrieved")
    public ResponseEntity<List<SlotDTO>> listSlots() {
        log.info("GET /putaway/slots - listing all slots");
        return ResponseEntity.ok(slotService.getAllSlots());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get slot by ID")
    @ApiResponse(responseCode = "200", description = "Slot found")
    @ApiResponse(responseCode = "404", description = "Slot not found")
    public ResponseEntity<SlotDTO> getSlot(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("GET /putaway/slots/{} - fetching slot", id);
        return ResponseEntity.ok(slotService.getSlotById(id));
    }

    @PostMapping
    @Operation(summary = "Create slot")
    @ApiResponse(responseCode = "201", description = "Slot created")
    public ResponseEntity<SlotDTO> createSlot(@RequestBody SlotDTO dto) {
        log.info("POST /putaway/slots - creating slot {}", dto.getCode());
        SlotDTO created = slotService.createSlot(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update slot", description = "Updates capacity, allowedCategory, and stockUnits")
    @ApiResponse(responseCode = "200", description = "Slot updated")
    @ApiResponse(responseCode = "404", description = "Slot not found")
    @ApiResponse(responseCode = "409", description = "Invalid update (category conflict)")
    public ResponseEntity<SlotDTO> updateSlot(
            @PathVariable Long id,
            @RequestBody SlotDTO dto
    ) throws ResourceNotFoundException, UpdateEntityException {

        log.info("PUT /putaway/slots/{} - updating slot {}", id, dto.getCode());
        SlotDTO updated = slotService.updateSlot(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete slot", description = "Deletes a slot only if empty")
    @ApiResponse(responseCode = "204", description = "Slot deleted")
    @ApiResponse(responseCode = "404", description = "Slot not found")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("DELETE /putaway/slots/{} - deleting slot", id);
        slotService.deleteSlot(id);
        return ResponseEntity.noContent().build();
    }
}
