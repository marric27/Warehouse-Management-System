package com.relatech.warehouse_management_system.stockUnit.mapper;



import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.stockUnit.entity.StockUnit;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

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
                .productCategory(entity.getProductCategory())
                .slotIds(entity.getSlots() != null ?
                        entity.getSlots().stream().map(Slot::getId).collect(Collectors.toSet())
                        : null)
                .build();
    }

    public StockUnit toEntity(StockUnitDTO dto, Set<Slot> slots) {
        return StockUnit.builder()
                .id(dto.getId())
                .batchNumber(dto.getBatchNumber())
                .expirationDate(dto.getExpirationDate())
                .productCode(dto.getProductCode())
                .uniqueCode(dto.getUniqueCode())
                .quantity(dto.getQuantity())
                .productCategory(dto.getProductCategory())
                .slots(slots)
                .build();
    }
}

