package com.relatech.warehouse_management_system.grnItem.controller;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;
import com.relatech.warehouse_management_system.grnItem.service.GrnItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grn-items")
public class GrnItemController {

    @Autowired
    private GrnItemService grnItemService;

    @PostMapping
    public ResponseEntity<GrnItemDto> createGrnItem(@Valid @RequestBody GrnItemDto grnItemDto) {
        GrnItemDto created = grnItemService.createGrnItem(grnItemDto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<GrnItemDto>> getAllGrnItems() {
        return ResponseEntity.ok(grnItemService.getAllGrnItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrnItemDto> getGrnItemById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(grnItemService.getGrnItemById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrnItemDto> updateGrnItem(
            @PathVariable Long id,
            @Valid @RequestBody GrnItemDto grnItemDto
    ) throws ResourceNotFoundException {

        GrnItemDto updated = grnItemService.updateGrnItem(id, grnItemDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrnItem(@PathVariable Long id) throws ResourceNotFoundException {
        grnItemService.deleteGrnItem(id);
        return ResponseEntity.noContent().build();
    }
}
