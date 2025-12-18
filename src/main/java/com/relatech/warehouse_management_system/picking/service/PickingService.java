package com.relatech.warehouse_management_system.picking.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.ErrorReason;
import com.relatech.warehouse_management_system.common.util.PickListItemState;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListItemService;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import com.relatech.warehouse_management_system.picking.controller.PickingController;
import com.relatech.warehouse_management_system.picking.entity.PickingInfoDto;
import com.relatech.warehouse_management_system.picking.entity.service.PickingInfoService;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PickingService {

    private final PickListService pickListService;
    private final PickListItemService pickListItemService;
    private final SlotService slotService;
    private final PickingInfoService pickingInfoService;
    private final StockUnitService stockUnitService;

    public PickListItemDto getNextPickListItem(List<Long> plIds) {
        if (plIds == null || plIds.isEmpty()) return null;

        Pageable limitOne = PageRequest.of(0, 1);
        return pickListService
                .findOpenItemsOrdered(plIds, PickListItemState.OPEN, limitOne)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void confirmPicking(PickingController.Request request) throws ResourceNotFoundException {
        PickListItemDto pickListItem = loadPickListItem(request.getPickListCode(), request.getPickListItemCode());
        SlotDto slot = slotService.getSlotByCode(pickListItem.getSlotCode());
        Map<String, StockUnitDto> stockUnitsByCode = mapStockUnitsByCode(slot.getStockUnits());

        int totalPickedQty = validatePicking(request.getStockUnitQuantities(), stockUnitsByCode, pickListItem.getQuantity());

        if (totalPickedQty == 0) {
            log.info("Nessuna quantità pickata");
            return;
        }

        ErrorReason errorReason = resolveErrorReason(request.getErrorReason(), totalPickedQty, pickListItem.getQuantity());
        executePicking(request.getStockUnitQuantities(), stockUnitsByCode, errorReason);
        updatePickListItem(pickListItem, totalPickedQty, errorReason);
    }

    /* =======================
       METODI PRIVATI
       ======================= */

    private PickListItemDto loadPickListItem(String pickListCode, String pickListItemCode) throws ResourceNotFoundException {

        PickListDto pickList = pickListService.getPickListByCode(pickListCode);

        PickListItemDto item = pickList.getPickListItemList().stream()
                .filter(i -> i.getCode().equals(pickListItemCode))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PickListItem", pickListItemCode));

        if (item.getState() != PickListItemState.OPEN) {
            throw new IllegalStateException("PickListItem non OPEN: " + item.getState());
        }
        return item;
    }

    private Map<String, StockUnitDto> mapStockUnitsByCode(List<StockUnitDto> stockUnits) {
        return stockUnits.stream().collect(Collectors.toMap(StockUnitDto::getCode, su -> su));
    }

    private int validatePicking(Map<String, Integer> requested, Map<String, StockUnitDto> stockUnits, int requiredQty) throws ResourceNotFoundException {

        int totalPicked = 0;

        for (Map.Entry<String, Integer> entry : requested.entrySet()) {

            String code = entry.getKey();
            Integer qty = entry.getValue();

            if (qty == null || qty < 0) {
                throw new IllegalArgumentException("Quantità non valida per StockUnit " + code);
            }

            StockUnitDto su = stockUnits.get(code);
            if (su == null) {
                throw new ResourceNotFoundException("StockUnit", code);
            }

            if (qty > su.getQuantity()) {
                throw new IllegalArgumentException("Quantità richiesta > disponibile per " + code);
            }

            totalPicked += qty;
        }

        if (totalPicked > requiredQty) {
            throw new IllegalArgumentException("Quantità totale pickata (" + totalPicked + ") > richiesta (" + requiredQty + ")");
        }
        return totalPicked;
    }

    private ErrorReason resolveErrorReason(ErrorReason requestReason, int pickedQty, int requiredQty) {
        if (pickedQty < requiredQty && requestReason == null) {
            return ErrorReason.MISSING_QTY;
        }
        return requestReason;
    }

    private void executePicking(Map<String, Integer> requested, Map<String, StockUnitDto> stockUnits, ErrorReason errorReason)
            throws ResourceNotFoundException {

        for (Map.Entry<String, Integer> entry : requested.entrySet()) {
            String code = entry.getKey();
            Integer qty = entry.getValue();

            if (qty == 0) continue;

            StockUnitDto su = stockUnits.get(code);
            createPickingInfo(su, qty, errorReason);
            stockUnitService.updateQuantity(code, su.getQuantity() - qty);
        }
    }

    private void updatePickListItem(PickListItemDto item, int totalPickedQty, ErrorReason errorReason) throws ResourceNotFoundException {

        if (totalPickedQty == item.getQuantity()) {
            pickListItemService.updateState(item.getCode(), PickListItemState.PICKED);
        } else {
            pickListItemService.updateQuantity(item.getCode(), item.getQuantity() - totalPickedQty);
            pickListItemService.updateErrorReason(item.getCode(), errorReason);
        }
    }

    private void createPickingInfo(StockUnitDto stockUnitDto, Integer pickedQty, ErrorReason errorReason) {

        PickingInfoDto pickingInfoDto = PickingInfoDto.builder()
                .user("USR-01QWERTY")
                .timestamp(LocalDateTime.now())
                .stockUnitCode(stockUnitDto.getCode())
                .batchNumber(stockUnitDto.getBatchNumber())
                .expirationDate(stockUnitDto.getExpirationDate())
                .quantity(pickedQty)
                .build();

        PickingInfoDto created = pickingInfoService.create(pickingInfoDto);
        log.info("Created picking info: {}", created);
    }
}
