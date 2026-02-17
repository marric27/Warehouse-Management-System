package com.relatech.warehouse_management_system.goodsIn.entity.mapper;

import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
import com.relatech.warehouse_management_system.product.mapper.ProductMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockUnitMapper {

    public StockUnitDto toDTO(StockUnit entity) {
        return StockUnitDto.builder()
                .id(entity.getId())
                .batchNumber(entity.getBatchNumber())
                .expirationDate(entity.getExpirationDate())
                .productCode(entity.getProductCode())
                .code(entity.getCode())
                .quantity(entity.getQuantity())
                .category(entity.getCategory())
                .slotId(entity.getSlot() != null ? entity.getSlot().getId() : null)
                .build();
    }

    public StockUnit toEntity(StockUnitDto dto) {
        return StockUnit.builder()
                .id(dto.getId())
                .batchNumber(dto.getBatchNumber())
                .expirationDate(dto.getExpirationDate())
                .productCode(dto.getProductCode())
                .code(dto.getCode())
                .quantity(dto.getQuantity())
                .category(dto.getCategory())
                .build();
    }

    public List<StockUnitDto> toDTO(List<StockUnit> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(this::toDTO)
                .toList();
    }

    public List<StockUnit> toEntity(List<StockUnitDto> dtos) {
        if (dtos == null) return List.of();
        return dtos.stream()
                .map(this::toEntity)
                .toList();
    }
}

