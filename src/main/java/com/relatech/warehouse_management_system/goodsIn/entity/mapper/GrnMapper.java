package com.relatech.warehouse_management_system.goodsIn.entity.mapper;

import com.relatech.warehouse_management_system.goodsIn.entity.dto.GrnDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.GRN;
import com.relatech.warehouse_management_system.util.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Component
@Slf4j
public class GrnMapper {

    public static GrnDTO toDto(GRN entity) {
        if (entity == null) {
            log.warn("Attempting to map null GRN entity to DTO");
            return null;
        }
        return GrnDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .supplier(entity.getSupplier())
                .receivingDate(entity.getReceivingDate())
                .state(entity.getState() != null ? entity.getState().name() : null)
                .items(GrnItemMapper.toDto(entity.getItems()))
                .build();
    }

    public static GRN toEntity(GrnDTO dto) {
        if (dto == null) {
            log.warn("Attempting to map null GrnDTO to entity");
            return null;
        }
        return GRN.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .supplier(dto.getSupplier())
                .receivingDate(dto.getReceivingDate())
                .state(State.OPEN) // Default state on creation
                .items(GrnItemMapper.toEntity(dto.getItems()))
                .build();
    }



}