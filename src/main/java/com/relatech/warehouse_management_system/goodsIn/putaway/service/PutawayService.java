package com.relatech.warehouse_management_system.goodsIn.putaway.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.CheckingInfoService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnItemNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.exception.UpdateEntityException;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PutawayService {

    private final SlotService slotService;
    private final StockUnitService stockUnitService;
    private final CheckingInfoService checkingInfoService;
    private final GrnItemService grnItemService;
    private final GrnItemStateService stateService;


    @Transactional(rollbackFor = {ResourceNotFoundException.class, UpdateEntityException.class, GrnItemNotFoundException.class}, propagation = Propagation.REQUIRES_NEW)
    public SlotDto assignStockUnitToSlot(Long stockUnitId, Long slotId) throws ResourceNotFoundException, UpdateEntityException, GrnItemNotFoundException, GrnNotFoundException {

        SlotDto slot = slotService.getSlotById(slotId);
        StockUnitDto su = stockUnitService.getStockUnitById(stockUnitId);

        if (!slot.getAllowedCategory().equals(su.getCategory()))
            throw new IllegalArgumentException("Category mismatch");
        if (su.getQuantity() >= slot.getCapacity())
            throw new IllegalArgumentException("Slot is full"); // TODO aggiungere colonna available capacity in slot

        slot.getStockUnits().add(su);
        su.setSlotId(slot.getId());
        SlotDto savedSlot = slotService.updateSlot(slotId, slot);

        // update checkingInfo state → triggers item state change
        CheckingInfoDto ci = checkingInfoService.getByStockUnitId(stockUnitId);
        ci.setState(State.PUTAWAY);
        checkingInfoService.update(ci.getId(), ci);

        GrnItemDto item = grnItemService.getGrnItemById(ci.getGrnItemId());
        stateService.evaluateAndProgressItemState(item);

        return savedSlot;
    }
}
