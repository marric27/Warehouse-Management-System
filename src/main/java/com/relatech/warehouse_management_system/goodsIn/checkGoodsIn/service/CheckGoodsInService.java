package com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.service;

import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.service.CheckingInfoService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.goodsIn.putaway.service.PutawayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CheckGoodsInService {

    private final CheckingInfoService checkingInfoService;
    private final StockUnitService stockUnitService;
    private final GrnItemService grnItemService;
    private final PutawayService putawayService;

    @Transactional
    public GrnItemDto createCheckingInfoWithStockUnitAndAssignToGrnItem(CheckingInfoDto checkingInfoDto, StockUnitDTO stockUnitDTO, Long grnItemId) throws Exception {

        GrnItemDto grnItem = grnItemService.getGrnItemById(grnItemId);

        StockUnitDTO savedStockUnit = stockUnitService.createStockUnit(stockUnitDTO);
        log.info("Created stockunit with ID: {}", savedStockUnit.getId());

        checkingInfoDto.setGrnItemId(grnItemId);
        checkingInfoDto.setStockUnitId(savedStockUnit.getId());
        CheckingInfoDto savedChecking = checkingInfoService.create(checkingInfoDto);
        log.info("Created CheckingInfo with ID: {}", savedChecking.getId());
        log.info("Assigned stockunit {} to CheckingInfo with ID: {}", savedStockUnit.getId(), savedChecking.getId());

        grnItemService.addCheckingInfo(grnItemId, savedChecking.getId());


        grnItem = grnItemService.updateGrnItem(grnItemId, grnItem);
        log.info("Updated GrnItem with ID: {}", grnItem.getId());

        putawayService.checkAssignedQuantity(grnItem);

        return grnItem;
    }
}
