package com.relatech.warehouse_management_system.outbound.entity.mapper;

import com.relatech.warehouse_management_system.outbound.dto.SalesOrderLineDto;
import com.relatech.warehouse_management_system.outbound.entity.SalesOrderLine;

import java.util.List;

public class SalesOrderLineMapper {

    public static SalesOrderLineDto toDto(SalesOrderLine entity) {
        if (entity == null) return null;

        return SalesOrderLineDto.builder()
                .id(entity.getId())
                .salesOrderNumber(entity.getSalesOrderNumber())
                .productCode(entity.getProductCode())
                .quantity(entity.getQuantity())
                .status(entity.getStatus())
                .orderId(entity.getOrder() != null ? entity.getOrder().getId() : null)
                .build();
    }

    public static SalesOrderLine toEntity(SalesOrderLineDto dto) {
        if (dto == null) return null;

        return SalesOrderLine.builder()
                .id(dto.getId())
                .productCode(dto.getProductCode())
                .salesOrderNumber(dto.getSalesOrderNumber())
                .quantity(dto.getQuantity())
                .status(dto.getStatus())
                .build();
    }

    public static List<SalesOrderLineDto> toDtoList(List<SalesOrderLine> entities) {
        return entities.stream().map(SalesOrderLineMapper::toDto).toList();
    }

    public static List<SalesOrderLine> toEntityList(List<SalesOrderLineDto> dtos) {
        return dtos.stream().map(SalesOrderLineMapper::toEntity).toList();
    }
}