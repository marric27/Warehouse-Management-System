package com.relatech.warehouse_management_system.picking.controller;

import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.picking.service.PickingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/picking")
@RequiredArgsConstructor
@Slf4j
public class PickingController {

    private final PickingService pickingService;

    @PostMapping("/next-item")
    public ResponseEntity<PickListItemDto> getNextPickListItem(@RequestBody List<Long> pickListIds) {
        log.info("Get next pick list item from pick list(s) with id(s) {}", pickListIds);
        PickListItemDto nextItem = pickingService.getNextPickListItem(pickListIds);
        if (nextItem == null) {
            log.info("No next items found");
            return ResponseEntity.noContent().build();
        }
        log.info("Next item found {}", nextItem);
        return ResponseEntity.ok(nextItem);
    }


}
