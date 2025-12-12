package com.relatech.warehouse_management_system.logistic.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;

public interface LogisticService {
    SlotDto assignProductToSlot(Long slotId, Long productId) throws ResourceNotFoundException;
    SlotDto removeProductFromSlot(Long slotId) throws ResourceNotFoundException;
    boolean canSlotContainProduct(Long slotId, Long productId) throws ResourceNotFoundException;
}
