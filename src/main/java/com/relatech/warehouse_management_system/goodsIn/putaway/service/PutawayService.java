package com.relatech.warehouse_management_system.goodsIn.putaway.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.dto.SlotDTO;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.CheckingInfo;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.service.CheckingInfoService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.SlotService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
import com.relatech.warehouse_management_system.goodsIn.exception.UpdateEntityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PutawayService {

    private final SlotService slotService;
    private final StockUnitService stockUnitService;
    private final CheckingInfoService checkingInfoService;
    private final GrnItemService grnItemService;

    @Transactional
    public SlotDTO assignStockUnitToSlot(Long stockUnitId, Long slotId) throws ResourceNotFoundException, UpdateEntityException, GrnExceptions.GrnItemNotFoundException {
        SlotDTO slotDTO = slotService.getSlotById(slotId);
        StockUnitDTO stockUnitDTO = stockUnitService.getStockUnitById(stockUnitId);

        if (!slotDTO.getAllowedCategory().equals(stockUnitDTO.getCategory())) {
            throw new IllegalArgumentException("StockUnit category not allowed in this Slot");
        }

        slotDTO.getStockUnits().add(stockUnitDTO);
        stockUnitDTO.setSlotId(slotDTO.getId());

        SlotDTO saved = slotService.updateSlot(slotId, slotDTO);
        updateCheckinfoState(stockUnitId);
        return saved;
    }

    private void updateCheckinfoState(Long stockUnitId) throws ResourceNotFoundException, GrnExceptions.GrnItemNotFoundException {
        CheckingInfoDto toUpdate = checkingInfoService.getByStockUnitId(stockUnitId);

        checkingInfoService.updateCheckingInfoState(toUpdate.getId(), State.PUTAWAY);

        GrnItemDto itemDto = grnItemService.getGrnItemById(toUpdate.getGrnItemId());
        checkAssignedQuantity(itemDto);
    }

    public void checkAssignedQuantity(GrnItemDto item) throws GrnExceptions.GrnItemNotFoundException {
        //GrnItemDto item = grnItemService.getGrnItemById(grnItemId);
        int expected = item.getExpectedQty();
        List<CheckingInfoDto> checkingInfos = item.getCheckingInfoList();
        int assigned = (checkingInfos == null || checkingInfos.isEmpty()) ? 0 :
                checkingInfos.stream().mapToInt(CheckingInfoDto::getQuantity).sum();

        State currentState = item.getState() != null ? item.getState() : State.OPEN;

        // OPEN → CHECKED
        if (assigned >= expected && currentState == State.OPEN) {
            log.info("Item {} complete → auto CHECKED", item.getId());
            item.setState(State.CHECKED);
            grnItemService.updateGrnItem(item.getId(), item);
            currentState = State.CHECKED; // update local state
        }

        // CHECKED → PUTAWAY
        if (currentState == State.CHECKED
                && checkingInfos != null
                && !checkingInfos.isEmpty()
                && checkingInfos.stream().allMatch(ci -> ci.getState() == State.PUTAWAY)) {

            log.info("All checkingInfos for item {} are PUTAWAY → auto PUTAWAY", item.getId());
            item.setState(State.PUTAWAY);
            grnItemService.updateGrnItem(item.getId(), item);
        }
    }






    //SlotDTO removeStockUnitFromSlot(Long slotId, Long stockUnitId) throws ResourceNotFoundException;


}
