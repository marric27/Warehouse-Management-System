package com.relatech.warehouse_management_system.outbound.release.controller;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import com.relatech.warehouse_management_system.outbound.release.service.PickListGen;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final PickListService pickListService;

    /**
     * Endpoint per rilasciare ordini e generare PickList per cliente.
     * @param orderIds lista di ID degli ordini da rilasciare
     * @return lista di PickListDto generate
     * @throws ResourceNotFoundException se qualche ordine non viene trovato
     */
    @PostMapping("/release")
    @Operation(summary = "Generate picklist", description = "Generates a picklist DTO based on the order IDs")
    public ResponseEntity<List<PickListDto>> releaseOrders(@RequestBody List<Long> orderIds) throws ResourceNotFoundException {

        List<PickListDto> pickLists = pickListGen.generatePickLists(orderIds);
        return ResponseEntity.ok(pickLists);
    }

    @GetMapping("/release")
    public ResponseEntity<List<PickListDto>> listAllPickList() {
        List<PickListDto> pickLists = pickListService.getAll();
        return ResponseEntity.ok(pickLists);
    }

    @GetMapping("/release-paged")
    public ResponseEntity<Page<PickListDto>> listAllPickListPaged(Pageable pageable) {
        Page<PickListDto> pickLists = pickListService.getAllPickListPaged(pageable);
        return ResponseEntity.ok(pickLists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PickListDto> listickListById(@PathVariable Long id) throws ResourceNotFoundException {
        PickListDto pickLists = pickListService.getPickListById(id);
        return ResponseEntity.ok(pickLists);
    }
}