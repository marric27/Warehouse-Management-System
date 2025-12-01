package com.relatech.warehouse_management_system.goodsIn.checkingInfo.mapper;

import com.relatech.warehouse_management_system.goodsIn.checkingInfo.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.checkingInfo.entity.CheckingInfo;
import org.springframework.stereotype.Component;

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
                .grnItemId(entity.getGrnItem() != null ? entity.getGrnItem().getId() : null)
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
        return ci;
    }
}
