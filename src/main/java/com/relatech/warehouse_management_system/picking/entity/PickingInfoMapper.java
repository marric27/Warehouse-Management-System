package com.relatech.warehouse_management_system.picking.entity;

import com.relatech.warehouse_management_system.outbound.entity.PickListItem;

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
                .pickListItemId(entity.getPickListItem().getId())
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
                .pickListItem(PickListItem.builder().id(dto.getPickListItemId()).build())
                .build();
    }
}
