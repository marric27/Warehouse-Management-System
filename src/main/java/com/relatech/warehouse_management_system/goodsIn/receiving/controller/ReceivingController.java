package com.relatech.warehouse_management_system.goodsIn.receiving.controller;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
import com.relatech.warehouse_management_system.goodsIn.receiving.service.ReceivingService;
import com.relatech.warehouse_management_system.common.util.State;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receiving")
@Tag(name = "Receiving Management", description = "Complete workflow for GRN receiving, validation, and item assignment")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ReceivingController {

    private final ReceivingService receivingService;

    @PostMapping("/grns")
    @Operation(summary = "Create new GRN", description = "Creates a new Goods Receipt Note with OPEN state")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "GRN created successfully"),
            @ApiResponse(responseCode = "409", description = "Duplicate GRN code", ref = "#/components/schemas/ApiError")
    })
    public ResponseEntity<GrnDTO> createGRN(@Valid @RequestBody GrnDTO dto) throws GrnExceptions.GrnNotFoundException, GrnExceptions.DuplicateGrnCodeException {
        log.info("POST /receiving/grns - Creating GRN with code: {}", dto.getCode());
        GrnDTO created = receivingService.createGRN(dto);
        log.info("GRN created successfully with ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/grns")
    @Operation(summary = "List all GRNs", description = "Retrieves list of all GRNs")
    @ApiResponse(responseCode = "200", description = "GRNs retrieved successfully")
    public ResponseEntity<List<GrnDTO>> listGRNs() {
        log.info("GET /receiving/grns - Fetching all GRNs");
        List<GrnDTO> grns = receivingService.listGRNs();
        log.info("Retrieved {} GRNs", grns.size());
        return ResponseEntity.ok(grns);
    }

    @GetMapping("/grns/{id}")
    @Operation(summary = "Get GRN by ID", description = "Retrieves a specific GRN with all items")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "GRN found"),
            @ApiResponse(responseCode = "404", description = "GRN not found")
    })
    public ResponseEntity<GrnDTO> getGRN(
            @Parameter(description = "GRN ID") @PathVariable Long id)
            throws GrnExceptions.GrnNotFoundException {
        log.info("GET /receiving/grns/{} - Fetching GRN", id);
        GrnDTO dto = receivingService.readGRN(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/grns/{id}")
    @Operation(summary = "Update GRN", description = "Updates supplier and receiving date of GRN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "GRN updated"),
            @ApiResponse(responseCode = "404", description = "GRN not found")
    })
    public ResponseEntity<GrnDTO> updateGRN(
            @Parameter(description = "GRN ID") @PathVariable Long id,
            @Valid @RequestBody GrnDTO dto)
            throws GrnExceptions.GrnNotFoundException {
        log.info("PUT /receiving/grns/{} - Updating GRN", id);
        GrnDTO updated = receivingService.updateGRN(id, dto);
        log.info("GRN {} updated successfully", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/grns/{id}")
    @Operation(summary = "Delete GRN", description = "Deletes GRN only if no items assigned")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "GRN deleted"),
            @ApiResponse(responseCode = "404", description = "GRN not found"),
            @ApiResponse(responseCode = "409", description = "GRN has items")
    })
    public ResponseEntity<Void> deleteGRN(
            @Parameter(description = "GRN ID") @PathVariable Long id)
            throws GrnExceptions.GrnNotFoundException, GrnExceptions.GrnWithItemsException {
        log.info("DELETE /receiving/grns/{} - Deleting GRN", id);
        receivingService.deleteGRN(id);
        log.info("GRN {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grns/search")
    @Operation(summary = "Search GRNs", description = "Search GRNs by code, supplier, or other term")
    @ApiResponse(responseCode = "200", description = "Search results")
    public ResponseEntity<List<GrnDTO>> searchGRNs(
            @Parameter(description = "Search term")
            @RequestParam(name = "term", required = false) String term) {
        log.info("GET /receiving/grns/search - Searching with term: '{}'", term);
        List<GrnDTO> results = receivingService.searchGRNs(term);
        log.info("Found {} GRNs matching term '{}'", results.size(), term);
        return ResponseEntity.ok(results);
    }


    // GRN ITEM ENDPOINTS

    @PostMapping("/grns/{grnId}/items")
    @Operation(summary = "Create GRN Item", description = "Adds new item to GRN with validation")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item created"),
            @ApiResponse(responseCode = "404", description = "GRN not found"),
            @ApiResponse(responseCode = "422", description = "Invalid quantity")
    })
    public ResponseEntity<GrnDTO> createGRNItem(
            @Parameter(description = "GRN ID") @PathVariable Long grnId,
            @Valid @RequestBody GrnItemDto dto)
            throws GrnExceptions.InvalidQuantityException,
            GrnExceptions.QuantityMismatchException, GrnExceptions.OverReceivedQuantityException, GrnExceptions.GrnItemNotFoundException, GrnExceptions.GrnNotFoundException, GrnExceptions.DuplicateGrnCodeException {
        log.info("POST /receiving/grns/{}/items - Creating item for GRN", grnId);
        //GrnDTO created = receivingService.createItemForGrn(grnId, dto);
        GrnDTO created = receivingService.createGRNItemAndAssignToGrn(grnId, dto);
        log.info("Item {} created for GRN {}", dto.getCode(), grnId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/grns/{grnId}/items")
    @Operation(summary = "List GRN Items", description = "Retrieves all items for specific GRN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Items retrieved"),
            @ApiResponse(responseCode = "404", description = "GRN not found")
    })
    public ResponseEntity<List<GrnItemDto>> listGRNItems(
            @Parameter(description = "GRN ID") @PathVariable Long grnId)
            throws GrnExceptions.GrnNotFoundException {
        log.info("GET /receiving/grns/{}/items - Fetching items", grnId);
        List<GrnItemDto> items = receivingService.listGRNItems(grnId);
        log.info("Retrieved {} items for GRN {}", items.size(), grnId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/{itemId}")
    @Operation(summary = "Get GRN Item", description = "Retrieves specific GRN item details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item found"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<GrnItemDto> getGRNItem(
            @Parameter(description = "Item ID") @PathVariable Long itemId)
            throws GrnExceptions.GrnItemNotFoundException {
        log.info("GET /receiving/items/{} - Fetching item", itemId);
        GrnItemDto item = receivingService.readGRNItem(itemId);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update GRN Item", description = "Updates item quantities and state")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item updated"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "422", description = "Invalid quantity")
    })
    public ResponseEntity<GrnItemDto> updateGRNItem(
            @Parameter(description = "Item ID") @PathVariable Long itemId,
            @Valid @RequestBody GrnItemDto dto)
            throws GrnExceptions.GrnItemNotFoundException, GrnExceptions.InvalidQuantityException,
            GrnExceptions.QuantityMismatchException, GrnExceptions.OverReceivedQuantityException {
        log.info("PUT /receiving/items/{} - Updating item", itemId);
        GrnItemDto updated = receivingService.updateGRNItem(itemId, dto);
        log.info("Item {} updated successfully", itemId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Delete GRN Item", description = "Removes item from GRN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item deleted"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<Void> deleteGRNItem(
            @Parameter(description = "Item ID") @PathVariable Long itemId)
            throws GrnExceptions.GrnItemNotFoundException {
        log.info("DELETE /receiving/items/{} - Deleting item", itemId);
        receivingService.deleteGRNItem(itemId);
        log.info("Item {} deleted successfully", itemId);
        return ResponseEntity.noContent().build();
    }


    // CHECKING INFO ASSIGNMENT

    @PostMapping("/items/{itemId}/checking")
    @Operation(summary = "Assign Checking Info", description = "Assigns checking information to GRN item and auto-transitions state if complete")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Checking info assigned"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "422", description = "Quantity mismatch")
    })
    public ResponseEntity<GrnItemDto> assignCheckingInfo(
            @Parameter(description = "Item ID") @PathVariable Long itemId,
            @Parameter(description = "List of Checking Info IDs")
            @RequestBody List<Long> checkingInfoIds)
            throws GrnExceptions.GrnItemNotFoundException, GrnExceptions.QuantityMismatchException {
        log.info("POST /receiving/items/{}/checking - Assigning {} checking infos", itemId, checkingInfoIds.size());
        GrnItemDto assigned = receivingService.assignCheckingInfoToItem(itemId, checkingInfoIds);
        log.info("Checking info assigned to item {}", itemId);
        return ResponseEntity.ok(assigned);
    }

    // STATE MANAGEMENT


    @PatchMapping("/grns/{id}/state/{state}")
    @Operation(summary = "Change GRN State", description = "Transitions GRN to new state (OPEN → CHECKED → PUTAWAY → CLOSED)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "State changed"),
            @ApiResponse(responseCode = "404", description = "GRN not found"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition")
    })
    public ResponseEntity<GrnDTO> changeGRNState(
            @Parameter(description = "GRN ID") @PathVariable Long id,
            @Parameter(description = "New state (OPEN, CHECKED, PUTAWAY, CLOSED)")
            @PathVariable State state)
            throws GrnExceptions.GrnNotFoundException, GrnExceptions.InvalidStateTransitionException {
        log.info("PATCH /receiving/grns/{}/state/{} - Changing GRN state", id, state);
        GrnDTO updated = receivingService.changeGRNState(id, state);
        log.info("GRN {} state changed to {}", id, state);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/items/{id}/state/{state}")
    @Operation(summary = "Change GRN Item State", description = "Transitions item to new state")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "State changed"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition")
    })
    public ResponseEntity<GrnItemDto> changeItemState(
            @Parameter(description = "Item ID") @PathVariable Long id,
            @Parameter(description = "New state (OPEN, CHECKED, PUTAWAY, CLOSED)")
            @PathVariable State state)
            throws GrnExceptions.GrnItemNotFoundException, GrnExceptions.InvalidStateTransitionException {
        log.info("PATCH /receiving/items/{}/state/{} - Changing item state", id, state);
        GrnItemDto updated = receivingService.changeItemState(id, state);
        log.info("Item {} state changed to {}", id, state);
        return ResponseEntity.ok(updated);
    }
}