package com.relatech.warehouse_management_system.goodsIn.putaway.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.*;
import com.relatech.warehouse_management_system.goodsIn.entity.service.*;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
import com.relatech.warehouse_management_system.goodsIn.exception.UpdateEntityException;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDTO;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PutawayService {

    private final SlotService slotService;
    private final StockUnitService stockUnitService;
    private final CheckingInfoService checkingInfoService;
    private final GrnItemService grnItemService;
    private final GrnItemStateService stateService;

    public SlotDTO assignStockUnitToSlot(Long stockUnitId, Long slotId) throws ResourceNotFoundException, UpdateEntityException, GrnExceptions.GrnItemNotFoundException, GrnExceptions.GrnNotFoundException {

        SlotDTO slot = slotService.getSlotById(slotId);
        StockUnitDTO su = stockUnitService.getStockUnitById(stockUnitId);

        if (!slot.getAllowedCategory().equals(su.getCategory()))
            throw new IllegalArgumentException("Category mismatch");

        slot.getStockUnits().add(su);
        su.setSlotId(slot.getId());
        SlotDTO savedSlot = slotService.updateSlot(slotId, slot);

        // update checkingInfo state → triggers item state change
        CheckingInfoDto ci = checkingInfoService.getByStockUnitId(stockUnitId);
        ci.setState(State.PUTAWAY);
        checkingInfoService.update(ci.getId(), ci);

        GrnItemDto item = grnItemService.getGrnItemById(ci.getGrnItemId());
        stateService.evaluateAndProgressItemState(item);

        return savedSlot;
    }

    // TODO togliere?
    public List<SlotDTO> listSlots() {
        return slotService.getAllSlots();
    }

    public SlotDTO getSlot(Long id) throws ResourceNotFoundException {
        return slotService.getSlotById(id);
    }

    public SlotDTO createSlot(SlotDTO slot) {
        return slotService.createSlot(slot);
    }

    public SlotDTO updateSlot(Long id, SlotDTO slot)
            throws ResourceNotFoundException, UpdateEntityException {
        return slotService.updateSlot(id, slot);
    }

    public void deleteSlot(Long id) throws ResourceNotFoundException {
        slotService.deleteSlot(id);
    }


}
