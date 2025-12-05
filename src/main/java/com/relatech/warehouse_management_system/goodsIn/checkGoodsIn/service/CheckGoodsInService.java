package com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.service;

import com.relatech.warehouse_management_system.common.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.service.CheckingInfoService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import com.relatech.warehouse_management_system.goodsIn.exception.CannotAssignCIToGrnItemInClosedOrPutawayStateException;
import com.relatech.warehouse_management_system.goodsIn.exception.CannotAssignItemToGrnClosedException;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CheckGoodsInService {

    private final StockUnitService stockUnitService;
    private final CheckingInfoService checkingInfoService;
    private final GrnItemService grnItemService;
    private final GrnItemStateService stateService;

    public GrnItemDto createCheckingInfo(Long grnItemId, CheckingInfoDto ci, StockUnitDTO su) throws CannotAssignCIToGrnItemInClosedOrPutawayStateException, DuplicateResourceException, GrnExceptions.GrnItemNotFoundException, GrnExceptions.GrnNotFoundException {

        if(stateService.checkGrnItemIfCheckedOrPutaway(grnItemId)) throw new CannotAssignCIToGrnItemInClosedOrPutawayStateException(grnItemId);

        // Create StockUnit
        StockUnitDTO stockUnit = stockUnitService.createStockUnit(su);

        // Create CheckingInfo
        ci.setStockUnitId(stockUnit.getId());
        ci.setGrnItemId(grnItemId);
        CheckingInfoDto savedCI = checkingInfoService.create(ci);

        // assign to item
        grnItemService.addCheckingInfo(grnItemId, savedCI.getId());

        // progress state
        GrnItemDto item = grnItemService.getGrnItemById(grnItemId);
        stateService.evaluateAndProgressItemState(item);

        return item;
    }
}
