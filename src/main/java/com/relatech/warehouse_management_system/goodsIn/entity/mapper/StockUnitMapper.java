package com.relatech.warehouse_management_system.goodsIn.entity.mapper;

import com.relatech.warehouse_management_system.product.mapper.ProductMapper;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
import org.springframework.stereotype.Component;
import java.util.List;

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
                .productDto(entity.getProduct() != null ? ProductMapper.toDto(entity.getProduct()) : null)
                .build();
    }

    public StockUnit toEntity(StockUnitDTO dto) {
        return StockUnit.builder()
                .id(dto.getId())
                .batchNumber(dto.getBatchNumber())
                .expirationDate(dto.getExpirationDate())
                .productCode(dto.getProductCode())
                .uniqueCode(dto.getUniqueCode())
                .quantity(dto.getQuantity())
                .category(dto.getCategory())
                .product(dto.getProductDto() != null ? ProductMapper.toEntity(dto.getProductDto()) : null)
                .build();
    }

    public List<StockUnitDTO> toDTO(List<StockUnit> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::toDTO)
                .toList();
    }

    public List<StockUnit> toEntity(List<StockUnitDTO> dtos) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(this::toEntity)
                .toList();
    }
}

