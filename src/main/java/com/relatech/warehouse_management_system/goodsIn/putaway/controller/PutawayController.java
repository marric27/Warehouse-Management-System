package com.relatech.warehouse_management_system.goodsIn.putaway.controller;

import com.relatech.warehouse_management_system.goodsIn.putaway.service.PutawayService;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/putaway")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Putaway Management", description = "Complete workflow for Putaway")
public class PutawayController {
    private final PutawayService putawayService;

    @PostMapping("/{stockUnitCode}/assignToSlot/{slotCode}")
    public ResponseEntity<SlotDto> assignStockUnitToSlot(@PathVariable String stockUnitCode, @PathVariable String slotCode) throws Exception {
        log.info("POST /putaway/{}/assignToSlot/{} - Assigning stockunit to slot", stockUnitCode, slotCode);
        SlotDto slotDTO = putawayService.assignStockUnitToSlot(stockUnitCode, slotCode);
        log.info("StockUnit {} assigned to Slot {}", stockUnitCode, slotCode);

        return ResponseEntity.ok(slotDTO);
    }


}