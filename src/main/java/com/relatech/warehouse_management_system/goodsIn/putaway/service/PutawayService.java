package com.relatech.warehouse_management_system.goodsIn.putaway.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.CheckingInfoService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.goodsIn.event.CheckingInfoPutawayAssignedEvent;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnItemNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.exception.UpdateEntityException;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher publisher;


    @Transactional(rollbackFor = {ResourceNotFoundException.class, UpdateEntityException.class, GrnItemNotFoundException.class}, propagation = Propagation.REQUIRES_NEW)
    public SlotDto assignStockUnitToSlot(String stockUnitCode, String slotCode) throws ResourceNotFoundException, UpdateEntityException, GrnItemNotFoundException, GrnNotFoundException {

        SlotDto slot = slotService.getSlotByCode(slotCode);
        StockUnitDto su = stockUnitService.getStockUnitByCode(stockUnitCode);

        if (!slot.getCategory().equals(su.getCategory()))
            throw new IllegalArgumentException("Category mismatch");

        slot.getStockUnits().add(su);
        su.setSlotId(slot.getId());
        SlotDto savedSlot = slotService.updateSlot(slot.getId(), slot);

        // update checkingInfo state → triggers item state change
        CheckingInfoDto ci = checkingInfoService.getByStockUnitId(su.getId());
        ci.setState(State.PUTAWAY);
        checkingInfoService.update(ci.getId(), ci);

        publisher.publishEvent(new CheckingInfoPutawayAssignedEvent(ci.getGrnItemId(), ci.getId()));

        return savedSlot;
    }
}
