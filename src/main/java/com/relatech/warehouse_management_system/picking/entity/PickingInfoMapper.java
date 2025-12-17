package com.relatech.warehouse_management_system.picking.entity;

public class PickingInfoMapper {

    public static PickingInfoDto toDto(PickingInfo entity) {
        if (entity == null) {
            return null;
        }

        return PickingInfoDto.builder()
                .id(entity.getId())
                .timestamp(entity.getTimestamp())
                .user(entity.getUser())
                .stockUnitCode(entity.getStockUnitCode())
                .batchNumber(entity.getBatchNumber())
                .expirationDate(entity.getExpirationDate())
                .quantity(entity.getQuantity())
                .build();
    }

    public static PickingInfo toEntity(PickingInfoDto dto) {
        if (dto == null) {
            return null;
        }

        return PickingInfo.builder()
                .id(dto.getId())
                .timestamp(dto.getTimestamp())
                .user(dto.getUser())
                .stockUnitCode(dto.getStockUnitCode())
                .batchNumber(dto.getBatchNumber())
                .expirationDate(dto.getExpirationDate())
                .quantity(dto.getQuantity())
                .build();
    }
}
