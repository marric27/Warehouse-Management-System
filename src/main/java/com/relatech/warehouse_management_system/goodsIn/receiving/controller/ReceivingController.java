package com.relatech.warehouse_management_system.goodsIn.receiving.controller;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
import com.relatech.warehouse_management_system.goodsIn.receiving.service.ReceivingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/receiving")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Receiving", description = "GRN creation and item receiving workflow")
public class ReceivingController {

    private final ReceivingService receivingService;

    // ---------------------------
    // GRN ENDPOINTS
    // ---------------------------

    @PostMapping("/grns")
    @Operation(summary = "Create GRN")
    @ApiResponse(responseCode = "201", description = "GRN created")
    public ResponseEntity<GrnDTO> createGRN(@Valid @RequestBody GrnDTO dto)
            throws GrnExceptions.DuplicateGrnCodeException {

        log.info("Creating GRN {}", dto.getCode());
        GrnDTO result = receivingService.createGRN(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/grns/{id}")
    @Operation(summary = "Get GRN by ID")
    public ResponseEntity<GrnDTO> getGRN(@PathVariable Long id)
            throws GrnExceptions.GrnNotFoundException {

        log.info("Fetching GRN {}", id);
        return ResponseEntity.ok(receivingService.getGRN(id));
    }

    @GetMapping("/grns")
    @Operation(summary = "List all GRNs")
    public ResponseEntity<List<GrnDTO>> listGRNs() {
        log.info("Listing all GRNs");
        return ResponseEntity.ok(receivingService.list());
    }

    // ---------------------------
    // GRN ITEM ENDPOINTS
    // ---------------------------

    @PostMapping("/grns/{grnId}/items")
    @Operation(summary = "Create item for GRN")
    @ApiResponse(responseCode = "201", description = "Item created")
    public ResponseEntity<GrnItemDto> createItem(
            @PathVariable Long grnId,
            @Valid @RequestBody GrnItemDto dto) throws Exception {

        log.info("Creating item {} for GRN {}", dto.getCode(), grnId);
        GrnItemDto result = receivingService.createItem(grnId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update item")
    public ResponseEntity<GrnItemDto> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody GrnItemDto dto) throws Exception {

        log.info("Updating item {}", itemId);
        GrnItemDto updated = receivingService.updateItem(itemId, dto);
        return ResponseEntity.ok(updated);
    }

    // ---------------------------
    // STATE MANUAL CHANGE
    // ---------------------------

    @PatchMapping("/grns/{id}/state/{state}")
    @Operation(summary = "Manually change GRN state")
    public ResponseEntity<GrnDTO> changeGrnState(
            @PathVariable Long id,
            @PathVariable State state)
            throws GrnExceptions.GrnNotFoundException, GrnExceptions.InvalidStateTransitionException {

        log.info("Changing state of GRN {} to {}", id, state);
        throw new UnsupportedOperationException("Not implemented in ReceivingService");
    }

}
