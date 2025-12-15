package com.relatech.warehouse_management_system.outbound.release.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.dto.SalesOrderLineDto;
import com.relatech.warehouse_management_system.outbound.entity.PickList;
import com.relatech.warehouse_management_system.outbound.entity.mapper.PickListMapper;
import com.relatech.warehouse_management_system.outbound.entity.service.OrderService;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import com.relatech.warehouse_management_system.warehouse.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PickListGen {

    private final OrderService orderService;
    private final PickListService pickListService;
    private final SlotService slotService;

    @Transactional
    public List<PickListDto> generatePickLists(List<Long> orderIds) throws ResourceNotFoundException {

        Map<String, PickListDto> pickListMap = new HashMap<>();

        for (Long orderId : orderIds) {
            OrderDto orderDto = orderService.getOrderById(orderId);

            // Se non esiste ancora una PickList per questo cliente, creala
            PickListDto pickListDTO = pickListMap.computeIfAbsent(orderDto.getCustomerCode(), customerCode ->
                    PickListDto.builder()
                            .customerCode(customerCode)
                            .pickListItemList(new ArrayList<>())
                            .build()
            );// TODO dati ordini, dividi per cust, per ogni entry cust creo la pick list, gen code uuid alla picklist relativo al rilascio

            for (SalesOrderLineDto line : orderDto.getSalesOrderLineList()) {

                String productCode = String.valueOf(line.getProductCode());

                String slotCode = slotService.getSlotContainingProduct(line.getProductCode(), line.getQuantity())
                        .orElseThrow(() -> new RuntimeException("No slot found for product " + line.getProductCode() + " with required quantity " + line.getQuantity()))
                        .getCode();

                PickListItemDto itemDTO = PickListItemDto.builder()
                        .productCode(productCode)
                        .quantity(line.getQuantity())
                        .slotCode(slotCode)
                        .salesOrderCode(orderDto.getCode())
                        .salesOrderLineNumber(line.getSalesOrderNumber())
                        .build();

                pickListDTO.getPickListItemList().add(itemDTO);
            }
        }

        List<PickListDto> result = new ArrayList<>();
        for (PickListDto dto : pickListMap.values()) {
            PickList pickListEntity = PickListMapper.toEntity(dto);
            pickListService.create(dto);
            result.add(PickListMapper.toDto(pickListEntity));
            log.info("Generated PickListEntity with {} items for customer {}",
                    pickListEntity.getPickListItemList().size(),
                    dto.getCustomerCode());
        }

        return result;
    }


    @Transactional
    public PickListDto generatePickList(Long orderId) throws ResourceNotFoundException {

        OrderDto orderDto = orderService.getOrderById(orderId);

        PickListDto pickListDTO = PickListDto.builder()
                .customerCode(orderDto.getCustomerCode())
                .build();

        for (SalesOrderLineDto line : orderDto.getSalesOrderLineList()) {

            String productCode = String.valueOf(line.getProductCode());
            String slotCode = slotService.getSlotContainingProduct(line.getProductCode(), line.getQuantity())
                    .orElseThrow(() -> new RuntimeException("No slot found for product " + line.getProductCode() + " with required quantity " + line.getQuantity()))
                    .getCode();

            PickListItemDto itemDTO = PickListItemDto.builder()
                    .productCode(productCode)
                    .quantity(line.getQuantity())
                    .slotCode(slotCode)
                    .salesOrderCode(orderDto.getCode())
                    .salesOrderLineNumber(line.getSalesOrderNumber())
                    .build();

            pickListDTO.getPickListItemList().add(itemDTO);
        }

        PickList pickListEntity = PickListMapper.toEntity(pickListDTO);

        log.info("Generated PickListEntity with {} items for order {}",
                pickListEntity.getPickListItemList().size(),
                orderDto.getCode());

        pickListService.create(pickListDTO);

        return PickListMapper.toDto(pickListEntity);
    }
}
