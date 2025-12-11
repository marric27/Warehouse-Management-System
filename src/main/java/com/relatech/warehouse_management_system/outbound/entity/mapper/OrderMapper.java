package com.relatech.warehouse_management_system.outbound.entity.mapper;

import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.outbound.entity.Order;
import com.relatech.warehouse_management_system.outbound.entity.SalesOrderLine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDto toDto(Order entity) {
        if (entity == null) return null;

        List<SalesOrderLine> lines = entity.getSalesOrderLineList();
        return OrderDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .date(entity.getDate())
                .customerCode(entity.getCustomerCode())
                .state(entity.getState())
                .salesOrderLineList(lines != null ?
                        SalesOrderLineMapper.toDtoList(lines) : null)
                .build();
    }

    public static Order toEntity(OrderDto dto) {
        if (dto == null) return null;

        List<SalesOrderLine> lines = dto.getSalesOrderLineList() != null ?
                SalesOrderLineMapper.toEntityList(dto.getSalesOrderLineList()) : new ArrayList<>();

        Order order = Order.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .date(dto.getDate())
                .customerCode(dto.getCustomerCode())
                .state(dto.getState())
                .salesOrderLineList(lines)
                .build();

        lines.forEach(line -> line.setOrder(order));

        return order;
    }

}
