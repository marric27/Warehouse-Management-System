package com.relatech.warehouse_management_system.goodsIn.entity.mapper;

import com.relatech.warehouse_management_system.product.mapper.ProductMapper;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
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
                .productDto(entity.getProduct() != null ? ProductMapper.toDto(entity.getProduct()) : null)
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
                .product(dto.getProductDto() != null ? ProductMapper.toEntity(dto.getProductDto()) : null)
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

