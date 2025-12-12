package com.relatech.warehouse_management_system.goodsIn.putaway.controller;

import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.goodsIn.putaway.service.PutawayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/putaway")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Putaway Management", description = "Complete workflow for Putaway")
public class PutawayController {
    private final PutawayService putawayService;

    @PostMapping("/{stockUnitId}/assignToSlot/{slotId}")
    public ResponseEntity<SlotDto> assignStockUnitToSlot(@PathVariable Long stockUnitId, @PathVariable Long slotId) throws Exception {
        log.info("POST /putaway/{}/assignToSlot/{} - Assigning stockunit to slot", stockUnitId, slotId);
        SlotDto slotDTO = putawayService.assignStockUnitToSlot(stockUnitId, slotId);
        log.info("StockUnit {} assigned to Slot {}", stockUnitId, slotId);

        return ResponseEntity.ok(slotDTO);
    }


}