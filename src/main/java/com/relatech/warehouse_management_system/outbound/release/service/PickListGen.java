package com.relatech.warehouse_management_system.outbound.release.service;

import com.github.f4b6a3.ulid.UlidCreator;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.OrderState;
import com.relatech.warehouse_management_system.common.util.PickListItemState;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.dto.SalesOrderLineDto;
import com.relatech.warehouse_management_system.outbound.entity.PickList;
import com.relatech.warehouse_management_system.outbound.entity.mapper.PickListMapper;
import com.relatech.warehouse_management_system.outbound.entity.service.OrderService;
import com.relatech.warehouse_management_system.outbound.entity.service.PickListService;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
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

        List<OrderDto> ordersOpen = orderService.getOrdersByStateInIds(OrderState.OPEN, orderIds);

        Map<String, PickListDto> pickListMap = new HashMap<>();

        String ulid = UlidCreator.getUlid().toString();
        String releaseNumber = "RLS-" + ulid.substring(0, 10).toUpperCase();

        for (OrderDto orderDto : ordersOpen) {
            PickListDto pickListDTO = pickListMap.computeIfAbsent(orderDto.getCustomerCode(), customerCode ->
                    PickListDto.builder()
                            .customerCode(customerCode)
                            .releaseNumber(releaseNumber)
                            .pickListItemList(new ArrayList<>())
                            .build()
            );

            for (SalesOrderLineDto line : orderDto.getSalesOrderLineList()) {

                String productCode = String.valueOf(line.getProductCode());

                SlotDto slot = slotService.getSlotContainingProduct(line.getProductCode(), line.getQuantity())
                        .orElseThrow(() -> new RuntimeException("No slot found for product " + line.getProductCode() + " with required quantity " + line.getQuantity()));

                PickListItemDto itemDTO = PickListItemDto.builder()
                        .productCode(productCode)
                        .state(PickListItemState.OPEN)
                        .quantity(line.getQuantity())
                        .pickedQty(0)
                        .pickingSequence(slot.getPickingSequence())
                        .slotCode(slot.getCode())
                        .salesOrderCode(orderDto.getCode())
                        .salesOrderLineNumber(line.getSalesOrderLineNumber())
                        .build();

                pickListDTO.getPickListItemList().add(itemDTO);
                orderService.updateOrderState(orderDto.getId(), OrderState.PICKING);
            }
        }

        List<PickListDto> result = new ArrayList<>();
        for (PickListDto dto : pickListMap.values()) {
            PickList pickListEntity = PickListMapper.toEntity(dto);
            pickListService.create(dto);
            result.add(PickListMapper.toDto(pickListEntity));
            log.info("Generated PickList with {} items for customer {}",
                    pickListEntity.getPickListItemList().size(),
                    dto.getCustomerCode());
        }

        return result;
    }
}
