package com.relatech.warehouse_management_system.outbound.entity.mapper;

import com.relatech.warehouse_management_system.outbound.dto.SalesOrderLineDto;
import com.relatech.warehouse_management_system.outbound.entity.SalesOrderLine;

import java.util.List;
import java.util.stream.Collectors;

public class SalesOrderLineMapper {

    public static SalesOrderLineDto toDto(SalesOrderLine entity) {
        if (entity == null) return null;

        return SalesOrderLineDto.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .status(entity.getStatus())
                .build();
    }

    public static SalesOrderLine toEntity(SalesOrderLineDto dto) {
        if (dto == null) return null;

        return SalesOrderLine.builder()
                .id(dto.getId())
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .status(dto.getStatus())
                .build();
    }

    public static List<SalesOrderLineDto> toDtoList(List<SalesOrderLine> entities) {
        return entities.stream().map(SalesOrderLineMapper::toDto).collect(Collectors.toList());
    }

    public static List<SalesOrderLine> toEntityList(List<SalesOrderLineDto> dtos) {
        return dtos.stream().map(SalesOrderLineMapper::toEntity).collect(Collectors.toList());
    }
}