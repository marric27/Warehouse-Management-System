package com.relatech.warehouse_management_system.goodsIn.entity.mapper;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnDto;
import com.relatech.warehouse_management_system.goodsIn.entity.Grn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GrnMapper {

    private final GrnItemMapper grnItemMapper;


    public GrnDto toDto(Grn entity) {
        if (entity == null) {
            log.warn("Attempting to map null GRN entity to DTO");
            return null;
        }
        return GrnDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .supplier(entity.getSupplier())
                .receivingDate(entity.getReceivingDate())
                .state(entity.getState() != null ? entity.getState() : null)
                .items(grnItemMapper.toDto(entity.getItems()))
                .build();
    }


    public Grn toEntity(GrnDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("GrnDTO cannot be null");
        }
        return Grn.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .supplier(dto.getSupplier())
                .receivingDate(dto.getReceivingDate())
                .state(dto.getState())
                .items(grnItemMapper.toEntity(dto.getItems()))
                .build();
    }
}
