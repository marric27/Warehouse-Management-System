package com.relatech.warehouse_management_system.goodsIn.putaway.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.*;
import com.relatech.warehouse_management_system.goodsIn.entity.service.*;
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
    private final GrnService grnService;

    @Transactional
    public SlotDTO assignStockUnitToSlot(Long stockUnitId, Long slotId) throws ResourceNotFoundException, UpdateEntityException, GrnExceptions.GrnItemNotFoundException, GrnExceptions.GrnNotFoundException {
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

    private void updateCheckinfoState(Long stockUnitId) throws ResourceNotFoundException, GrnExceptions.GrnItemNotFoundException, GrnExceptions.GrnNotFoundException {
        CheckingInfoDto toUpdate = checkingInfoService.getByStockUnitId(stockUnitId);

        updateCheckingInfoState(toUpdate.getId(), State.PUTAWAY);

        GrnItemDto itemDto = grnItemService.getGrnItemById(toUpdate.getGrnItemId());
        checkAssignedQuantity(itemDto);
    }

    public void checkAssignedQuantity(GrnItemDto item) throws GrnExceptions.GrnItemNotFoundException, GrnExceptions.GrnNotFoundException {
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
            checkGrnState(item.getGrnId());
        }
    }

    public void checkGrnState(Long grnId) throws GrnExceptions.GrnNotFoundException {
        GrnDTO grnDTO = grnService.getGRNById(grnId);
        boolean allItemsPutaway = grnDTO.getItems().stream()
                .allMatch(i -> i.getState() == State.PUTAWAY);

        if (allItemsPutaway) {
            grnDTO.setState(State.CLOSED);
            grnService.updateGRN(grnId, grnDTO);
            log.info("All GrnItems for GRN {} are PUTAWAY → auto CLOSED", grnId);
        }
    }

    @Transactional
    public void updateCheckingInfoState(Long checkingInfoId, State newState) throws ResourceNotFoundException {
        log.info("Updating checkinginfo {} to state {}", checkingInfoId, newState);
        CheckingInfoDto ci = checkingInfoService.getById(checkingInfoId);

        ci.setState(newState);
        checkingInfoService.update(checkingInfoId, ci);
        log.info("Updated checkinginfo {} to state {}", checkingInfoId, newState);
    }

}
