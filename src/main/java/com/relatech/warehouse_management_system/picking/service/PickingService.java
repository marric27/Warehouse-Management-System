package com.relatech.warehouse_management_system.picking.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.ErrorReason;
import com.relatech.warehouse_management_system.common.util.PickListItemState;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import com.relatech.warehouse_management_system.picking.controller.PickingController;
import com.relatech.warehouse_management_system.picking.entity.PickingInfo;
import com.relatech.warehouse_management_system.picking.entity.PickingInfoDto;
import com.relatech.warehouse_management_system.picking.entity.service.PickingInfoService;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class PickingService {

    private final PickListService pickListService;
    private final SlotService slotService;
    private final StockUnitService stockUnitService;
    private final PickingInfoService pickingInfoService;


    public PickListItemDto getNextPickListItem(List<Long> plIds) {
        if (plIds == null || plIds.isEmpty()) {
            return null;
        }
        Pageable limitOne = PageRequest.of(0, 1); // get first result
        List<PickListItemDto> result = pickListService.findOpenItemsOrdered(plIds, PickListItemState.OPEN, limitOne);

        if (result.isEmpty()) {
            log.info("Nessun PickListItem OPEN trovato");
            return null;
        }

        return result.getFirst();
    }


    public void confirmPicking(PickingController.Request request) throws ResourceNotFoundException {

        // controlli e creazione pickinginfo
        log.info("Faccio tutti i controlli");
        log.info("Creazione Picking info per stock unit(qty): {}", request.getStockUnitQuantities());
        check(request);

        log.info("Aggiorno tutti gli stati");
        updateStatuses();

    }

    public void check(PickingController.Request request) throws ResourceNotFoundException {
        String pickListCode = request.getPickListCode();
        String pickListItemCode = request.getPickListItemCode();
        Map<String, Integer> stockUnitQuantities = request.getStockUnitQuantities();
        ErrorReason reason = null;

        // controlla se pl esiste
        PickListDto pickList = pickListService.getPickListByCode(pickListCode);
        // implicitamente controlla se pli si trova in pl
        PickListItemDto pickListItemDto = pickList.getPickListItemList()
                .stream()
                .filter(item -> item.getCode().equals(pickListItemCode))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PickListItem", pickListItemCode));

        // controlla se esiste e si trova in pli
        SlotDto slotDto = slotService.getSlotByCode(pickListItemDto.getSlotCode());

        // stockunit in slot
        for (Map.Entry<String, Integer> entry : stockUnitQuantities.entrySet()) {

            String stockUnitCode = entry.getKey();
            Integer pickedQty = entry.getValue();

            StockUnitDto stockUnitDto = slotDto.getStockUnits().stream()
                    .filter(su -> su.getCode().equals(stockUnitCode))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("StockUnit", stockUnitCode));

            if (pickedQty < 0 || pickedQty > stockUnitDto.getQuantity()) {
                throw new IllegalArgumentException("Quantità non valida per StockUnit " + stockUnitCode);
            } else if (pickedQty > 0 && pickedQty < stockUnitDto.getQuantity()) {
                log.info("Setto la error reason obbligatoriamente poiche pickedQty < stockunitQty");
                if (request.getErrorReason() == null) reason = ErrorReason.MISSING_QTY;
                else reason = request.getErrorReason();
                // creo picking info
                createPickingInfo(stockUnitDto, pickedQty, reason);
                log.info("item qty -= qty picked");
                log.info("item error reason to be set");
            } else if (pickedQty.equals(stockUnitDto.getQuantity())) {
                log.info("itemstate = picked");
                //itemstate = picked;
                // creo picking info
                createPickingInfo(stockUnitDto, pickedQty, reason);
            } else {
                // pickedQty = 0 non creo picking info
                log.info("pickedQty = 0 quindi non creo picking info");
                return;
            }
        }

    }

    private void createPickingInfo(StockUnitDto stockUnitDto, Integer pickedQty, ErrorReason errorReason) {
        PickingInfoDto pickingInfoDto = PickingInfoDto.builder()
                .user("USR-01QWERTY")
                .timestamp(LocalDate.now())
                .stockUnitCode(stockUnitDto.getCode())
                .batchNumber(stockUnitDto.getBatchNumber())
                .expirationDate(stockUnitDto.getExpirationDate())
                .quantity(pickedQty)
                .build();

        pickingInfoService.create(pickingInfoDto);
        log.info("created pickinginfo {}", pickingInfoDto);
    }

    private void updateStatuses() {

    }
}
