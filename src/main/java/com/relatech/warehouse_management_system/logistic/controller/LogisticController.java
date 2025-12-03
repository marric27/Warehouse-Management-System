package com.relatech.warehouse_management_system.logistic.controller;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.logistic.service.LogisticService;
import com.relatech.warehouse_management_system.goodsIn.dto.SlotDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/logistic")
@Tag(name = "Logistic Management", description = "APIs for managing logistic")
public class LogisticController {
    @Autowired
    private LogisticService logisticService;

    // PATCH /api/slots/{slotId}/assign/{productId}
    @PatchMapping("/{slotId}/assign/{productId}")
    public ResponseEntity<SlotDTO> assignProductToSlot(
            @PathVariable Long slotId,
            @PathVariable Long productId
    ) throws ResourceNotFoundException {
        log.info("Received PATCH request for assign product with ID {} to slot with ID {} ", slotId, productId);
        SlotDTO slotDTO = logisticService.assignProductToSlot(slotId, productId);
        log.info("Successfully assigned product: {} to slot: {}", productId, slotId);
        return ResponseEntity.ok(slotDTO);
    }

    // PATCH /api/slots/{id}/remove-product
    @PatchMapping("/{id}/remove-product")
    public ResponseEntity<SlotDTO> removeProductFromSlot(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Received PATCH request for remove product from slot with ID {} ", id);
        SlotDTO slotDTO = logisticService.removeProductFromSlot(id);
        log.info("Successfully removed product from slot: {}",id);
        return ResponseEntity.ok(slotDTO);
    }

    // GET /api/slots/{slotId}/can-contain/{productId}
    @GetMapping("/{slotId}/can-contain/{productId}")
    public ResponseEntity<Boolean> canSlotContainProduct(
            @PathVariable Long slotId,
            @PathVariable Long productId
    ) throws ResourceNotFoundException {
        log.info("Received GET request to check if slot with ID {} can contain product with ID {}", slotId, productId);
        boolean canContain = logisticService.canSlotContainProduct(slotId, productId);
        log.info("slot with ID {} can contain product with ID {}: {}",slotId, productId, canContain);
        return ResponseEntity.ok(canContain);
    }
}
