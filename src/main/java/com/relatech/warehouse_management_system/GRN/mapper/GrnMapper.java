package com.relatech.warehouse_management_system.GRN.mapper;

import com.relatech.warehouse_management_system.GRN.dto.GrnDTO;
import com.relatech.warehouse_management_system.GRN.entity.GRN;
import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;
import com.relatech.warehouse_management_system.grnItem.entity.GrnItem;
import com.relatech.warehouse_management_system.util.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class GrnMapper {

    public GrnDTO toDto(GRN entity) {
        if (entity == null) {
            log.warn("Attempting to map null GRN entity to DTO");
            return null;
        }
        return GrnDTO.builder()
                .id(entity.getId())
                .supplier(entity.getSupplier())
                .receivingDate(entity.getReceivingDate())
                .state(entity.getState() != null ? entity.getState().name() : null)
                .items(toItemsDtoList(entity.getItems()))
                .build();
    }

    public GRN toEntity(GrnDTO dto) {
        if (dto == null) {
            log.warn("Attempting to map null GrnDTO to entity");
            return null;
        }
        GRN grn = GRN.builder()
                .id(dto.getId())
                .supplier(dto.getSupplier())
                .receivingDate(dto.getReceivingDate())
                .state(State.OPEN) // Default state on creation
                .items(toItemsEntityList(dto.getItems()))
                .build();

        // Set back-reference for cascade
        if (grn.getItems() != null) {
            grn.getItems().forEach(item -> item.setGrn(grn));
        }
        return grn;
    }

    private List<GrnItemDto> toItemsDtoList(List<GrnItem> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }

    private List<GrnItem> toItemsEntityList(List<GrnItemDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        return dtos.stream()
                .map(this::toItemEntity)
                .collect(Collectors.toList());
    }

    public GrnItemDto toItemDto(GrnItem entity) {
        if (entity == null) {
            log.warn("Attempting to map null GrnItem entity to DTO");
            return null;
        }
        return GrnItemDto.builder()
                .id(entity.getId())
                .productCode(entity.getProductCode())
                .expectedQty(entity.getExpectedQty())
                .receivedQty(entity.getReceivedQty())
                .compliantQty(entity.getCompliantQty())
                .notCompliantQty(entity.getNotCompliantQty())
                .state(entity.getState() != null ? entity.getState() : null)
                .build();
    }

    public GrnItem toItemEntity(GrnItemDto dto) {
        if (dto == null) {
            log.warn("Attempting to map null GrnItemDto to entity");
            return null;
        }
        return GrnItem.builder()
                .id(dto.getId())
                .productCode(dto.getProductCode())
                .expectedQty(dto.getExpectedQty())
                .receivedQty(dto.getReceivedQty())
                .compliantQty(dto.getCompliantQty())
                .notCompliantQty(dto.getNotCompliantQty())
                .state(State.OPEN)
                .build();
    }
}