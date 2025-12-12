package com.relatech.warehouse_management_system.outbound.release.controller;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.release.service.PickListGen;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/picklists")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "PickList Management",
        description = "Endpoints for generating picklists from orders"
)
public class PickListController {

    private final PickListGen pickListGen;

    /**
     * Endpoint per rilasciare ordini e generare PickList per cliente.
     * @param orderIds lista di ID degli ordini da rilasciare
     * @return lista di PickListDto generate
     * @throws ResourceNotFoundException se qualche ordine non viene trovato
     */
    @PostMapping("/release")
    public ResponseEntity<List<PickListDto>> releaseOrders(@RequestBody List<Long> orderIds)
            throws ResourceNotFoundException {

        List<PickListDto> pickLists = pickListGen.generatePickLists(orderIds);
        return ResponseEntity.ok(pickLists);
    }

    @PostMapping("/generate/{orderId}")
    @Operation(summary = "Generate picklist", description = "Generates a picklist DTO based on the order ID")
    public ResponseEntity<PickListDto> generatePickList(@PathVariable Long orderId) {
        try {
            PickListDto pickListDto = pickListGen.generatePickList(orderId);
            return ResponseEntity.ok(pickListDto);
        } catch (ResourceNotFoundException e) {
            log.error("Order not found: {}", orderId, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error generating picklist for order {}", orderId, e);
            return ResponseEntity.status(500).build();
        }
    }
}