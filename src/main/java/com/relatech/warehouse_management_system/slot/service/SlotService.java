package com.relatech.warehouse_management_system.slot.service;

import com.relatech.warehouse_management_system.exception.EntityNotFoundException;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;

import java.util.List;

public interface SlotService {
    List<SlotDTO> getAllSlots();
    SlotDTO getSlotById(Long slotId) throws EntityNotFoundException;
    SlotDTO createSlot(SlotDTO slotDTO);
    SlotDTO updateSlot(Long slotId, SlotDTO slotDTO) throws Exception;
    void deleteSlot(Long slotId) throws EntityNotFoundException;
}