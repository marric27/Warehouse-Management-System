package com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.service;

import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.CheckingInfoMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.StockUnitMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.service.CheckingInfoService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnItemService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
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

    private final CheckingInfoMapper checkingInfoMapper;
    private final StockUnitMapper stockUnitMapper;
    private final GrnItemMapper grnItemMapper;

    @Transactional
    public GrnItemDto createCheckingInfoWithStockUnitAndAssignToGrnItem(CheckingInfoDto checkingInfoDto, StockUnitDTO stockUnitDTO, Long grnItemId) throws Exception {

        // recupero la grnitem esistente
        // gli assegno la checking info
        // assegno la ci a item

        // salvo tutto


        // 1. recupero grn item esistente
        GrnItemDto grnItem = grnItemService.getGrnItemById(grnItemId);

        // 2. creo stock unit
        StockUnitDTO savedStockUnit = stockUnitService.createStockUnit(stockUnitDTO);

        // 3. preparo checking info
        checkingInfoDto.setGrnItemId(grnItemId);
        checkingInfoDto.setStockUnitId(savedStockUnit.getId());

        CheckingInfoDto savedChecking = checkingInfoService.create(checkingInfoDto);

        // 4. aggiorno quantita grn item
        grnItem.setReceivedQty(grnItem.getReceivedQty() + savedChecking.getQuantity());

        if (savedChecking.getState() == State.OPEN) {
            grnItem.setCompliantQty(grnItem.getCompliantQty() + savedChecking.getQuantity());
        } else {
            grnItem.setNotCompliantQty(grnItem.getNotCompliantQty() + savedChecking.getQuantity());
        }

        GrnItemDto updated = grnItemService.updateGrnItem(grnItemId, grnItem);

        log.info(String.valueOf(updated));

        // 5. ritorno DTO
        return (updated);
    }




}
