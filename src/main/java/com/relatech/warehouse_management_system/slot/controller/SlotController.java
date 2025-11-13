package com.relatech.warehouse_management_system.slot.controller;

import com.relatech.warehouse_management_system.exception.EntityNotFoundException;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import com.relatech.warehouse_management_system.slot.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    @Autowired
    private SlotService slotService;

    @GetMapping
    public ResponseEntity<List<SlotDTO>> getAllSlots() {
        List<SlotDTO> slots = slotService.getAllSlots();
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SlotDTO> getSlotById(@PathVariable Long id) throws EntityNotFoundException {
        SlotDTO slot = slotService.getSlotById(id);
        return ResponseEntity.ok(slot);
    }

    @PostMapping
    public ResponseEntity<SlotDTO> createSlot(@RequestBody SlotDTO slotDTO) {
        SlotDTO createdSlot = slotService.createSlot(slotDTO);
        return ResponseEntity.ok(createdSlot);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SlotDTO> updateSlot(@PathVariable Long id, @RequestBody SlotDTO slotDTO) throws Exception {
        SlotDTO updatedSlot = slotService.updateSlot(id, slotDTO);
        return ResponseEntity.ok(updatedSlot);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long id) throws EntityNotFoundException {
        slotService.deleteSlot(id);
        return ResponseEntity.noContent().build();
    }


}