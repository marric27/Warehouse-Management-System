package com.relatech.warehouse_management_system.logistic.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDTO;

public interface LogisticService {
    SlotDTO assignProductToSlot(Long slotId, Long productId) throws ResourceNotFoundException;
    SlotDTO removeProductFromSlot(Long slotId) throws ResourceNotFoundException;
    boolean canSlotContainProduct(Long slotId, Long productId) throws ResourceNotFoundException;
}
