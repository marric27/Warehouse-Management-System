package com.relatech.warehouse_management_system.logistic.controller;

import com.relatech.warehouse_management_system.logistic.service.LogisticService;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logistic")
public class LogisticController {
    @Autowired
    private LogisticService logisticService;

    // POST /api/slots/{slotId}/assign/{productId}
    @PostMapping("/{slotId}/assign/{productId}")
    public ResponseEntity<SlotDTO> assignProductToSlot(
            @PathVariable Long slotId,
            @PathVariable Long productId
    ) {
        SlotDTO slotDTO = logisticService.assignProductToSlot(slotId, productId);
        return ResponseEntity.ok(slotDTO);
    }

    // POST /api/slots/{id}/remove-product
    @PostMapping("/{id}/remove-product")
    public ResponseEntity<SlotDTO> removeProductFromSlot(@PathVariable Long id) {
        SlotDTO slotDTO = logisticService.removeProductFromSlot(id);
        return ResponseEntity.ok(slotDTO);
    }

    // GET /api/slots/{slotId}/can-contain/{productId}
    @GetMapping("/{slotId}/can-contain/{productId}")
    public ResponseEntity<Boolean> canSlotContainProduct(
            @PathVariable Long slotId,
            @PathVariable Long productId
    ) {
        boolean canContain = logisticService.canSlotContainProduct(slotId, productId);
        return ResponseEntity.ok(canContain);
    }
}
