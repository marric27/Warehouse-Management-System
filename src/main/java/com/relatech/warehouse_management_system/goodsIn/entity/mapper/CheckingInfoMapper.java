package com.relatech.warehouse_management_system.goodsIn.entity.mapper;

import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.entity.CheckingInfo;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CheckingInfoMapper {

    public static CheckingInfoDto toDto(CheckingInfo entity) {
        if (entity == null) return null;

        return CheckingInfoDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .batchNumber(entity.getBatchNumber())
                .expirationDate(entity.getExpirationDate())
                .quantity(entity.getQuantity())
                .state(entity.getState())
                .stockUnitId(entity.getStockUnitId())
                .grnItemId(entity.getGrnItem().getId())
                .build();
    }

    public static CheckingInfo toEntity(CheckingInfoDto dto) {
        if (dto == null) return null;

        CheckingInfo ci = new CheckingInfo();
        ci.setId(dto.getId());
        ci.setCode(dto.getCode());
        ci.setBatchNumber(dto.getBatchNumber());
        ci.setExpirationDate(dto.getExpirationDate());
        ci.setQuantity(dto.getQuantity());
        ci.setState(dto.getState());
        ci.setStockUnitId(dto.getStockUnitId());
        if (dto.getGrnItemId() != null) {
            GrnItem grnItem = new GrnItem();
            grnItem.setId(dto.getGrnItemId());
            ci.setGrnItem(grnItem);
        }
        return ci;
    }

    public static List<CheckingInfoDto> toDtoList(List<CheckingInfo> list) {
        if (list == null) return new ArrayList<>();
        return list.stream()
                .map(CheckingInfoMapper::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
