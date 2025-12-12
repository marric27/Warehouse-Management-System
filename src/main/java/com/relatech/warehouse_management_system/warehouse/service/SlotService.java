package com.relatech.warehouse_management_system.warehouse.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.exception.UpdateEntityException;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SlotService {
    List<SlotDto> getAllSlots();
    Page<SlotDto> getAllSlotsPaged(Pageable pageable);
    SlotDto getSlotById(Long slotId) throws ResourceNotFoundException;
    SlotDto createSlot(SlotDto slotDTO);
    SlotDto updateSlot(Long slotId, SlotDto slotDTO) throws ResourceNotFoundException, UpdateEntityException;
    void deleteSlot(Long slotId) throws ResourceNotFoundException;

    Optional<SlotDto> getSlotContainingProduct(String productCode, int requiredQuantity) throws ResourceNotFoundException;
}