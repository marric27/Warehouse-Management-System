package com.relatech.warehouse_management_system.stockUnit.mapper;



import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.stockUnit.entity.StockUnit;


import org.springframework.stereotype.Component;

@Component
public class StockUnitMapper {

    public StockUnitDTO toDTO(StockUnit entity) {
        return StockUnitDTO.builder()
                .id(entity.getId())
                .batchNumber(entity.getBatchNumber())
                .expirationDate(entity.getExpirationDate())
                .productCode(entity.getProductCode())
                .uniqueCode(entity.getUniqueCode())
                .quantity(entity.getQuantity())
                .category(entity.getCategory())
                .slotId(entity.getSlot() != null ? entity.getSlot().getId() : null)
                .build();
    }


    public StockUnit toEntity(StockUnitDTO dto, Slot slot) {
        return StockUnit.builder()
                .id(dto.getId())
                .batchNumber(dto.getBatchNumber())
                .expirationDate(dto.getExpirationDate())
                .productCode(dto.getProductCode())
                .uniqueCode(dto.getUniqueCode())
                .quantity(dto.getQuantity())
                .category(dto.getCategory())
                .slot(slot)
                .build();
    }
}

