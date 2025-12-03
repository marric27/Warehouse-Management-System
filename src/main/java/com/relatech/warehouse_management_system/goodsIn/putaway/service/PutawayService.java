package com.relatech.warehouse_management_system.goodsIn.putaway.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.dto.SlotDTO;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.service.SlotService;
import com.relatech.warehouse_management_system.goodsIn.entity.service.StockUnitService;
import com.relatech.warehouse_management_system.goodsIn.exception.UpdateEntityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PutawayService {
    // obiettivo: stoccaggio dei prodotti in magazzino

    // ASSEGNAZIONE DI STOCK UNIT A SLOT

    // AGGIORNAMENTO DI STATI OPEN -> PUTAWAY

    private final SlotService slotService;
    private final StockUnitService stockUnitService;

    @Transactional
    public SlotDTO assignStockUnitToSlot(Long stockUnitId, Long slotId) throws ResourceNotFoundException, UpdateEntityException {
        SlotDTO slotDTO = slotService.getSlotById(slotId);
        StockUnitDTO stockUnitDTO = stockUnitService.getStockUnitById(stockUnitId);

        if (!slotDTO.getAllowedCategory().equals(stockUnitDTO.getCategory())) {
            throw new IllegalArgumentException("StockUnit category not allowed in this Slot");
        }

        slotDTO.getStockUnits().add(stockUnitDTO);
        stockUnitDTO.setSlotDto(slotDTO);

        return slotService.updateSlot(slotId, slotDTO);
    }





    //SlotDTO removeStockUnitFromSlot(Long slotId, Long stockUnitId) throws ResourceNotFoundException;


}
