package com.relatech.warehouse_management_system.stockUnit.mapper;

import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.stockUnit.entity.StockUnit;


import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockUnitMapper {

    public static StockUnitDTO toDTO(StockUnit entity) {
        return StockUnitDTO.builder()
                .id(entity.getId())
                .batchNumber(entity.getBatchNumber())
                .expirationDate(entity.getExpirationDate())
                .productCode(entity.getProductCode())
                .uniqueCode(entity.getUniqueCode())
                .quantity(entity.getQuantity())
                .category(entity.getCategory())
                .product(entity.getSlot() != null ? entity.getProduct() : null)
                .build();
    }

    public static StockUnit toEntity(StockUnitDTO dto) {
        return StockUnit.builder()
                .id(dto.getId())
                .batchNumber(dto.getBatchNumber())
                .expirationDate(dto.getExpirationDate())
                .productCode(dto.getProductCode())
                .uniqueCode(dto.getUniqueCode())
                .quantity(dto.getQuantity())
                .category(dto.getCategory())
                .product(dto.getProduct() != null ? dto.getProduct() : null)
                .build();
    }

    public static List<StockUnitDTO> toDTO(List<StockUnit> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(StockUnitMapper::toDTO)
                .toList();
    }

    public static List<StockUnit> toEntity(List<StockUnitDTO> dtos) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(StockUnitMapper::toEntity)
                .toList();
    }
}

