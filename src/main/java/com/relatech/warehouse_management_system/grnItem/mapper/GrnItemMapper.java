package com.relatech.warehouse_management_system.grnItem.mapper;

import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;
import com.relatech.warehouse_management_system.grnItem.entity.GrnItem;

import java.util.Collections;
import java.util.List;

public class GrnItemMapper {

    public static GrnItemDto toDto(GrnItem grnItem) {
        if (grnItem == null) {
            return null;
        }

        return GrnItemDto.builder()
                .id(grnItem.getId())
                .productCode(grnItem.getProductCode())
                .expectedQty(grnItem.getExpectedQty())
                .receivedQty(grnItem.getReceivedQty())
                .compliantQty(grnItem.getCompliantQty())
                .notCompliantQty(grnItem.getNotCompliantQty())
                .state(grnItem.getState())
                .checkingInfoList(grnItem.getCheckingInfoList())
                .build();
    }

    public static GrnItem toEntity(GrnItemDto grnItemDto) {
        if (grnItemDto == null) {
            return null;
        }

        GrnItem grnItem = new GrnItem();
        grnItem.setId(grnItemDto.getId());
        grnItem.setProductCode(grnItemDto.getProductCode());
        grnItem.setExpectedQty(grnItemDto.getExpectedQty());
        grnItem.setReceivedQty(grnItemDto.getReceivedQty());
        grnItem.setCompliantQty(grnItemDto.getCompliantQty());
        grnItem.setNotCompliantQty(grnItemDto.getNotCompliantQty());
        grnItem.setState(grnItemDto.getState());
        grnItem.setCheckingInfoList(grnItemDto.getCheckingInfoList());

        return grnItem;
    }

    public static List<GrnItemDto> toDto(List<GrnItem> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(GrnItemMapper::toDto).toList();
    }

    public static List<GrnItem> toEntity(List<GrnItemDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        return dtos.stream()
                .map(GrnItemMapper::toEntity).toList();
    }
}
