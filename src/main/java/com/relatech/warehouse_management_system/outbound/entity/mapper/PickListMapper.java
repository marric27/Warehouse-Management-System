package com.relatech.warehouse_management_system.outbound.entity.mapper;

import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.entity.PickList;
import com.relatech.warehouse_management_system.outbound.entity.PickListItem;

import java.util.stream.Collectors;

public class PickListMapper {

    public static PickList toEntity(PickListDto dto) {
        if (dto == null) return null;

        PickList pickList = PickList.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .releaseNumber(dto.getReleaseNumber())
                .customerCode(dto.getCustomerCode())
                .build();

        if (dto.getPickListItemList() != null) {
            dto.getPickListItemList().forEach(itemDto -> {
                PickListItem item = PickListItem.builder()
                        .id(itemDto.getId())
                        .code(itemDto.getCode())
                        .productCode(itemDto.getProductCode())
                        .state(itemDto.getState())
                        .qty(itemDto.getQuantity())
                        .pickedQty(itemDto.getPickedQty())
                        .pickingSequence(itemDto.getPickingSequence())
                        .errorReason(itemDto.getErrorReason())
                        .slotCode(itemDto.getSlotCode())
                        .salesOrderCode(itemDto.getSalesOrderCode())
                        .salesOrderLineNumber(itemDto.getSalesOrderLineNumber())
                        .pickList(pickList)
                        .build();
                pickList.getPickListItemList().add(item);
            });
        }

        return pickList;
    }

    public static PickListDto toDto(PickList entity) {
        if (entity == null) return null;

        PickListDto dto = PickListDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .releaseNumber(entity.getReleaseNumber())
                .customerCode(entity.getCustomerCode())
                .build();

        if (entity.getPickListItemList() != null) {
            dto.setPickListItemList(entity.getPickListItemList()
                    .stream()
                    .map(PickListMapper::toItemDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static PickListItemDto toItemDto(PickListItem item) {
        if (item == null) return null;

        return PickListItemDto.builder()
                .id(item.getId())
                .code(item.getCode())
                .productCode(item.getProductCode())
                .state(item.getState())
                .quantity(item.getQty())
                .pickedQty(item.getPickedQty())
                .pickingSequence(item.getPickingSequence())
                .errorReason(item.getErrorReason())
                .slotCode(item.getSlotCode())
                .salesOrderCode(item.getSalesOrderCode())
                .salesOrderLineNumber(item.getSalesOrderLineNumber())
                .build();
    }
}
