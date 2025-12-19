package com.relatech.warehouse_management_system.picking.service;

import com.relatech.warehouse_management_system.common.exception.MatchingDifferentCategoryException;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.ErrorReason;
import com.relatech.warehouse_management_system.common.util.PickListItemState;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.goodsIn.exception.QuantityMismatchException;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListItemService;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import com.relatech.warehouse_management_system.picking.dto.ConfirmPickingRequest;
import com.relatech.warehouse_management_system.picking.dto.NextItemRequest;
import com.relatech.warehouse_management_system.picking.entity.PickingInfoDto;
import com.relatech.warehouse_management_system.picking.entity.service.PickingInfoService;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    public PickListItemDto getNextPickListItem(NextItemRequest request) {
        List<Long> plIds = request.getPickListIds();

        Pageable limitOne = PageRequest.of(0, 1);
        return pickListService.findItemsByStateOrdered(plIds, PickListItemState.OPEN, limitOne)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Transactional(rollbackFor = {ResourceNotFoundException.class, QuantityMismatchException.class}, propagation = Propagation.REQUIRED)
    public void confirmPicking(ConfirmPickingRequest request) throws Exception {
        PickListItemDto pickListItem = loadPickListItem(request.getPickListCode(), request.getPickListItemCode());
        int toPick = request.getStockUnitQuantities().values().stream().mapToInt(Integer::intValue).sum();
        if(toPick > pickListItem.getQuantity()) throw new QuantityMismatchException("Requested quantity > available qty");

        ErrorReason errorReason;
        if(toPick < pickListItem.getQuantity() && request.getErrorReason() != null) {
            log.info("Set error reason from request");
            errorReason = request.getErrorReason();
        } else if(toPick == pickListItem.getQuantity()) {
            errorReason = null;
        } else {
            throw new Exception("Error reason cant be omitted when qty to pick is lower than ");
        }

        SlotDto slot = slotService.getSlotByCode(pickListItem.getSlotCode());
        Map<String, StockUnitDto> stockUnitsByCode = slot.getStockUnits().stream().collect(Collectors.toMap(StockUnitDto::getCode, su -> su));
        canPickFromSU(request.getStockUnitQuantities(), stockUnitsByCode, pickListItem);

        executePicking(request.getStockUnitQuantities(), stockUnitsByCode, pickListItem);
        updatePickListItem(pickListItem, toPick, errorReason);
    }

    ////////////////////////////////////////////////
    ////////////////// VALIDATION //////////////////
    ////////////////////////////////////////////////
    private PickListItemDto loadPickListItem(String pickListCode, String pickListItemCode) throws ResourceNotFoundException {

        PickListDto pickList = pickListService.getPickListByCode(pickListCode);

        PickListItemDto item = pickList.getPickListItemList().stream()
                .filter(i -> i.getCode().equals(pickListItemCode))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PickListItem", pickListItemCode));

        if (item.getState() != PickListItemState.OPEN) {
            throw new IllegalStateException("PickListItem is not OPEN: " + item.getState());
        }
        return item;
    }

    public void canPickFromSU(Map<String, Integer> requested, Map<String, StockUnitDto> stockUnits, PickListItemDto pickListItem) throws ResourceNotFoundException, QuantityMismatchException, MatchingDifferentCategoryException {
        for (Map.Entry<String, Integer> entry : requested.entrySet()) {
            String code = entry.getKey();
            Integer qty = entry.getValue();

            StockUnitDto su = stockUnits.get(code);
            if (su == null) {
                throw new ResourceNotFoundException("StockUnit", code);
            }

            if (!su.getProductCode().equals(pickListItem.getProductCode())) {
                throw new MatchingDifferentCategoryException(
                        String.format(
                                "StockUnit %s contains product %s but PickListItem requires product %s",
                                code,
                                su.getProductCode(),
                                pickListItem.getProductCode()
                        )
                );
            }

            if (qty > su.getQuantity()) {
                throw new QuantityMismatchException("Requested quantity > available qty for stock unit: " + code);
            }
        }
    }

    /////////////////////////////////////////////////////
    ////////////////// PICK AND UPDATE //////////////////
    /////////////////////////////////////////////////////
    private void executePicking(Map<String, Integer> requested, Map<String, StockUnitDto> stockUnits, PickListItemDto pickListItem) throws ResourceNotFoundException {
        for (Map.Entry<String, Integer> entry : requested.entrySet()) {
            String code = entry.getKey();
            Integer qty = entry.getValue();

            StockUnitDto su = stockUnits.get(code);
            int oldQty = su.getQuantity();
            su.setQuantity(oldQty - qty);
            stockUnitService.updateStockUnit(su.getId(), su);
            createPickingInfo(su, qty, pickListItem);
        }
    }

    private void updatePickListItem(PickListItemDto item, int totalPickedQty, ErrorReason errorReason) throws ResourceNotFoundException {

        int pickedQty = item.getPickedQty() + totalPickedQty;
        item.setPickedQty(pickedQty);
        if (pickedQty == item.getQuantity()) {
            item.setState(PickListItemState.PICKED);
            pickListItemService.update(item.getCode(), item);
        } else {
            item.setErrorReason(errorReason);
            pickListItemService.update(item.getCode(), item);
        }
    }

    private void createPickingInfo(StockUnitDto stockUnitDto, Integer pickedQty, PickListItemDto pickListItem) {

        PickingInfoDto pickingInfoDto = PickingInfoDto.builder()
                .user("USR-01QWERTY")
                .timestamp(LocalDateTime.now())
                .stockUnitCode(stockUnitDto.getCode())
                .batchNumber(stockUnitDto.getBatchNumber())
                .expirationDate(stockUnitDto.getExpirationDate())
                .quantity(pickedQty)
                .pickListItemId(pickListItem.getId())
                .build();

        PickingInfoDto created = pickingInfoService.create(pickingInfoDto);
        log.info("Created picking info: {}", created);
    }
}
