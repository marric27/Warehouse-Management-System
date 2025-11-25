package com.relatech.warehouse_management_system.GRN.controller;

import com.relatech.warehouse_management_system.GRN.dto.GrnDTO;
import com.relatech.warehouse_management_system.GRN.mapper.GrnMapper;
import com.relatech.warehouse_management_system.GRN.service.GrnService;
import com.relatech.warehouse_management_system.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.exception.GrnWithItemsException;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Goods Receipt Notes.
 */
@RestController
@RequestMapping("/grn")
@Tag(name = "GRN Management", description = "APIs for managing Goods Receipt Notes")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GrnController {

    private final GrnService grnService;
    private final GrnMapper grnMapper;

    @PostMapping
    public ResponseEntity<GrnDTO> createGRN(@Valid @RequestBody GrnDTO dto) throws DuplicateResourceException {
        log.info("Received POST request to create GRN for supplier: {}", dto.getSupplier());
        GrnDTO created = grnService.createGRN(dto);
        log.info("GRN created with ID: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<Page<GrnDTO>> getAllGRNsPaged(
            @ParameterObject
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        log.info("Received GET request for paginated GRNs: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<GrnDTO> grnPage = grnService.getAllGRNsPaged(pageable);

        log.info("Returning {} GRNs (page {})",
                grnPage.getNumberOfElements(), pageable.getPageNumber());

        return ResponseEntity.ok(grnPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrnDTO> getGRNById(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Received GET request for GRN with ID: {}", id);
        GrnDTO dto = grnService.getGRNById(id);
        log.info("Returning GRN: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<Page<GrnItemDto>> getGRNItems(
            @PathVariable Long id,
            @ParameterObject
            @PageableDefault(size = 50) Pageable pageable
    ) throws ResourceNotFoundException {

        log.info("Received GET request for items of GRN {} (page size: {})",
                id, pageable.getPageSize());

        Page<GrnItemDto> itemsPage = grnService.findItemsByGrnId(id, pageable);

        log.info("Returning {} items for GRN {}",
                itemsPage.getNumberOfElements(), id);

        return ResponseEntity.ok(itemsPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrnDTO> updateGRN(@PathVariable Long id, @Valid @RequestBody GrnDTO dto)
            throws ResourceNotFoundException {

        log.info("Received PUT request to update GRN with ID: {}", id);
        GrnDTO updated = grnService.updateGRN(id, dto);
        log.info("GRN {} updated successfully", id);

        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<GrnDTO> updateGRNStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) throws ResourceNotFoundException {

        log.info("Received PATCH request to update status of GRN {} to {}", id, status);
        GrnDTO updated = grnService.updateStatus(id, status);
        log.info("Status of GRN {} updated to {}", id, status);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGRN(@PathVariable Long id)
            throws ResourceNotFoundException, GrnWithItemsException {

        log.info("Received DELETE request for GRN with ID: {}", id);
        grnService.deleteById(id);
        log.info("GRN {} deleted successfully", id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<GrnDTO>> searchGrns(@RequestParam(name = "term", required = false) String term) {
        log.info("Received GET request to search GRNs with term: '{}'", term);
        List<GrnDTO> results = grnService.searchGrns(term);
        log.info("Found {} GRNs matching term '{}'", results.size(), term);

        return ResponseEntity.ok(results);
    }

    @PatchMapping("/{id}/items")
    public ResponseEntity<GrnDTO> assignItems(@PathVariable Long id, @RequestBody List<Long> itemIds) throws ResourceNotFoundException {
        log.info("Received PATCH request for assign grn items {} to grn with ID {} ", itemIds, id);
        GrnDTO grnDTO = grnService.addItemsToGrn(id, itemIds);
        log.info("Successfully assigned grn items: {} to grn: {}", itemIds, id);
        return ResponseEntity.ok(grnDTO);
    }

}
