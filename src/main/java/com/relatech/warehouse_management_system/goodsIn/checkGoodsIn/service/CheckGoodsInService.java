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

// obiettivo: creazione stockunit relative ad item e checkinginfo

// CREO STOCKUNIT E CHECKING INFO VENGONO CREATE INSIEME

// FUNZIONI DI ASSEGNAZIONE DI STOCK UNIT E CHECKING INFO A GRNITEM

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

        //grnItem.getCheckingInfoList().add(savedChecking);
        grnItemService.addCheckingInfo(grnItemId, savedChecking.getId());


        // TODO check assigned quantity
        grnItem = grnItemService.updateGrnItem(grnItemId, grnItem);
        log.info("Updated GrnItem with ID: {}", grnItem.getId());

        // 6. Ora posso verificare quantità e stati
        putawayService.checkAssignedQuantity(grnItem);

        return grnItem;
    }




    // 4. aggiorno quantita grn item TODO cos'è?
    //grnItem.setReceivedQty(grnItem.getReceivedQty() + savedChecking.getQuantity());

//        if (savedChecking.getState() == State.OPEN) {
//            grnItem.setCompliantQty(grnItem.getCompliantQty() + savedChecking.getQuantity());
//        } else {
//            grnItem.setNotCompliantQty(grnItem.getNotCompliantQty() + savedChecking.getQuantity());
//        }



}
