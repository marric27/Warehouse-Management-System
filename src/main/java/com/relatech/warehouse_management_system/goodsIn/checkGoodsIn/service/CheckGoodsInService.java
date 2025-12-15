package com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.service;

import com.relatech.warehouse_management_system.common.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.CheckingInfoService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.goodsIn.GrnItemStateService;
import com.relatech.warehouse_management_system.goodsIn.exception.CannotAssignCIToGrnItemInClosedOrPutawayStateException;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnItemNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CheckGoodsInService {

    private final StockUnitService stockUnitService;
    private final CheckingInfoService checkingInfoService;
    private final GrnItemService grnItemService;
    private final GrnItemStateService stateService;

    @Transactional(rollbackFor = {CannotAssignCIToGrnItemInClosedOrPutawayStateException.class, DuplicateResourceException.class, GrnItemNotFoundException.class, GrnNotFoundException.class})
    public GrnItemDto createCheckingInfoAndStockUnit(Long grnItemId, CheckingInfoDto ci, StockUnitDto su) throws Exception {

        if(stateService.checkGrnItemIfCheckedOrPutaway(grnItemId))
            throw new CannotAssignCIToGrnItemInClosedOrPutawayStateException(grnItemId);

        // Create StockUnit
        StockUnitDto stockUnit = stockUnitService.createStockUnit(su);

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


    @Transactional(readOnly = true)
    public List<CheckingInfoDto> listCheckinginfo() {
        return checkingInfoService.getAll();
    }

    @Transactional(readOnly = true)
    public Page<CheckingInfoDto> listCIPaged(Pageable pageable) {
        return checkingInfoService.getAllPaged(pageable);
    }

    @Transactional(readOnly = true)
    public List<StockUnitDto> listStockUnit() {
        return stockUnitService.getAllStockUnits();
    }

    @Transactional(readOnly = true)
    public Page<StockUnitDto> listStockUnitPaged(Pageable pageable) {
        return stockUnitService.getAllStockUnitsPaged(pageable);
    }
}
