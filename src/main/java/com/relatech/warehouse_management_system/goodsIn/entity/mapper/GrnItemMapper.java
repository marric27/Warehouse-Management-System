package com.relatech.warehouse_management_system.goodsIn.entity.mapper;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GrnItemMapper {


    public GrnItemDto toDto(GrnItem grnItem) {
        if (grnItem == null) return null;
        return GrnItemDto.builder()
                .id(grnItem.getId())
                .code(grnItem.getCode())
                .productCode(grnItem.getProductCode())
                .expectedQty(grnItem.getExpectedQty())
                .receivedQty(grnItem.getReceivedQty())
                .compliantQty(grnItem.getCompliantQty())
                .notCompliantQty(grnItem.getNotCompliantQty())
                .state(grnItem.getState())
                .checkingInfoList(grnItem.getCheckingInfoList())
                .build();
    }


    public GrnItem toEntity(GrnItemDto dto) {
        if (dto == null) return null;
        return GrnItem.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .productCode(dto.getProductCode())
                .expectedQty(dto.getExpectedQty())
                .receivedQty(dto.getReceivedQty())
                .compliantQty(dto.getCompliantQty())
                .notCompliantQty(dto.getNotCompliantQty())
                .state(dto.getState())
                .build();
    }


    public List<GrnItemDto> toDto(List<GrnItem> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream().map(this::toDto).toList();
    }

    public List<GrnItem> toEntity(List<GrnItemDto> dtos) {
        if (dtos == null || dtos.isEmpty()) return List.of();
        return dtos.stream().map(this::toEntity).toList();
    }

    // STATICI opzionali (retrocompatibilità)
    public static GrnItemDto toDtoStatic(GrnItem grnItem) { return new GrnItemMapper().toDto(grnItem); }
    public static GrnItem toEntityStatic(GrnItemDto dto) { return new GrnItemMapper().toEntity(dto); }
}
