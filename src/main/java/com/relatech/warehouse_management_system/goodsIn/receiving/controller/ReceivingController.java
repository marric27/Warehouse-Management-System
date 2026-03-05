package com.relatech.warehouse_management_system.goodsIn.receiving.controller;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.receiving.service.ReceivingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<GrnDto> createGRN(@Valid @RequestBody GrnDto dto) {

        log.info("Creating GRN");
        GrnDto result = receivingService.createGRN(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/grns/{id}")
    @Operation(summary = "Get GRN by ID")
    public ResponseEntity<GrnDto> getGRN(@PathVariable Long id) throws GrnNotFoundException {

        log.info("Fetching GRN {}", id);
        return ResponseEntity.ok(receivingService.getGRN(id));
    }

    @GetMapping("/grns/code/{code}")
    @Operation(summary = "Get GRN by code")
    public ResponseEntity<GrnDto> getGRN(@PathVariable String code) throws GrnNotFoundException {

        log.info("Fetching GRN {}", code);
        return ResponseEntity.ok(receivingService.getGRNByCode(code));
    }

    @GetMapping("/grns-all")
    @Operation(summary = "List all GRNs")
    public ResponseEntity<List<GrnDto>> listGRNs() {
        log.info("Listing all GRNs");
        return ResponseEntity.ok(receivingService.listGrn());
    }

    @GetMapping("/grns")
    @Operation(summary = "List all GRNs")
    public ResponseEntity<Page<GrnDto>> listGRNsPaged(Pageable pageable) {
        log.info("Listing all GRNs Paged");
        return ResponseEntity.ok(receivingService.listGrnPaged(pageable));
    }

    // ---------------------------
    // GRN ITEM ENDPOINTS
    // ---------------------------

    @GetMapping("/items")
    @Operation(summary = "List all items")
    public ResponseEntity<List<GrnItemDto>> listGrnItems() {
        log.info("Listing all items");
        return ResponseEntity.ok(receivingService.listGrnItems());
    }

    @GetMapping("/items-paged")
    @Operation(summary = "List all GrnItems paged")
    public ResponseEntity<Page<GrnItemDto>> listGrnItemsPaged(Pageable pageable) {
        log.info("Listing all GrnItems Paged");
        return ResponseEntity.ok(receivingService.listGrnItemsPaged(pageable));
    }

    @PostMapping("/grns/{grnCode}/items")
    @Operation(summary = "Create item for GRN")
    @ApiResponse(responseCode = "201", description = "Item created")
    public ResponseEntity<GrnItemDto> createItem(
            @PathVariable String grnCode,
            @Valid @RequestBody GrnItemDto dto) throws Exception {

        log.info("Creating item for GRN {}", grnCode);
        GrnItemDto result = receivingService.createItem(grnCode, dto);
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

    @GetMapping("/items/{itemId}")
    @Operation(summary = "Get GRN Item by ID")
    public ResponseEntity<GrnItemDto> getGrnItemById(@PathVariable Long itemId) throws Exception {
        log.info("Fetching GRN Item {}", itemId);
        GrnItemDto item = receivingService.getGrnItemById(itemId);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/items/code/{code}")
    @Operation(summary = "Get GRN Item by code")
    public ResponseEntity<GrnItemDto> getGrnItemByCode(@PathVariable String code) throws Exception {
        log.info("Fetching GRN Item {}", code);
        GrnItemDto item = receivingService.getGrnItemByCode(code);
        return ResponseEntity.ok(item);
    }
    // ---------------------------
    // STATE MANUAL CHANGE
    // ---------------------------

    @PatchMapping("/grns/{id}/state/{state}")
    @Operation(summary = "Manually change GRN state")
    public ResponseEntity<GrnDto> changeGrnState(
            @PathVariable Long id,
            @PathVariable State state) {

        log.info("Changing state of GRN {} to {}", id, state);
        throw new UnsupportedOperationException("Not implemented in ReceivingService");
    }

}
