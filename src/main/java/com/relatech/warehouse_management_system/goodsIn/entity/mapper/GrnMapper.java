package com.relatech.warehouse_management_system.goodsIn.entity.mapper;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.GRN;
import com.relatech.warehouse_management_system.common.util.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Slf4j
@RequiredArgsConstructor
public class GrnMapper {

    private final GrnItemMapper grnItemMapper;  //


    public GrnDTO toDto(GRN entity) {
        if (entity == null) {
            log.warn("Attempting to map null GRN entity to DTO");
            return null;
        }
        return GrnDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .supplier(entity.getSupplier())
                .receivingDate(entity.getReceivingDate())
                .state(entity.getState() != null ? entity.getState() : null)
                .items(entity.getItems() != null ?
                        new ArrayList<>(grnItemMapper.toDto(entity.getItems())) :
                        new ArrayList<>())
                .build();
    }


    public GRN toEntity(GrnDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("GrnDTO cannot be null");
        }
        return GRN.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .supplier(dto.getSupplier())
                .receivingDate(dto.getReceivingDate())
                .state(State.OPEN) // Default state on creation
                .items(dto.getItems() != null ?
                        new ArrayList<>(grnItemMapper.toEntity(dto.getItems())) :
                        new ArrayList<>())
                .build();
    }


    public static GrnDTO toDtoStatic(GRN entity) {
        return new GrnMapper(null).toDto(entity);
    }
    public static GRN toEntityStatic(GrnDTO dto) {
        return new GrnMapper(null).toEntity(dto);
    }
}
