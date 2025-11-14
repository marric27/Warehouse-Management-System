package com.relatech.warehouse_management_system.logistic.service;

import com.relatech.warehouse_management_system.slot.dto.SlotDTO;

public interface LogisticService {
    SlotDTO assignProductToSlot(Long slotId, Long productId);
    SlotDTO removeProductFromSlot(Long slotId);
    boolean canSlotContainProduct(Long slotId, Long productId);
}
