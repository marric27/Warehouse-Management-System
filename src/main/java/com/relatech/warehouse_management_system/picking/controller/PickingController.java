package com.relatech.warehouse_management_system.picking.controller;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.ErrorReason;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.picking.service.PickingService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/confirm")
    public void confirmPicking(@RequestBody Request request) throws ResourceNotFoundException {
        log.info("Confirm picking: {}", request);
        pickingService.confirmPicking(request);
        log.info("Picking confirmed");
    }

    /**
     * Wrapper request DTO: contains
     */
    @Getter
    @Setter
    @ToString
    public static class Request {
        @Schema(example = "PKL-01KCKXFNW3")
        private String pickListCode;
        @Schema(example = "PKLI-22")
        private String pickListItemCode;
        @Schema(
                description = "Mappa stockUnitCode → quantità pickata",
                example = "{ \"STK-01KCH3N988\": 3, \"STK-01KCH3MHZZ\": 2 }"
        )
        private Map<String, Integer> stockUnitQuantities; // stockunit code, quantity
        private ErrorReason errorReason;
    }


}
