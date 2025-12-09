package com.relatech.warehouse_management_system.warehouse.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.exception.UpdateEntityException;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SlotService {
    List<SlotDTO> getAllSlots();
    Page<SlotDTO> getAllSlotsPaged(Pageable pageable);
    SlotDTO getSlotById(Long slotId) throws ResourceNotFoundException;
    SlotDTO createSlot(SlotDTO slotDTO);
    SlotDTO updateSlot(Long slotId, SlotDTO slotDTO) throws ResourceNotFoundException, UpdateEntityException;
    void deleteSlot(Long slotId) throws ResourceNotFoundException;
}